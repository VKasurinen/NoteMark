package com.vkasurinen.notemark.settings

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.vkasurinen.notemark.core.domain.SessionStorage
import com.vkasurinen.notemark.core.presentation.util.UiText
import kotlinx.coroutines.channels.Channel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.receiveAsFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch

class SettingsViewModel(
    private val sessionStorage: SessionStorage
) : ViewModel() {

    private val _state = MutableStateFlow(SettingsState())
    val state = _state

    private val eventChannel = Channel<SettingsEvent>()
    val events = eventChannel.receiveAsFlow()

    fun onAction(action: SettingsAction) {
        when (action) {
            SettingsAction.Logout -> {
                viewModelScope.launch {
                    try {
                        sessionStorage.clear()
                        eventChannel.send(SettingsEvent.NavigateToLogin)
                    } catch (e: Exception) {
                        eventChannel.send(SettingsEvent.Error(UiText.Dynamic("Failed to log out")))
                    }
                }
            }

            SettingsAction.NavigateBack -> {
                viewModelScope.launch {
                    eventChannel.send(SettingsEvent.NavigateBack)
                }
            }
        }
    }
}
