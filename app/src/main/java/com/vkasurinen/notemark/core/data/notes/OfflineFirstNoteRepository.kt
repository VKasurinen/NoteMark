package com.vkasurinen.notemark.core.data.notes

import com.vkasurinen.notemark.core.database.dao.NotePendingSyncDao
import com.vkasurinen.notemark.core.database.entity.DeletedNoteSyncEntity
import com.vkasurinen.notemark.core.database.entity.NotePendingSyncEntity
import com.vkasurinen.notemark.core.domain.notes.*
import com.vkasurinen.notemark.core.domain.util.Result
import com.vkasurinen.notemark.notes.domain.Note
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.launch
import timber.log.Timber

class OfflineFirstNoteRepository(
    private val localDataSource: LocalDataSource,
    private val remoteDataSource: RemoteDataSource,
    private val notePendingSyncDao: NotePendingSyncDao,
    private val syncScheduler: SyncRunScheduler,
    private val applicationScope: CoroutineScope
) : NotesRepository {

    override suspend fun createNote(note: Note): Result<Note> {
        return try {
            // 1. Save to local DB first
            localDataSource.createNote(note)

            // 2. Add to sync queue
            notePendingSyncDao.upsertPendingSyncNote(
                NotePendingSyncEntity(
                    noteId = note.id,
                    syncType = NotePendingSyncEntity.SyncType.CREATE,
                    lastAttempt = System.currentTimeMillis()
                )
            )

            // 3. Schedule sync
            syncScheduler.scheduleSync(SyncRunScheduler.SyncType.CreateNote(note))

            Result.Success(note)
        } catch (e: Exception) {
            Timber.e(e, "Failed to create note locally")
            Result.Error("Failed to create note: ${e.message}")
        }
    }

    override suspend fun updateNote(note: Note): Result<Note> {
        Timber.d("updateNote() - Starting update for note ID: ${note.id}")
        return try {
            // 1. Update local DB first
            Timber.d("updateNote() - Attempting to update note in local database")
            val localUpdateResult = localDataSource.updateNote(note)
            if (localUpdateResult is Result.Error) {
                Timber.e("updateNote() - Local database update failed: ${localUpdateResult.message}")
                return localUpdateResult
            }
            Timber.d("updateNote() - Local database update successful for note ID: ${note.id}")

            // 2. Queue for sync
            Timber.d("updateNote() - Queuing note for sync with ID: ${note.id}")
            notePendingSyncDao.upsertPendingSyncNote(
                NotePendingSyncEntity(
                    noteId = note.id,
                    syncType = NotePendingSyncEntity.SyncType.UPDATE,
                    lastAttempt = System.currentTimeMillis()
                )
            )
            Timber.d("updateNote() - Note successfully queued for sync with ID: ${note.id}")

            // 3. Immediately return the successfully updated local note
            Timber.d("updateNote() - Returning success for note ID: ${note.id}")
            Result.Success(note)

        } catch (e: Exception) {
            Timber.e(e, "updateNote() - Failed to update note locally for ID: ${note.id}")
            Result.Error("Failed to update note: ${e.message}")
        }
    }

    override suspend fun getNotes(page: Int, size: Int): Result<List<Note>> {
        return try {
            // 1. Return local data immediately
            val localNotes = localDataSource.getNotes(page, size)

            // 2. Trigger background sync
            applicationScope.launch {
                when (val remoteResult = remoteDataSource.getNotes(page, size)) {
                    is Result.Success -> {
                        remoteResult.data?.let { remoteNotes ->
                            // Only update notes that aren't currently being edited locally
                            val notesToUpdate = remoteNotes.filter { remoteNote ->
                                notePendingSyncDao.getPendingSyncNote(remoteNote.id) == null
                            }
                            localDataSource.upsertNotes(notesToUpdate)
                        }
                    }

                    is Result.Error -> {
                        Timber.e("Failed to fetch notes from remote: ${remoteResult.message}")
                    }

                    is Result.Loading -> {
                        // Possible loading state handling
                    }
                }
            }

            localNotes
        } catch (e: Exception) {
            Timber.e(e, "Failed to get notes from local DB")
            Result.Error("Failed to get notes: ${e.message}")
        }
    }

    override suspend fun deleteNote(id: String): Result<Unit> {
        return try {
            // 1. Immediately delete from local DB
            localDataSource.deleteNote(id)

            // 2. Check if this was a note that was created offline
            val isPendingSync = notePendingSyncDao.getPendingSyncNote(id) != null
            if (isPendingSync) {
                // If it was never synced, just remove from sync queue
                notePendingSyncDao.deletePendingSyncNote(id)
                return Result.Success(Unit)
            }

            // 3. Add to deleted notes sync queue
            notePendingSyncDao.upsertDeletedNote(
                DeletedNoteSyncEntity(
                    noteId = id,
                    deletedAt = System.currentTimeMillis()
                )
            )

            // 4. Schedule sync
            syncScheduler.scheduleSync(SyncRunScheduler.SyncType.DeleteNote(id))

            Result.Success(Unit)
        } catch (e: Exception) {
            Timber.e(e, "Failed to delete note locally")
            Result.Error("Failed to delete note: ${e.message}")
        }
    }

    override fun observeNotes(): Flow<List<Note>> {
        return localDataSource.observeNotes()
    }


    override suspend fun syncPendingNotes() {
        Timber.d("Starting offline-first sync process")
        try {
            syncPendingCreatesAndUpdates()
            syncPendingDeletes()
        } catch (e: Exception) {
            Timber.e(e, "Sync process failed")
        } finally {
            Timber.d("Sync process completed")
        }
    }

    private suspend fun syncPendingCreatesAndUpdates() {
        val pendingNotes = notePendingSyncDao.getAllPendingSyncNotes()
        pendingNotes.forEach { pendingNote ->
            if (shouldSkipSync(pendingNote)) return@forEach

            when (val freshNote = getFreshNoteForSync(pendingNote.noteId)) {
                null -> handleMissingNote(pendingNote)
                else -> processNoteSync(freshNote, pendingNote)
            }
        }
    }

    private suspend fun shouldSkipSync(pendingNote: NotePendingSyncEntity): Boolean {
        if (pendingNote.retryCount >= pendingNote.maxRetries) {
            Timber.w("Max retries reached for note ${pendingNote.noteId}")
            notePendingSyncDao.deletePendingSyncNote(pendingNote.noteId)
            return true
        }
        return false
    }

    private suspend fun getFreshNoteForSync(noteId: String): Note? {
        return when (val result = localDataSource.getNoteById(noteId)) {
            is Result.Success -> {
                result.data?.also { note ->
                    if (note.content.isBlank()) {
                        Timber.e("Empty content detected for note $noteId")
                    }
                }
            }
            else -> {
                Timber.e("Failed to fetch note $noteId from local DB")
                null
            }
        }
    }

    private suspend fun handleMissingNote(pendingNote: NotePendingSyncEntity) {
        Timber.w("Note ${pendingNote.noteId} not found, removing from sync queue")
        notePendingSyncDao.deletePendingSyncNote(pendingNote.noteId)
    }

    private suspend fun processNoteSync(note: Note, pendingNote: NotePendingSyncEntity) {
        Timber.d("Processing ${pendingNote.syncType} sync for note ${note.id}")
        Timber.d("Local content: '${note.content}'")

        when (pendingNote.syncType) {
            NotePendingSyncEntity.SyncType.CREATE -> processCreateSync(note, pendingNote)
            NotePendingSyncEntity.SyncType.UPDATE -> processUpdateSync(note, pendingNote)
        }
    }

    private suspend fun processCreateSync(note: Note, pendingNote: NotePendingSyncEntity) {
        when (val result = remoteDataSource.postNote(note)) {
            is Result.Success -> {
                result.data?.let { remoteNote ->
                    handleCreateSuccess(note, remoteNote, pendingNote)
                } ?: run {
                    Timber.e("Received null note from server for create operation")
                    handleSyncError(pendingNote, "Server returned null note")
                }
            }
            is Result.Error -> handleSyncError(pendingNote, result.message)
            is Result.Loading -> Timber.d("Create in progress for note ${note.id}")
        }
    }

    private suspend fun processUpdateSync(note: Note, pendingNote: NotePendingSyncEntity) {
        // Add timestamp check to prevent overwriting newer local changes
        val serverNote = when (val result = remoteDataSource.updateNote(note)) {
            is Result.Success -> result.data
            else -> null
        }

        serverNote?.let { remoteNote ->
            // Only update local if server version is newer
            if (remoteNote.lastEditedAt > note.lastEditedAt) {
                localDataSource.updateNote(remoteNote)
            }
            notePendingSyncDao.deletePendingSyncNote(pendingNote.noteId)
        } ?: handleSyncError(pendingNote, "Update failed")
    }

    private suspend fun handleCreateSuccess(
        localNote: Note,
        remoteNote: Note,
        pendingNote: NotePendingSyncEntity
    ) {
        if (isContentMatching(localNote, remoteNote)) {
            Timber.d("Create verified for ${localNote.id}")
            notePendingSyncDao.deletePendingSyncNote(pendingNote.noteId)
            localDataSource.updateNote(localNote.copy(isSynced = true))
        } else {
            handleContentMismatch(localNote, remoteNote, pendingNote)
        }
    }

    private fun isContentMatching(localNote: Note, remoteNote: Note): Boolean {
        return remoteNote.content == localNote.content &&
                remoteNote.title == localNote.title
    }

    private suspend fun handleContentMismatch(
        localNote: Note,
        remoteNote: Note,
        pendingNote: NotePendingSyncEntity
    ) {
        Timber.w("Content mismatch detected for note ${localNote.id}")

        // Implement last-write-wins strategy
        val winningNote = if (localNote.lastEditedAt > remoteNote.lastEditedAt) {
            Timber.d("Local version is newer, keeping it")
            localNote
        } else {
            Timber.d("Remote version is newer, updating local")
            remoteNote
        }

        // Update local with winning version
        localDataSource.updateNote(winningNote)

        // Retry sync if local version was newer
        if (winningNote == localNote) {
            notePendingSyncDao.upsertPendingSyncNote(
                pendingNote.copy(
                    lastAttempt = System.currentTimeMillis(),
                    retryCount = pendingNote.retryCount + 1
                )
            )
        } else {
            notePendingSyncDao.deletePendingSyncNote(pendingNote.noteId)
        }
    }

    private suspend fun handleSyncError(pendingNote: NotePendingSyncEntity, error: String?) {
        Timber.e("Sync failed for ${pendingNote.noteId}: $error")
        notePendingSyncDao.upsertPendingSyncNote(
            pendingNote.copy(
                lastAttempt = System.currentTimeMillis(),
                retryCount = pendingNote.retryCount + 1
            )
        )
    }

    private suspend fun syncPendingDeletes() {
        val pendingDeletes = notePendingSyncDao.getAllDeletedNotes()
        pendingDeletes.forEach { deletedNote ->
            Timber.d("Processing delete for ${deletedNote.noteId}")
            when (val result = remoteDataSource.deleteNote(deletedNote.noteId)) {
                is Result.Success -> {
                    Timber.d("Delete successful for ${deletedNote.noteId}")
                    // Remove from both queues
                    notePendingSyncDao.deleteDeletedNote(deletedNote.noteId)
                    notePendingSyncDao.deletePendingSyncNote(deletedNote.noteId)
                }
                is Result.Error -> {
                    Timber.e("Delete failed for ${deletedNote.noteId}: ${result.message}")

                    // Increment retry count and update
                    val updatedNote = deletedNote.copy(
                        retryCount = deletedNote.retryCount + 1
                    )

                    // Remove if max retries reached, otherwise update
                    if (updatedNote.retryCount >= deletedNote.maxRetries) {
                        Timber.w("Max retries reached for ${deletedNote.noteId}, removing from queue")
                        notePendingSyncDao.deleteDeletedNote(deletedNote.noteId)
                    } else {
                        notePendingSyncDao.upsertDeletedNote(updatedNote)
                    }
                }
                is Result.Loading -> Timber.d("Delete in progress for ${deletedNote.noteId}")
            }
        }
    }

    override suspend fun getNoteById(id: String): Result<Note> {
        return localDataSource.getNoteById(id)
    }
}