package com.vkasurinen.notemark.auth.presentation.register

import androidx.compose.runtime.snapshotFlow
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.vkasurinen.notemark.auth.domain.UserDataValidator
import com.vkasurinen.notemark.auth.domain.repository.AuthRepository
import kotlinx.coroutines.channels.Channel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.receiveAsFlow
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import com.vkasurinen.notemark.core.domain.util.Result
import com.vkasurinen.notemark.core.presentation.util.UiText

class RegisterViewModel(
    private val userDataValidator: UserDataValidator,
    private val authRepository: AuthRepository
) : ViewModel() {

    private val _state = MutableStateFlow(RegisterState())
    val state = _state
        .stateIn(
            scope = viewModelScope,
            started = SharingStarted.WhileSubscribed(5_000L),
            initialValue = RegisterState()
        )

    private val eventChannel = Channel<RegisterEvent>()
    val events = eventChannel.receiveAsFlow()

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
        val isUsernameValid = username.length in 3..20

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

            RegisterAction.OnRegisterClick -> register()

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

    fun register() {
        viewModelScope.launch {
            _state.update { it.copy(isRegistering = true) }
            val result = authRepository.register(
                email = _state.value.email.text.toString().trim(),
                username = _state.value.username.text.toString().trim(),
                password = _state.value.password.text.toString()
            )
            _state.update { it.copy(isRegistering = false) }

            when (result) {
                is Result.Error -> {
                    val errorMessage = if (result.message == "Conflict") {
                        "Email already exists"
                    } else {
                        result.message ?: "An unknown error occurred"
                    }
                    eventChannel.send(RegisterEvent.Error(UiText.Dynamic(errorMessage)))
                }
                is Result.Success -> {
                    eventChannel.send(RegisterEvent.RegistrationSuccess)
                }

                is Result.Loading -> {
                    _state.update { it.copy(isRegistering = result.isLoading) }
                }
            }
        }
    }



}