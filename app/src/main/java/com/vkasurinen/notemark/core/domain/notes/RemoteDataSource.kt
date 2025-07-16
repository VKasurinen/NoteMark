package com.vkasurinen.notemark.core.domain.notes

import com.vkasurinen.notemark.core.domain.util.Result
import com.vkasurinen.notemark.notes.domain.Note

interface RemoteDataSource {
    suspend fun getNotes(page: Int, size: Int): Result<List<Note>>
    suspend fun postNote(note: Note): Result<Note>
    suspend fun updateNote(note: Note): Result<Note>
    suspend fun deleteNote(id: String): Result<Unit>
}