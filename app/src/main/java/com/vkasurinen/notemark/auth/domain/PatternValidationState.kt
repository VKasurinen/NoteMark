package com.vkasurinen.notemark.auth.domain

data class PasswordValidationState(
    val hasMinLength: Boolean = false,
    val hasNumber: Boolean = false,
    val hasSymbol: Boolean = false,
) {
    val isValidPassword: Boolean
        get() = hasMinLength && (hasNumber || hasSymbol)
}