package com.vkasurinen.notemark.notes.presentation.notes_details.edit_details

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.vkasurinen.notemark.core.domain.util.Result
import com.vkasurinen.notemark.notes.domain.Note
import com.vkasurinen.notemark.notes.domain.repository.NotesRepository
import com.vkasurinen.notemark.notes.presentation.notes_details.view_details.ViewDetailAction
import com.vkasurinen.notemark.notes.presentation.notes_details.view_details.ViewDetailEvent
import kotlinx.coroutines.channels.Channel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.receiveAsFlow
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import timber.log.Timber

class EditDetailViewModel(
    private val notesRepository: NotesRepository,
    private val noteId: String
) : ViewModel() {

    private val _state = MutableStateFlow(EditDetailState())
    val state = _state
        .stateIn(
            scope = viewModelScope,
            started = SharingStarted.WhileSubscribed(5_000L),
            initialValue = EditDetailState()
        )

    private val eventChannel = Channel<EditDetailEvent>()
    val events get() = eventChannel.receiveAsFlow()

    init {
        if (noteId.isNotBlank()) {
            loadNoteDetails(noteId)
        }
    }

    fun onAction(action: EditDetailAction) {
        when (action) {
            EditDetailAction.OnSaveClick -> {
                viewModelScope.launch {
                    val currentState = _state.value

                    if (currentState.title.isBlank() || currentState.content.isBlank()) {
                        eventChannel.send(EditDetailEvent.ShowValidationError("Content cannot be empty"))
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
                        eventChannel.send(EditDetailEvent.NavigateToViewDetail)
                    } else {
                        eventChannel.send(EditDetailEvent.ShowValidationError("Failed to update note"))
                    }
                }
            }

            is EditDetailAction.OnTitleChange -> {
                _state.update { it.copy(title = action.title) }
            }
            is EditDetailAction.OnContentChange -> {
                _state.update { it.copy(content = action.content) }
            }

            EditDetailAction.NavigateToReader -> {
                viewModelScope.launch {
                    eventChannel.send(EditDetailEvent.NavigateToReaderDetail)
                }
            }
        }
    }

    private fun loadNoteDetails(noteId: String) {
        viewModelScope.launch {
            try {
                Timber.d("Loading details for note ID: $noteId")
                val result = notesRepository.getNotes(1, 20)
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
                    } else {
                        Timber.e("Note with ID $noteId not found")
                    }
                } else {
                    Timber.e("Failed to fetch notes: ${result.message}")
                }
            } catch (e: Exception) {
                Timber.e(e, "Error loading note details")
            }
        }
    }

    private fun getCurrentTimestamp(): String {
        return java.time.Instant.now().toString()
    }
}