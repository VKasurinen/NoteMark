package com.vkasurinen.notemark.auth.presentation.register

import com.vkasurinen.notemark.core.presentation.util.UiText

sealed interface RegisterEvent {
    data object RegistrationSuccess : RegisterEvent
    data class Error(val error: UiText) : RegisterEvent
    data object NavigateToLogin : RegisterEvent
}