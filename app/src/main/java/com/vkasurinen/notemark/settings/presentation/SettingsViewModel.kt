package com.vkasurinen.notemark.settings.presentation

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.vkasurinen.notemark.core.domain.SessionStorage
import com.vkasurinen.notemark.core.domain.notes.NotesRepository
import com.vkasurinen.notemark.core.presentation.util.ConnectivityObserver
import com.vkasurinen.notemark.core.presentation.util.UiText
import com.vkasurinen.notemark.settings.domain.repository.SettingsRepository
import com.vkasurinen.notemark.settings.presentation.components.SyncInterval
import kotlinx.coroutines.*
import kotlinx.coroutines.channels.Channel
import kotlinx.coroutines.flow.*
import timber.log.Timber
import java.time.Instant
import java.time.ZonedDateTime

class SettingsViewModel(
    private val sessionStorage: SessionStorage,
    private val connectivityObserver: ConnectivityObserver,
    private val settingsRepository: SettingsRepository,
    private val notesRepository: NotesRepository
) : ViewModel() {

    private val _state = MutableStateFlow(SettingsState())
    val state = _state.asStateFlow()

    private val eventChannel = Channel<SettingsEvent>()
    val events = eventChannel.receiveAsFlow()

    private var syncJob: Job? = null
    private var intervalJob: Job? = null

    init {
        // Start syncing if interval is not manual
        startIntervalSync(state.value.syncInterval)
    }

    fun onAction(action: SettingsAction) {
        when (action) {
            SettingsAction.Logout -> logout()
            SettingsAction.NavigateBack -> sendEvent(SettingsEvent.NavigateBack)
            is SettingsAction.SelectSyncInterval -> {
                _state.update { it.copy(syncInterval = action.interval) }
                restartIntervalSync(action.interval)
            }
            SettingsAction.SyncNotes -> {
                performManualSync()
            }
        }
    }

    private fun logout() {
        viewModelScope.launch {
            try {
                val isOnline = connectivityObserver.connectivity.first()
                if (!isOnline) {
                    sendEvent(SettingsEvent.Error(UiText.Dynamic("You need an internet connection to log out")))
                    return@launch
                }

                _state.update { it.copy(isLoggingOut = true) }

                settingsRepository.clearLocalDatabase()
                sessionStorage.clear()

                launch {
                    try {
                        val authInfo = sessionStorage.get()
                        val refreshToken = authInfo?.refreshToken ?: throw IllegalStateException("Refresh token not found")
                        settingsRepository.logout(refreshToken)
                    } catch (e: Exception) {
                        Timber.e(e, "Logout failed")
                    }
                }

                sendEvent(SettingsEvent.NavigateToLogin)
            } catch (e: Exception) {
                sendEvent(SettingsEvent.Error(UiText.Dynamic("Failed to log out: ${e.message}")))
            } finally {
                _state.update { it.copy(isLoggingOut = false) }
            }
        }
    }

    private fun performManualSync() {
        syncJob?.cancel()
        syncJob = viewModelScope.launch {
            _state.update { it.copy(isSyncing = true) }

            try {
                val connected = connectivityObserver.connectivity.first()
                if (!connected) {
                    sendEvent(SettingsEvent.Error(UiText.Dynamic("No internet connection")))
                    return@launch
                }

                notesRepository.syncPendingNotes()
                val formattedTime = getCurrentFormattedTime()
                _state.update { it.copy(lastSync = formattedTime) }

            } catch (e: Exception) {
                Timber.e(e, "Sync failed")
                sendEvent(SettingsEvent.Error(UiText.Dynamic("Sync failed: ${e.message}")))
            } finally {
                _state.update { it.copy(isSyncing = false) }
            }
        }
    }

    private fun restartIntervalSync(interval: SyncInterval) {
        intervalJob?.cancel()
        startIntervalSync(interval)
    }

    private fun startIntervalSync(interval: SyncInterval) {
        val minutes = interval.minutes ?: return
        intervalJob = viewModelScope.launch {
            while (isActive) {
                delay(minutes.toLong() * 60 * 1000)
                try {
                    _state.update { it.copy(isSyncing = true) }
                    notesRepository.syncPendingNotes()
                    val formattedTime = getCurrentFormattedTime()
                    _state.update { it.copy(
                        isSyncing = false,
                        lastSync = formattedTime
                    ) }
                } catch (e: Exception) {
                    Timber.e(e, "Periodic sync failed")
                    _state.update { it.copy(isSyncing = false) }
                }
            }
        }
    }

    private fun getCurrentFormattedTime(): String {
        return ZonedDateTime.now().toLocalTime().toString()
    }

    private fun sendEvent(event: SettingsEvent) {
        viewModelScope.launch {
            eventChannel.send(event)
        }
    }

    override fun onCleared() {
        super.onCleared()
        syncJob?.cancel()
        intervalJob?.cancel()
    }
}
