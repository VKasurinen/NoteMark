package com.vkasurinen.notemark.notes.presentation.notes_details

sealed class DetailEvent {
    data object NavigateToNotes : DetailEvent()
    data class ShowValidationError(val message: String) : DetailEvent()

}