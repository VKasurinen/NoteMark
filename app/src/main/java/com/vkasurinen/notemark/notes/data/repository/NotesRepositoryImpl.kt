package com.vkasurinen.notemark.notes.data.repository

import com.vkasurinen.notemark.notes.data.api.NotesApi
import com.vkasurinen.notemark.core.database.dao.NoteDao
import com.vkasurinen.notemark.core.database.mappers.toDomain
import com.vkasurinen.notemark.core.database.mappers.toEntity
import com.vkasurinen.notemark.core.database.mappers.toRequest
import com.vkasurinen.notemark.notes.domain.Note
import com.vkasurinen.notemark.core.domain.util.Result
import com.vkasurinen.notemark.notes.domain.repository.NotesRepository
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.launch
import timber.log.Timber

class NotesRepositoryImpl(
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
        return try {
            // Try remote first
            val response = notesApi.getNotes(page, size)
            val remoteNotes = response.notes.map { it.toDomain() }

            // Save to local DB
            noteDao.upsertNotes(remoteNotes.map { it.toEntity() })

            Result.Success(remoteNotes)
        } catch (e: Exception) {
            Timber.e(e, "Remote fetch failed, falling back to local")

            try {
                // Fallback to local
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
                Result.Success(localNotes)
            } catch (dbException: Exception) {
                Timber.e(dbException, "Local fetch failed")
                Result.Error("Failed to load notes from both remote and local sources")
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

}