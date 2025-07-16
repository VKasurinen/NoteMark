package com.vkasurinen.notemark.core.domain.notes

import kotlinx.coroutines.flow.Flow
import com.vkasurinen.notemark.core.domain.util.Result
import com.vkasurinen.notemark.notes.domain.Note

interface LocalDataSource {
    suspend fun createNote(note: Note): Result<Unit>
    suspend fun updateNote(note: Note): Result<Note>
    suspend fun getNotes(page: Int, size: Int): Result<List<Note>>
    suspend fun deleteNote(id: String): Result<Unit>
}