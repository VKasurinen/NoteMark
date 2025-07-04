package com.vkasurinen.notemark.notes.presentation.notes_details.reader_details

import com.vkasurinen.notemark.core.presentation.util.UiText
import com.vkasurinen.notemark.notes.presentation.notes_details.view_details.ViewDetailEvent

interface ReaderEvent {
    data object NavigateToEditDetail : ReaderEvent
    data object NavigateToReaderDetail : ReaderEvent
    data object NavigateBack : ReaderEvent
    data class Error(val error: UiText) : ReaderEvent
}
