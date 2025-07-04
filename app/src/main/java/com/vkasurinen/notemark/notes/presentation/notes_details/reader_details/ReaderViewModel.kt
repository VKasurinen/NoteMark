package com.vkasurinen.notemark.notes.presentation.notes_details.reader_details

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.onStart
import kotlinx.coroutines.flow.stateIn

class ReaderViewModel : ViewModel() {


    private val _state = MutableStateFlow(ReaderState())
    val state = _state
        .stateIn(
            scope = viewModelScope,
            started = SharingStarted.WhileSubscribed(5_000L),
            initialValue = ReaderState()
        )

    fun onAction(action: ReaderAction) {
        when (action) {
            else -> TODO("Handle actions")
        }
    }

}