package com.vkasurinen.notemark.notes.presentation.notes_details.reader_details

import com.vkasurinen.notemark.core.presentation.util.UiText

interface ReaderEvent {
    data object NavigateBack : ReaderEvent
    data object NavigateToEditDetail : ReaderEvent
    data object NavigateToViewDetail : ReaderEvent
    data class Error(val error: UiText) : ReaderEvent
}
