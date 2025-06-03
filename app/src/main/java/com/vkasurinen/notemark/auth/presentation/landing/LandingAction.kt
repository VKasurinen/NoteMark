package com.vkasurinen.notemark.auth.presentation.landing

sealed interface LandingAction {
    data object OnGetStartedClick : LandingAction
    data object OnLogInClick : LandingAction
}