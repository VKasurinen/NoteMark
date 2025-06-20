package com.vkasurinen.notemark.auth.presentation.register

import androidx.compose.foundation.text.input.TextFieldState
import com.vkasurinen.notemark.auth.domain.PasswordValidationState

data class RegisterState(
    val email: TextFieldState = TextFieldState(),
    val isEmailValid: Boolean = false,
    val username: TextFieldState = TextFieldState(),
    val isUsernameValid: Boolean = false,
    val usernameError: String? = null,
    val password: TextFieldState = TextFieldState(),
    val password2: TextFieldState = TextFieldState(),
    val isPasswordVisible: Boolean = false,
    val isPassword2Visible: Boolean = false,
    val passwordValidationState: PasswordValidationState = PasswordValidationState(),
    val isRegistering: Boolean = false,
    val canRegister: Boolean = false,
    val isLoading: Boolean = false
) {
    val isPasswordMatching: Boolean
        get() = password.text == password2.text && password.text.isNotEmpty()
}