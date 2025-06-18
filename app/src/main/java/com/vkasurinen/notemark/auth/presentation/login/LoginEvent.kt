package com.vkasurinen.notemark.auth.presentation.login

import com.vkasurinen.notemark.core.presentation.util.UiText

sealed interface LoginEvent {
    data class LoginSuccess(val username: String) : LoginEvent
    data class Error(val error: UiText) : LoginEvent
    data object NavigateToRegister : LoginEvent
}

