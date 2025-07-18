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
            val request = note.toRequest()
            Timber.d("Sending POST request: $request")
            val response = notesApi.createNote(request)
            Timber.d("Received response: $response")
            Result.Success(response.toDomain())
        } catch (e: Exception) {
            Timber.e(e, "Failed to create note on remote")
            Result.Error("Failed to create note on remote: ${e.message}")
        }
    }

    override suspend fun updateNote(note: Note): Result<Note> {

        // Debug logging
        Timber.d("""
        Preparing update for ${note.id}
        Title: '${note.title}'
        Content: '${note.content}'
    """.trimIndent())

        // Execute update
        return try {
            val request = note.toRequest().also {
                Timber.d("Request payload verified - Content: '${it.content}'")
            }

            val response = notesApi.updateNote(request)

            // Verify response
            if (response.content != note.content) {
                Timber.e("Server returned mismatched content")
                return Result.Error("Server returned inconsistent data")
            }

            Result.Success(response.toDomain())
        } catch (e: Exception) {
            Timber.e(e, "Update failed for ${note.id}")
            Result.Error("Update failed: ${e.message}")
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