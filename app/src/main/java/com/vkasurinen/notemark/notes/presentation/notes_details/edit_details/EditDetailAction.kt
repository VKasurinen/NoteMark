package com.vkasurinen.notemark.notes.presentation.notes_details.edit_details

sealed interface EditDetailAction {
    data object OnSaveClick : EditDetailAction
    data object NavigateToReader : EditDetailAction
    data class OnTitleChange(val title: String) : EditDetailAction
    data class OnContentChange(val content: String) : EditDetailAction
}