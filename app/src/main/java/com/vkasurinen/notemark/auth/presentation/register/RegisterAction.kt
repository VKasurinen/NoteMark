package com.vkasurinen.notemark.auth.presentation.register

sealed interface RegisterAction {

    data class OnTogglePasswordVisibilityClick(val isPassword1: Boolean) : RegisterAction
    data object OnLoginClick: RegisterAction
    data object OnRegisterClick: RegisterAction

}