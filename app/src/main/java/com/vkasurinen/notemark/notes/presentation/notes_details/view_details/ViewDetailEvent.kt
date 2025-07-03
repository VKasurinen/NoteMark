package com.vkasurinen.notemark.notes.presentation.notes_details.view_details

import com.vkasurinen.notemark.core.presentation.util.UiText


interface ViewDetailEvent {
    data object NavigateToEditDetail : ViewDetailEvent
    data object NavigateToReaderDetail : ViewDetailEvent
    data object NavigateBack : ViewDetailEvent
    data class Error(val error: UiText) : ViewDetailEvent
}