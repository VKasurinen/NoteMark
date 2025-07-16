package com.vkasurinen.notemark.core.domain.notes

import kotlinx.coroutines.flow.Flow
import com.vkasurinen.notemark.core.domain.util.Result
import com.vkasurinen.notemark.notes.domain.Note

interface LocalDataSource {
    fun getNotes(): Flow<List<Note>>
    suspend fun getNotesOnce(): List<Note>
    suspend fun upsertNote(note: Note): Result<Unit>
    suspend fun upsertNotes(notes: List<Note>): Result<Unit>
    suspend fun deleteNote(id: String): Result<Unit>
    suspend fun deleteAllNotes(): Result<Unit>
}