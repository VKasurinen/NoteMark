package com.vkasurinen.notemark.core.database

import com.vkasurinen.notemark.core.database.dao.NoteDao
import com.vkasurinen.notemark.core.database.mappers.toDomain
import com.vkasurinen.notemark.core.database.mappers.toEntity
import com.vkasurinen.notemark.core.domain.notes.LocalDataSource
import com.vkasurinen.notemark.core.domain.util.Result
import com.vkasurinen.notemark.notes.domain.Note
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map
import timber.log.Timber

class RoomLocalNoteDataSource(
    private val noteDao: NoteDao
) : LocalDataSource {

    override suspend fun createNote(note: Note): Result<Unit> {
        return try {
            Timber.d("LocalDataSource - Creating note locally: $note")
            noteDao.upsertNote(note.toEntity())
            Timber.d("LocalDataSource - Note created successfully: ${note.id}")
            Result.Success(Unit)
        } catch (e: Exception) {
            Timber.e(e, "LocalDataSource - Failed to create note locally: ${note.id}")
            Result.Error("LocalDataSource - Failed to create note locally: ${e.message}")
        }
    }

    override suspend fun updateNote(note: Note): Result<Note> {
        return try {
            Timber.d("LocalDataSource - Updating note locally: $note")
            noteDao.upsertNote(note.toEntity())
            Timber.d("LocalDataSource - Note updated successfully: ${note.id}")
            Result.Success(note)
        } catch (e: Exception) {
            Timber.e(e, "LocalDataSource - Failed to update note locally: ${note.id}")
            Result.Error("LocalDataSource - Failed to update note locally: ${e.message}")
        }
    }

    override suspend fun getNotes(page: Int, size: Int): Result<List<Note>> {
        return try {
            val notes: List<Note> = noteDao.getNotesOnce()
                .drop((page - 1) * size)
                .take(size)
                .map { it.toDomain() }
            Result.Success(notes)
        } catch (e: Exception) {
            Result.Error("LocalDataSource - Failed to fetch notes locally: ${e.message}")
        }
    }

    override suspend fun deleteNote(id: String): Result<Unit> {
        return try {
            noteDao.deleteNote(id)
            Result.Success(Unit)
        } catch (e: Exception) {
            Result.Error("LocalDataSource - Failed to delete note locally: ${e.message}")
        }
    }

    override suspend fun getNoteById(id: String): Result<Note> {
        return try {
            noteDao.getNoteById(id)?.toDomain()?.let { note ->
                Result.Success(note)
            } ?: Result.Error("LocalDataSource - Note not found")
        } catch (e: Exception) {
            Result.Error("LocalDataSource - Failed to get note: ${e.message}")
        }
    }

    override suspend fun upsertNotes(notes: List<Note>): Result<Unit> {
        return try {
            Timber.d("LocalDataSource - Upserting notes locally: ${notes.map { it.id }}")
            noteDao.upsertNotes(notes.map { it.toEntity() })
            Timber.d("LocalDataSource - Notes upserted successfully: ${notes.map { it.id }}")
            Result.Success(Unit)
        } catch (e: Exception) {
            Timber.e(e, "LocalDataSource - Failed to upsert notes locally")
            Result.Error("LocalDataSource - Failed to upsert notes: ${e.message}")
        }
    }

    override fun observeNotes(): Flow<List<Note>> {
        return noteDao.observeNotes().map { entities ->
            entities.map { it.toDomain() }
        }
    }
}