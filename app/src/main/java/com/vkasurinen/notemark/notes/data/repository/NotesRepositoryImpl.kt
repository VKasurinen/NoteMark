package com.vkasurinen.notemark.notes.data.repository

import com.vkasurinen.notemark.notes.data.api.NotesApi
import com.vkasurinen.notemark.core.database.dao.NoteDao
import com.vkasurinen.notemark.core.database.mappers.toDomain
import com.vkasurinen.notemark.core.database.mappers.toEntity
import com.vkasurinen.notemark.core.database.mappers.toRequest
import com.vkasurinen.notemark.notes.domain.Note
import com.vkasurinen.notemark.core.domain.util.Result
import com.vkasurinen.notemark.notes.domain.repository.NotesRepository

class NotesRepositoryImpl(
    private val notesApi: NotesApi,
    private val noteDao: NoteDao
) : NotesRepository {

    override suspend fun createNote(request: Note): Result<Note> {
        return try {
            val response = notesApi.createNote(request.toRequest())
            val note = response.toDomain()
            noteDao.upsertNote(note.toEntity())
            Result.Success(note)
        } catch (e: Exception) {
            Result.Error(e.message ?: "Failed to create note")
        }
    }

    override suspend fun updateNote(request: Note): Result<Note> {
        return try {
            val response = notesApi.updateNote(request.toRequest())
            val note = response.toDomain()
            noteDao.upsertNote(note.toEntity())
            Result.Success(note)
        } catch (e: Exception) {
            Result.Error(e.message ?: "Failed to update note")
        }
    }

    override suspend fun getNotes(page: Int, size: Int): Result<List<Note>> {
        return try {
            val response = notesApi.getNotes(page, size)
            val notes = response.notes.map { it.toDomain() }
            Result.Success(notes)
        } catch (e: Exception) {
            Result.Error(e.message ?: "Failed to fetch notes")
        }
    }

    override suspend fun deleteNote(id: String): Result<Unit> {
        return try {
            notesApi.deleteNote(id)
            noteDao.deleteNote(id)
            Result.Success(Unit)
        } catch (e: Exception) {
            Result.Error(e.message ?: "Failed to delete note")
        }
    }
}