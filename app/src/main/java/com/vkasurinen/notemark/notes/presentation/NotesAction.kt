package com.vkasurinen.notemark.notes.presentation

sealed interface NotesAction {
    data class UpdateUsername(val username: String) : NotesAction
}