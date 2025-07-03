package com.vkasurinen.notemark.notes.presentation.notes_details.edit_details

sealed interface EditDetailEvent{
    data object NavigateToViewDetail : EditDetailEvent
    data class ShowValidationError(val message: String) : EditDetailEvent
}