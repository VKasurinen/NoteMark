package com.vkasurinen.notemark.notes.presentation.notes_details

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.vkasurinen.notemark.core.domain.util.Result
import com.vkasurinen.notemark.notes.domain.Note
import com.vkasurinen.notemark.notes.domain.repository.NotesRepository
import kotlinx.coroutines.channels.Channel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.receiveAsFlow
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch

class DetailViewModel(
    private val notesRepository: NotesRepository,
    private val noteId: String
) : ViewModel() {

    private val _state = MutableStateFlow(DetailState())
    val state = _state
        .stateIn(
            scope = viewModelScope,
            started = SharingStarted.WhileSubscribed(5_000L),
            initialValue = DetailState()
        )

    private val eventChannel = Channel<DetailEvent>()
    val events get() = eventChannel.receiveAsFlow()

    init {
        if (noteId.isNotBlank()) {
            loadNoteDetails(noteId)
        }
    }

    fun onAction(action: DetailAction) {
        when (action) {
            DetailAction.OnSaveClick -> {
                viewModelScope.launch {
                    val currentState = _state.value

                    if (currentState.title.isBlank() || currentState.content.isBlank()) {
                        eventChannel.send(DetailEvent.ShowValidationError("Content cannot be empty"))
                        return@launch
                    }

                    val updatedNote = Note(
                        id = noteId,
                        title = currentState.title,
                        content = currentState.content,
                        createdAt = currentState.createdAt,
                        lastEditedAt = getCurrentTimestamp()
                    )

                    val result = notesRepository.updateNote(updatedNote)
                    if (result is Result.Success) {
                        eventChannel.send(DetailEvent.NavigateToNotes)
                    } else {
                        eventChannel.send(DetailEvent.ShowValidationError("Failed to update note"))
                    }
                }
            }

            is DetailAction.OnTitleChange -> {
                _state.update { it.copy(title = action.title) }
            }
            is DetailAction.OnContentChange -> {
                _state.update { it.copy(content = action.content) }
            }

            DetailAction.OnBackClick -> {
                // Handle back navigation logic
            }
        }
    }

    fun loadNoteDetails(noteId: String) {
        viewModelScope.launch {
            try {
                val result = notesRepository.getNotes(1, 1)
                if (result is Result.Success) {
                    val note = result.data?.firstOrNull { it.id == noteId }
                    if (note != null) {
                        _state.update {
                            it.copy(
                                id = note.id,
                                title = note.title,
                                content = note.content,
                                originalTitle = note.title,
                                originalContent = note.content,
                                createdAt = note.createdAt,
                                lastEditedAt = note.lastEditedAt
                            )
                        }
                    }
                }
            } catch (e: Exception) {
                // Handle error
            }
        }
    }

    private fun getCurrentTimestamp(): String {
        return java.time.Instant.now().toString()
    }
}