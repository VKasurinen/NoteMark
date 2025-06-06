package com.vkasurinen.notemark.auth.presentation.landing

sealed interface LandingEvent {
    data object NavigateToRegister : LandingEvent
    data object NavigateToLogin : LandingEvent
}