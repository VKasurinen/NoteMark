package com.vkasurinen.notemark.auth.presentation.register

import androidx.compose.runtime.snapshotFlow
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.vkasurinen.notemark.auth.domain.UserDataValidator
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch

class RegisterViewModel(
    private val userDataValidator: UserDataValidator
) : ViewModel() {

    private val _state = MutableStateFlow(RegisterState())
    val state = _state
        .stateIn(
            scope = viewModelScope,
            started = SharingStarted.WhileSubscribed(5_000L),
            initialValue = RegisterState()
        )

    init {
        viewModelScope.launch {
            combine(
                snapshotFlow { _state.value.email.text },
                snapshotFlow { _state.value.username.text },
                snapshotFlow { _state.value.password.text },
                snapshotFlow { _state.value.password2.text },
                snapshotFlow { _state.value.isRegistering }
            ) { email, username, password, password2, isRegistering ->
                validateForm(
                    email = email.toString(),
                    username = username.toString(),
                    password = password.toString(),
                    confirmPassword = password2.toString(),
                    isRegistering = isRegistering
                )
            }.collect { isValid ->
                _state.update { it.copy(canRegister = isValid) }
            }
        }
    }

    private fun validateForm(
        email: String,
        username: String,
        password: String,
        confirmPassword: String,
        isRegistering: Boolean
    ): Boolean {
        // Validate email
        val isEmailValid = userDataValidator.isValidEmail(email)

        // Validate username (basic check for now)
        val isUsernameValid = username.length >= 3

        // Validate password
        val passwordValidation = userDataValidator.validatePassword(password)

        // Check if passwords match
        val passwordsMatch = password == confirmPassword && password.isNotEmpty()

        // Update state with all validations
        _state.update { current ->
            current.copy(
                isEmailValid = isEmailValid,
                isUsernameValid = isUsernameValid,
                passwordValidationState = passwordValidation,
            )
        }

        // Only enable register button when all conditions are met
        return isEmailValid &&
                isUsernameValid &&
                passwordValidation.isValidPassword &&
                passwordsMatch &&
                !isRegistering
    }

    fun onAction(action: RegisterAction) {
        when (action) {
            RegisterAction.OnLoginClick -> {
                // Handle navigation to login screen
            }

            RegisterAction.OnRegisterClick -> {
                if (state.value.canRegister) {
                    _state.update { it.copy(isRegistering = true) }
                    // Perform registration logic here
                    // After registration is complete:
                    // _state.update { it.copy(isRegistering = false) }
                }
            }

            is RegisterAction.OnTogglePasswordVisibilityClick -> {
                _state.update { currentState ->
                    if (action.isPassword1) {
                        currentState.copy(isPasswordVisible = !currentState.isPasswordVisible)
                    } else {
                        currentState.copy(isPassword2Visible = !currentState.isPassword2Visible)
                    }
                }
            }
        }
    }
}