package com.vkasurinen.notemark.auth.presentation.landing

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.onStart
import kotlinx.coroutines.flow.stateIn

import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.asSharedFlow
import kotlinx.coroutines.launch

class LandingViewModel : ViewModel() {

    private val _events = MutableSharedFlow<LandingEvent>()
    val events = _events.asSharedFlow()

    fun onAction(action: LandingAction) {
        when (action) {
            is LandingAction.OnGetStartedClick -> {
                viewModelScope.launch {
                    _events.emit(LandingEvent.NavigateToRegister)
                }
            }
            is LandingAction.OnLogInClick -> {
                // Handle login action if needed
            }
        }
    }
}