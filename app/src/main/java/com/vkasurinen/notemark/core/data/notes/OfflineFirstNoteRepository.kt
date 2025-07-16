package com.vkasurinen.notemark.core.data.notes

import com.vkasurinen.notemark.core.database.dao.NoteDao
import com.vkasurinen.notemark.core.database.mappers.toDomain
import com.vkasurinen.notemark.core.database.mappers.toEntity
import com.vkasurinen.notemark.core.database.mappers.toRequest
import com.vkasurinen.notemark.core.domain.notes.NotesRepository
import com.vkasurinen.notemark.core.domain.util.Result
import com.vkasurinen.notemark.notes.domain.Note
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.launch
import timber.log.Timber

class OfflineFirstNoteRepository(
    private val notesApi: NotesApi,
    private val noteDao: NoteDao,
    private val applicationScope: CoroutineScope
) : NotesRepository {

    override suspend fun createNote(request: Note): Result<Note> {
        return try {
            noteDao.upsertNote(request.toEntity())

            applicationScope.launch {
                try {
                    val response = notesApi.createNote(request.toRequest())
                    noteDao.upsertNote(response.toDomain().toEntity())
                } catch (e: Exception) {
                    Timber.e(e, "Remote create failed — will retry later.")
                    // Store failed sync for sync job
                }
            }

            Result.Success(request)
        } catch (e: Exception) {
            Timber.e(e, "createNote() - Local DB write failed")
            Result.Error(e.message ?: "Failed to create note locally")
        }
    }

    override suspend fun updateNote(request: Note): Result<Note> {
        return try {
            noteDao.upsertNote(request.toEntity())

            applicationScope.launch {
                try {
                    val response = notesApi.updateNote(request.toRequest())
                    noteDao.upsertNote(response.toDomain().toEntity())
                } catch (e: Exception) {
                    Timber.e(e, "Remote update failed")
                }
            }

            Result.Success(request)
        } catch (e: Exception) {
            Timber.e(e, "updateNote() - Local DB write failed")
            Result.Error(e.message ?: "Failed to update note locally")
        }
    }

    override suspend fun getNotes(page: Int, size: Int): Result<List<Note>> {
        try {
            // Step 1: Fetch from local first and return immediately
            val localEntities = noteDao.getNotesOnce()
            val localNotes = localEntities.map { entity ->
                Note(
                    id = entity.id,
                    title = entity.title,
                    content = entity.content,
                    createdAt = entity.createdAt,
                    lastEditedAt = entity.lastEditedAt
                )
            }

            // Launch remote fetch in parallel, but don’t block
            applicationScope.launch {
                try {
                    val response = notesApi.getNotes(page, size)
                    val remoteNotes = response.notes.map { it.toDomain() }
                    noteDao.upsertNotes(remoteNotes.map { it.toEntity() })
                    Timber.d("Remote notes fetched and saved to DB")
                } catch (e: Exception) {
                    Timber.e(e, "Remote fetch failed, using local fallback")
                }
            }

            return Result.Success(localNotes)
        } catch (localException: Exception) {
            Timber.e(localException, "Local fetch failed, trying remote")

            // Step 2: Local failed, fallback to remote
            return try {
                val response = notesApi.getNotes(page, size)
                val remoteNotes = response.notes.map { it.toDomain() }

                noteDao.upsertNotes(remoteNotes.map { it.toEntity() })
                Result.Success(remoteNotes)
            } catch (remoteException: Exception) {
                Timber.e(remoteException, "Remote fallback failed")
                Result.Error("Failed to load notes from both local and remote")
            }
        }
    }


    override suspend fun deleteNote(id: String): Result<Unit> {
        return try {
            noteDao.deleteNote(id)

            applicationScope.launch {
                try {
                    notesApi.deleteNote(id)
                } catch (e: Exception) {
                    Timber.e(e, "Remote delete failed")
                    // Store failed delete for sync job
                }
            }

            Result.Success(Unit)
        } catch (e: Exception) {
            Timber.e(e, "deleteNote() - Local DB delete failed")
            Result.Error(e.message ?: "Failed to delete note locally")
        }
    }
    override fun observeNotes(): Flow<List<Note>> {
        return noteDao.observeNotes().map { entities ->
            entities.map { entity ->
                Note(
                    id = entity.id,
                    title = entity.title,
                    content = entity.content,
                    createdAt = entity.createdAt,
                    lastEditedAt = entity.lastEditedAt
                )
            }
        }
    }



}