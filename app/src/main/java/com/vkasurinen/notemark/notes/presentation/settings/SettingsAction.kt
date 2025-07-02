package com.vkasurinen.notemark.notes.presentation.settings

import com.vkasurinen.notemark.notes.presentation.notes_overview.NotesAction

sealed interface SettingsAction {
    data object NavigateBack : SettingsAction
    data object Logout : SettingsAction
}