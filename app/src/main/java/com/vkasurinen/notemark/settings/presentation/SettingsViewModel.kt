package com.vkasurinen.notemark.settings.presentation

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.vkasurinen.notemark.core.domain.SessionStorage
import com.vkasurinen.notemark.core.presentation.util.ConnectivityObserver
import com.vkasurinen.notemark.core.presentation.util.UiText
import com.vkasurinen.notemark.settings.di.settingsModule
import com.vkasurinen.notemark.settings.domain.repository.SettingsRepository
import kotlinx.coroutines.channels.Channel
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.flow.receiveAsFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import timber.log.Timber

class SettingsViewModel(
    private val sessionStorage: SessionStorage,
    private val connectivityObserver: ConnectivityObserver,
    private val settingsRepository: SettingsRepository
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
                        val isOnline = connectivityObserver.connectivity.first()
                        if (!isOnline) {
                            eventChannel.send(SettingsEvent.Error(UiText.Dynamic("You need an internet connection to log out")))
                            return@launch
                        }

                        _state.update { it.copy(isLoggingOut = true) }

                        // 1. Clear the local note database (you'll need to inject your database repository)
                        settingsRepository.clearLocalDatabase()

                        // 2. Clear session tokens
                        sessionStorage.clear()

                        // 3. Call POST /api/auth/logout (you'll need to inject your auth repository)
                        viewModelScope.launch {
                            try {
                                val authInfo = sessionStorage.get()
                                val refreshToken = authInfo?.refreshToken ?: throw IllegalStateException("Refresh token not found")
                                settingsRepository.logout(refreshToken)
                            } catch (e: Exception) {
                                Timber.e(e, "Logout failed")
                            }
                        }

                        eventChannel.send(SettingsEvent.NavigateToLogin)
                    } catch (e: Exception) {
                        eventChannel.send(SettingsEvent.Error(UiText.Dynamic("Failed to log out: ${e.message}")))
                    } finally {
                        _state.update { it.copy(isLoggingOut = false) }
                    }
                }
            }

            SettingsAction.NavigateBack -> {
                viewModelScope.launch {
                    eventChannel.send(SettingsEvent.NavigateBack)
                }
            }

            is SettingsAction.SelectSyncInterval -> {
                _state.update { it.copy(syncInterval = action.interval) }
            }

            SettingsAction.SyncNotes -> {
                _state.update { it.copy(isSyncing = true) }
                viewModelScope.launch {
                    try {
                        // Simulate sync completion for now
                        delay(2000)
                    } finally {
                        _state.update { it.copy(isSyncing = false) }
                    }
                }
            }
        }
    }
}