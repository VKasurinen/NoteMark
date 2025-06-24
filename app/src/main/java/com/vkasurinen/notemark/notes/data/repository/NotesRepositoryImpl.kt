package com.vkasurinen.notemark.notes.data.repository

import com.vkasurinen.notemark.notes.data.api.NotesApi
import com.vkasurinen.notemark.core.database.dao.NoteDao
import com.vkasurinen.notemark.core.database.entity.NoteEntity
import com.vkasurinen.notemark.core.database.mappers.toEntity
import com.vkasurinen.notemark.notes.data.requests.NoteRequest
import com.vkasurinen.notemark.notes.data.requests.PaginatedNotesResponse
import com.vkasurinen.notemark.notes.data.requests.NoteResponse
import com.vkasurinen.notemark.core.domain.util.Result
import com.vkasurinen.notemark.notes.domain.repository.NotesRepository

class NotesRepositoryImpl(
    private val notesApi: NotesApi,
    private val noteDao: NoteDao
) : NotesRepository {

    override suspend fun createNote(request: NoteRequest): Result<NoteResponse> {
        return try {
            val response = notesApi.createNote(request)
            noteDao.upsertNote(response.toEntity())
            Result.Success(response)
        } catch (e: Exception) {
            Result.Error(e.message ?: "Failed to create note")
        }
    }


    override suspend fun updateNote(request: NoteRequest): Result<NoteResponse> {
        return try {
            val response = notesApi.updateNote(request)
            noteDao.upsertNote(
                NoteEntity(
                    id = response.id,
                    title = response.title,
                    content = response.content,
                    createdAt = response.createdAt,
                    lastEditedAt = response.lastEditedAt
                )
            )
            Result.Success(response)
        } catch (e: Exception) {
            Result.Error(e.message ?: "Failed to update note")
        }
    }

    override suspend fun getNotes(page: Int, size: Int): Result<PaginatedNotesResponse> {
        return try {
            val response = notesApi.getNotes(page, size)
            Result.Success(response)
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