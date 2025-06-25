package com.vkasurinen.notemark.notes.presentation.notes_details

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.vkasurinen.notemark.core.database.mappers.toRequest
import com.vkasurinen.notemark.core.domain.util.Result
import com.vkasurinen.notemark.notes.domain.Note
import com.vkasurinen.notemark.notes.domain.repository.NotesRepository
import kotlinx.coroutines.channels.Channel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.onStart
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
                        val message = if (currentState.title.isBlank()) {
                            "Title cannot be empty"
                        } else {
                            "Content cannot be empty"
                        }
                        eventChannel.send(DetailEvent.ShowValidationError(message))
                        return@launch
                    }

                    val updatedNote = Note(
                        id = currentState.id,
                        title = currentState.title,
                        content = currentState.content,
                        createdAt = currentState.createdAt,
                        lastEditedAt = getCurrentTimestamp()
                    )

                    notesRepository.updateNote(updatedNote.toRequest())
                    eventChannel.send(DetailEvent.NavigateToNotes)
                }
            }


            is DetailAction.OnTitleChange -> {
                _state.update { it.copy(title = action.title) }
            }
            is DetailAction.OnContentChange -> {
                _state.update { it.copy(content = action.content) }
            }

            DetailAction.OnBackClick -> TODO()
        }
    }

    fun loadNoteDetails(noteId: String) {
        viewModelScope.launch {
            try {
                val response = notesRepository.getNotes(page = 1, size = Int.MAX_VALUE)
                if (response is Result.Success) {
                    val note = response.data?.notes?.find { it.id == noteId }
                    note?.let {
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