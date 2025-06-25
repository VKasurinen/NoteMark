package com.vkasurinen.notemark.notes.presentation.notes_details

sealed interface DetailEvent{
    data object NavigateToNotes : DetailEvent
    data class ShowValidationError(val message: String) : DetailEvent
}