package com.vkasurinen.notemark.auth.presentation.login

import android.annotation.SuppressLint
import android.content.Context
import androidx.compose.runtime.snapshotFlow
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.vkasurinen.notemark.auth.domain.UserDataValidator
import com.vkasurinen.notemark.auth.domain.repository.AuthRepository
import com.vkasurinen.notemark.auth.presentation.register.RegisterAction
import com.vkasurinen.notemark.auth.presentation.register.RegisterEvent
import com.vkasurinen.notemark.auth.presentation.register.RegisterState
import kotlinx.coroutines.channels.Channel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.onStart
import kotlinx.coroutines.flow.receiveAsFlow
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import com.vkasurinen.notemark.core.domain.util.Result
import com.vkasurinen.notemark.core.presentation.util.UiText
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.delay
import kotlinx.coroutines.withContext
import kotlinx.coroutines.withTimeoutOrNull

class LoginViewModel @SuppressLint("StaticFieldLeak") constructor(
    private val authRepository: AuthRepository,
    private val userDataValidator: UserDataValidator,
) : ViewModel() {

    private val _state = MutableStateFlow(LoginState())
    val state = _state
        .stateIn(
            scope = viewModelScope,
            started = SharingStarted.WhileSubscribed(5_000L),
            initialValue = LoginState()
        )

    private val eventChannel = Channel<LoginEvent>()
    val events = eventChannel.receiveAsFlow()

    init {
        viewModelScope.launch {
            combine(
                snapshotFlow { _state.value.email.text.toString() },
                snapshotFlow { _state.value.password.text.toString() }
            ) { email, password ->
                val isEmailValid = userDataValidator.isValidEmail(email)
                val isPasswordNotEmpty = password.isNotEmpty()
                _state.update { it.copy(isEmailValid = isEmailValid) }
                isEmailValid && isPasswordNotEmpty
            }.collect { isValid ->
                _state.update { it.copy(canLogin = isValid) }
            }
        }
    }

    fun onAction(action: LoginAction) {
        when (action) {
            LoginAction.OnLoginClick -> {
                login()
            }

            LoginAction.OnRegisterClick -> {
                viewModelScope.launch {
                    eventChannel.send(LoginEvent.NavigateToRegister)
                }
            }

            is LoginAction.OnTogglePasswordVisibilityClick -> {
                _state.update { currentState ->
                    currentState.copy(
                        isPasswordVisible = !currentState.isPasswordVisible
                    )
                }
            }
        }
    }

    private fun login() {
        viewModelScope.launch {
            _state.update { it.copy(isLoggingIn = true, isLoading = true) }

            val result = withTimeoutOrNull(5000L) {
                authRepository.login(
                    email = _state.value.email.text.toString().trim(),
                    password = _state.value.password.text.toString()
                )
            }

            _state.update { it.copy(isLoggingIn = false, isLoading = false) }

            when (result) {
                null -> {
                    eventChannel.send(LoginEvent.Error(UiText.Dynamic("Login timed out. Please try again.")))
                }
                is Result.Error -> {
                    eventChannel.send(LoginEvent.Error(UiText.Dynamic("Invalid email or password")))
                }
                is Result.Success -> {
                    eventChannel.send(LoginEvent.LoginSuccess)
                }
                is Result.Loading -> Unit // already handled above
            }
        }
    }
}