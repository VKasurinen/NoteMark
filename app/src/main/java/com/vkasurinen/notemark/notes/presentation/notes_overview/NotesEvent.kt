package com.vkasurinen.notemark.notes.presentation.notes_overview

import com.vkasurinen.notemark.auth.presentation.login.LoginEvent
import com.vkasurinen.notemark.core.presentation.util.UiText

sealed interface NotesEvent {
    data class NavigateToEditDetail(val noteId: String) : NotesEvent
    data object NavigateToSettings : NotesEvent
    data class Error(val error: UiText) : NotesEvent
}
