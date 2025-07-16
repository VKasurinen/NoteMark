package com.vkasurinen.notemark.notes.network

import com.vkasurinen.notemark.core.data.notes.NotesApi
import com.vkasurinen.notemark.core.database.mappers.toDomain
import com.vkasurinen.notemark.core.database.mappers.toRequest
import com.vkasurinen.notemark.core.domain.notes.RemoteDataSource
import com.vkasurinen.notemark.core.domain.util.Result
import com.vkasurinen.notemark.notes.domain.Note
import timber.log.Timber

class RemoteNoteDataSource(
    private val notesApi: NotesApi
) : RemoteDataSource {

    override suspend fun getNotes(page: Int, size: Int): Result<List<Note>> {
        return try {
            val response = notesApi.getNotes(page, size)
            Result.Success(response.notes.map { it.toDomain() })
        } catch (e: Exception) {
            Timber.e(e, "Failed to fetch notes from remote")
            Result.Error("Failed to fetch notes from remote")
        }
    }

    override suspend fun postNote(note: Note): Result<Note> {
        return try {
            val response = notesApi.createNote(note.toRequest())
            Result.Success(response.toDomain())
        } catch (e: Exception) {
            Timber.e(e, "Failed to create note on remote")
            Result.Error("Failed to create note on remote")
        }
    }

    override suspend fun updateNote(note: Note): Result<Note> {
        return try {
            val response = notesApi.updateNote(note.toRequest())
            Result.Success(response.toDomain())
        } catch (e: Exception) {
            Timber.e(e, "Failed to update note on remote")
            Result.Error("Failed to update note on remote")
        }
    }

    override suspend fun deleteNote(id: String): Result<Unit> {
        return try {
            notesApi.deleteNote(id)
            Result.Success(Unit)
        } catch (e: Exception) {
            Timber.e(e, "Failed to delete note on remote")
            Result.Error("Failed to delete note on remote")
        }
    }
}