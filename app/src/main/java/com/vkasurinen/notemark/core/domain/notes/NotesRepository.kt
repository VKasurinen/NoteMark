package com.vkasurinen.notemark.core.domain.notes

import com.vkasurinen.notemark.core.domain.util.Result
import com.vkasurinen.notemark.notes.domain.Note
import kotlinx.coroutines.flow.Flow


interface NotesRepository {
    suspend fun createNote(note: Note): Result<Note>
    suspend fun updateNote(note: Note): Result<Note>
    suspend fun getNotes(page: Int, size: Int): Result<List<Note>>
    suspend fun deleteNote(id: String): Result<Unit>
    fun observeNotes(): Flow<List<Note>>
    suspend fun syncPendingNotes()
}