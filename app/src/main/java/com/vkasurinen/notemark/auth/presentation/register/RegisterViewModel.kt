package com.vkasurinen.notemark.auth.presentation.register

import android.util.Log
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
import kotlinx.coroutines.delay
import timber.log.Timber

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
        username: String,
        email: String,
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
                viewModelScope.launch {
                    eventChannel.send(RegisterEvent.NavigateToLogin)
                }
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

    private fun register() {
        viewModelScope.launch {
            _state.update { it.copy(isRegistering = true, isLoading = true) }

            delay(2000L)

            val username = _state.value.username.text.toString().trim()
            val email = _state.value.email.text.toString().trim()
            val password = _state.value.password.text.toString()

            val result = authRepository.register(
                username = username,
                email = email,
                password = password
            )

            _state.update { it.copy(isRegistering = false, isLoading = false) }

            when (result) {
                is Result.Error -> {
                    val errorMessage = result.message ?: "An error occurred when creating an account"
                    if (errorMessage.contains("already exists", ignoreCase = true)) {
                        eventChannel.send(RegisterEvent.Error(UiText.Dynamic("A user with that email or username already exists.")))
                    } else {
                        eventChannel.send(RegisterEvent.Error(UiText.Dynamic(errorMessage)))
                    }
                }
                is Result.Success -> {
                    eventChannel.send(RegisterEvent.RegistrationSuccess)
                }
                is Result.Loading -> Unit
            }
        }
    }

}