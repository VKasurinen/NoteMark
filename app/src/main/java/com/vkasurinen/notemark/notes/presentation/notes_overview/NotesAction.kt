package com.vkasurinen.notemark.notes.presentation.notes_overview

sealed interface NotesAction {
    data class UpdateUsername(val username: String) : NotesAction
    data object CreateNewNote : NotesAction
}