package com.vkasurinen.notemark.app.main

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.vkasurinen.notemark.core.domain.SessionStorage
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch

class MainViewModel(
    private val sessionStorage: SessionStorage
) : ViewModel() {

    private val _state = MutableStateFlow(MainState())
    val state = _state.asStateFlow()

    init {
        viewModelScope.launch {
            _state.update { it.copy(isCheckingAuth = true) }
            val isLoggedIn = sessionStorage.get() != null
            _state.update { it.copy(isLoggedIn = isLoggedIn, isCheckingAuth = false) }
        }
    }
}