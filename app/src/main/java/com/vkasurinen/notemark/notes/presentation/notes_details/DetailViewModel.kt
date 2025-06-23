package com.vkasurinen.notemark.notes.presentation.notes_details

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.onStart
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.flow.update

class DetailViewModel : ViewModel() {

    private val _state = MutableStateFlow(DetailState())
    val state = _state
        .stateIn(
            scope = viewModelScope,
            started = SharingStarted.WhileSubscribed(5_000L),
            initialValue = DetailState()
        )

    fun onAction(action: DetailAction) {
        when (action) {
            DetailAction.OnBackClick -> {
                // Handle back click
            }
            DetailAction.OnSaveClick -> {
                // Handle save click
            }
            is DetailAction.OnTitleChange -> {
                _state.update { it.copy(title = action.title) }
            }
            is DetailAction.OnContentChange -> {
                _state.update { it.copy(content = action.content) }
            }
        }
    }
}