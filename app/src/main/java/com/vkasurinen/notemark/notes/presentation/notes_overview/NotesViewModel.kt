package com.vkasurinen.notemark.notes.presentation.notes_overview

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.vkasurinen.notemark.notes.data.requests.NoteRequest
import kotlinx.coroutines.channels.Channel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.receiveAsFlow
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import com.vkasurinen.notemark.notes.domain.repository.NotesRepository
import com.vkasurinen.notemark.core.domain.util.Result
import java.time.Instant
import java.util.UUID

class NotesViewModel(
    private val notesRepository: NotesRepository
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
        when (action) {
            is NotesAction.UpdateUsername -> {
                _state.update { it.copy(username = action.username) }
            }

            NotesAction.CreateNewNote -> createNewNote { newNoteId ->
                // Handle the created note ID, e.g., navigate to the detail screen
            }
        }
    }

    private fun createNewNote(onNoteCreated: (String) -> Unit) {
        viewModelScope.launch {
            try {
                val newNote = NoteRequest(
                    id = UUID.randomUUID().toString(),
                    title = "New Note",
                    content = "",
                    createdAt = Instant.now().toString(),
                    lastEditedAt = Instant.now().toString()
                )
                val response = notesRepository.createNote(newNote)
                if (response is Result.Success && response.data != null) {
                    _state.update { currentState ->
                        currentState.copy(notes = currentState.notes + Note(
                            date = response.data.createdAt,
                            title = response.data.title,
                            description = response.data.content
                        ))
                    }
                    onNoteCreated(response.data.id)
                }
            } catch (e: Exception) {
                // Handle error
            }
        }
    }
}