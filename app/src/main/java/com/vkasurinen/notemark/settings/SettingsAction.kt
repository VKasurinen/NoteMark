package com.vkasurinen.notemark.settings

sealed interface SettingsAction {
    data object NavigateBack : SettingsAction
    data object Logout : SettingsAction
}