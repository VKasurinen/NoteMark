package com.vkasurinen.notemark.notes.presentation.settings

sealed interface SettingsAction {
    data object NavigateBack : SettingsAction
    data object Logout : SettingsAction
}