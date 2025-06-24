package com.vkasurinen.notemark.notes.presentation.notes_overview

data class Note(
    val date: String,
    val title: String,
    val description: String
)

data class NotesState(
    val username: String? = null,
    val notes: List<Note?> = emptyList()
)
