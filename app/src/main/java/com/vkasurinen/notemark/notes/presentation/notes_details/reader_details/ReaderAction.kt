package com.vkasurinen.notemark.notes.presentation.notes_details.reader_details

import com.vkasurinen.notemark.notes.presentation.notes_details.view_details.ViewDetailAction

sealed interface ReaderAction {

    data object NavigateBack : ReaderAction
    data object NavigateToReader : ReaderAction
    data object NavigateToEdit : ReaderAction

}