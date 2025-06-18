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

            delay(2000L)

            val result = authRepository.login(
                email = _state.value.email.text.toString().trim(),
                password = _state.value.password.text.toString()
            )
            _state.update { it.copy(isLoggingIn = false, isLoading = false) }

            when (result) {
                is Result.Error -> {
                    eventChannel.send(LoginEvent.Error(UiText.Dynamic("Invalid login credentials")))
                }

                is Result.Success -> {
                    val username = result.data?.username
                    _state.update { it.copy(username = username) }
                    eventChannel.send(LoginEvent.LoginSuccess)
                }

                is Result.Loading -> Unit // Handled by state update
            }
        }
    }
}