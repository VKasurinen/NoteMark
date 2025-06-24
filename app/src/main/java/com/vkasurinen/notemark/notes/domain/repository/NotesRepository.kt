package com.vkasurinen.notemark.notes.domain.repository

import com.vkasurinen.notemark.notes.data.requests.NoteRequest
import com.vkasurinen.notemark.notes.data.requests.PaginatedNotesResponse
import com.vkasurinen.notemark.notes.data.requests.NoteResponse
import com.vkasurinen.notemark.core.domain.util.Result

interface NotesRepository {
    suspend fun createNote(request: NoteRequest): Result<NoteResponse>
    suspend fun updateNote(request: NoteRequest): Result<NoteResponse>
    suspend fun getNotes(page: Int, size: Int): Result<PaginatedNotesResponse>
    suspend fun deleteNote(id: String): Result<Unit>
}