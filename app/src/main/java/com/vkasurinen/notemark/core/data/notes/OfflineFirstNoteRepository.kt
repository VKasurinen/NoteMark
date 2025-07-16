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
        return try {
            // 1. Update local DB first
            localDataSource.updateNote(note)

            // 2. Add to sync queue
            notePendingSyncDao.upsertPendingSyncNote(
                NotePendingSyncEntity(
                    noteId = note.id,
                    syncType = NotePendingSyncEntity.SyncType.UPDATE,
                    lastAttempt = System.currentTimeMillis()
                )
            )

            // 3. Schedule sync
            syncScheduler.scheduleSync(SyncRunScheduler.SyncType.CreateNote(note))

            Result.Success(note)
        } catch (e: Exception) {
            Timber.e(e, "Failed to update note locally")
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
                        remoteResult.data?.let { notes ->
                            localDataSource.upsertNotes(notes)
                        }
                    }
                    is Result.Error -> {
                        Timber.e("Failed to fetch notes from remote: ${remoteResult.message}")
                    }
                    is Result.Loading -> {
                        // Handle loading state if needed
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
            // 1. Delete from local DB first
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
        try {
            // Sync pending creates/updates
            val pendingNotes = notePendingSyncDao.getAllPendingSyncNotes()
            pendingNotes.forEach { pendingNote ->
                when (val localNoteResult = localDataSource.getNoteById(pendingNote.noteId)) {
                    is Result.Success -> {
                        val note = localNoteResult.data
                        if (note != null) {
                            when (pendingNote.syncType) {
                                NotePendingSyncEntity.SyncType.CREATE -> {
                                    when (val result = remoteDataSource.postNote(note)) {
                                        is Result.Success -> {
                                            notePendingSyncDao.deletePendingSyncNote(pendingNote.noteId)
                                        }
                                        is Result.Error -> {
                                            // Update last attempt time
                                            notePendingSyncDao.upsertPendingSyncNote(
                                                pendingNote.copy(
                                                    lastAttempt = System.currentTimeMillis()
                                                )
                                            )
                                        }
                                        is Result.Loading -> {
                                            // Just retry next time
                                            Timber.d("Post note loading, will retry")
                                        }
                                    }
                                }
                                NotePendingSyncEntity.SyncType.UPDATE -> {
                                    when (val result = remoteDataSource.updateNote(note)) {
                                        is Result.Success -> {
                                            notePendingSyncDao.deletePendingSyncNote(pendingNote.noteId)
                                        }
                                        is Result.Error -> {
                                            // Update last attempt time
                                            notePendingSyncDao.upsertPendingSyncNote(
                                                pendingNote.copy(
                                                    lastAttempt = System.currentTimeMillis()
                                                )
                                            )
                                        }
                                        is Result.Loading -> {
                                            // Just retry next time
                                            Timber.d("Update note loading, will retry")
                                        }
                                    }
                                }
                            }
                        } else {
                            // Note not found, remove from sync queue
                            notePendingSyncDao.deletePendingSyncNote(pendingNote.noteId)
                        }
                    }
                    is Result.Error -> {
                        Timber.e("Failed to get local note for sync: ${localNoteResult.message}")
                        // Consider removing from sync queue if note can't be found
                        notePendingSyncDao.deletePendingSyncNote(pendingNote.noteId)
                    }
                    is Result.Loading -> {
                        Timber.d("Loading local note, will retry")
                    }
                }
            }

            // Sync pending deletes
            val pendingDeletes = notePendingSyncDao.getAllDeletedNotes()
            pendingDeletes.forEach { deletedNote ->
                when (val result = remoteDataSource.deleteNote(deletedNote.noteId)) {
                    is Result.Success -> {
                        notePendingSyncDao.deleteDeletedNote(deletedNote.noteId)
                    }
                    is Result.Error -> {
                        // Could update last attempt time if needed
                        Timber.e("Failed to delete note remotely: ${result.message}")
                    }
                    is Result.Loading -> {
                        Timber.d("Delete note loading, will retry")
                    }
                }
            }
        } catch (e: Exception) {
            Timber.e(e, "Failed to sync pending notes")
        }
    }
}