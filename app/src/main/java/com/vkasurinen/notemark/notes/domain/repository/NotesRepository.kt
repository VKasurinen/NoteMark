package com.vkasurinen.notemark.notes.domain.repository

import com.vkasurinen.notemark.core.domain.util.Result
import com.vkasurinen.notemark.notes.domain.Note

interface NotesRepository {
    suspend fun createNote(request: Note): Result<Note>
    suspend fun updateNote(request: Note): Result<Note>
    suspend fun getNotes(page: Int, size: Int): Result<List<Note>>
    suspend fun deleteNote(id: String): Result<Unit>
}