package com.vkasurinen.notemark.notes.presentation.notes_overview

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.channels.Channel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.receiveAsFlow
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import com.vkasurinen.notemark.notes.domain.repository.NotesRepository
import com.vkasurinen.notemark.core.domain.util.Result
import com.vkasurinen.notemark.core.presentation.util.UiText
import com.vkasurinen.notemark.notes.domain.Note
import timber.log.Timber
import java.time.Instant
import java.util.UUID

class NotesViewModel(
    private val notesRepository: NotesRepository,
) : ViewModel() {

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
        Timber.d("NotesViewModel onAction called with: $action")
        when (action) {
            is NotesAction.UpdateUsername -> {
                _state.update { it.copy(username = action.username) }
            }

            NotesAction.CreateNewNote -> {
                Timber.d("Creating new note")
                createNewNote()
            }
        }
    }

    fun loadNotes() {
        viewModelScope.launch {
            try {
                val result = notesRepository.getNotes(page = 1, size = 20)
                if (result is Result.Success) {
                    _state.update { it.copy(notes = result.data ?: emptyList()) }
                } else {
                    eventChannel.send(NotesEvent.Error(UiText.Dynamic("Failed to load notes")))
                }
            } catch (e: Exception) {
                eventChannel.send(NotesEvent.Error(UiText.Dynamic("Unknown error")))
            }
        }
    }

    private fun createNewNote() {
        Timber.d("createNewNote invoked")
        viewModelScope.launch {
            try {
                val newNote = Note(
                    id = UUID.randomUUID().toString(),
                    title = "New Note",
                    content = "",
                    createdAt = Instant.now().toString(),
                    lastEditedAt = Instant.now().toString()
                )
                val response = notesRepository.createNote(newNote)
                if (response is Result.Success && response.data != null) {
                    _state.update { currentState ->
                        currentState.copy(notes = currentState.notes + response.data)
                    }
                    loadNotes()
                    eventChannel.send(NotesEvent.NavigateToDetail(response.data.id))
                } else {
                    eventChannel.send(NotesEvent.Error(UiText.Dynamic("Failed to create note")))
                }
            } catch (e: Exception) {
                eventChannel.send(NotesEvent.Error(UiText.Dynamic("Unknown error")))
            }
        }
    }
}