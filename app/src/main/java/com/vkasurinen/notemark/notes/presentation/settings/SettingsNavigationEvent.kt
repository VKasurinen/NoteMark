package com.vkasurinen.notemark.notes.presentation.settings

import com.vkasurinen.notemark.core.presentation.util.UiText

sealed interface SettingsEvent {
    data object NavigateToLogin : SettingsEvent
    data object NavigateBack : SettingsEvent
    data class Error(val error: UiText) : SettingsEvent
}
