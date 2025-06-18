package com.vkasurinen.notemark.notes.presentation

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.channels.Channel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.onStart
import kotlinx.coroutines.flow.receiveAsFlow
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch

class NotesViewModel : ViewModel() {


    private val _state = MutableStateFlow(NotesState())
    val state = _state
        .stateIn(
            scope = viewModelScope,
            started = SharingStarted.WhileSubscribed(5_000L),
            initialValue = NotesState()
        )

    private val eventChannel = Channel<NotesEvent>()
    val events = eventChannel.receiveAsFlow()

    fun onAction(action: NotesAction) {
        when (action) {
            is NotesAction.UpdateUsername -> {
                _state.update { it.copy(username = action.username) }
            }
        }
    }
}