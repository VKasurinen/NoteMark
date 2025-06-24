package com.vkasurinen.notemark.notes.presentation.notes_overview

import com.vkasurinen.notemark.notes.domain.Note

data class NotesState(
    val username: String? = null,
    val notes: List<Note> = emptyList()
)

