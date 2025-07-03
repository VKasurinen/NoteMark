package com.vkasurinen.notemark.notes.presentation.notes_details.view_details


sealed interface ViewDetailAction {
    data object NavigateBack : ViewDetailAction
    data object NavigateToReader : ViewDetailAction
    data object NavigateToEdit : ViewDetailAction
}