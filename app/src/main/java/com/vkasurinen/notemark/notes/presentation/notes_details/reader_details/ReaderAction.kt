package com.vkasurinen.notemark.notes.presentation.notes_details.reader_details

sealed interface ReaderAction {

    data object NavigateBack : ReaderAction
    data object NavigateToReader : ReaderAction
    data object NavigateToEdit : ReaderAction
    data object ToggleUI : ReaderAction

}