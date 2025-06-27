package com.vkasurinen.notemark.notes.presentation.notes_details

sealed interface DetailAction {
    data object OnSaveClick : DetailAction
    data class OnTitleChange(val title: String) : DetailAction
    data class OnContentChange(val content: String) : DetailAction
}