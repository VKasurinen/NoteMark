package com.vkasurinen.notemark.notes.presentation.notes_details.edit_details

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.vkasurinen.notemark.core.domain.util.Result
import com.vkasurinen.notemark.core.domain.notes.NotesRepository
import com.vkasurinen.notemark.notes.domain.Note
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
            Timber.d("EditViewModel - Initializing ViewModel for noteId: $noteId")
            loadNoteDetails(noteId)
        } else {
            Timber.w("EditViewModel - Initialized with blank noteId")
        }
    }

    // Kotlin
    fun onAction(action: EditDetailAction) {
        Timber.d("EditViewModel - Processing action: ${action.javaClass.simpleName}")
        when (action) {
            EditDetailAction.OnSaveClick -> {
                viewModelScope.launch {
                    val currentState = _state.value
                    Timber.d("EditViewModel - Save triggered - Current state: title='${currentState.title}', content='${currentState.content}'")

                    if (currentState.title.isBlank() || currentState.content.isBlank()) {
                        Timber.w("EditViewModel - Validation failed - empty title or content")
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

                    println("Printed note: $updatedNote")

                    if (noteId.isBlank()) {
                        // Create new note
                        when (val result = notesRepository.createNote(updatedNote)) {
                            is Result.Success -> {
                                Timber.d("EditViewModel - Note creation successful")
                                _state.update { it.copy(id = result.data!!.id) }
                            }
                            is Result.Error -> {
                                Timber.e("EditViewModel - Note creation failed: ${result.message}")
                                eventChannel.send(EditDetailEvent.ShowValidationError("Failed to create note"))
                            }

                            is Result.Loading -> TODO()
                        }
                    } else {
                        // Update existing note
                        when (val result = notesRepository.updateNote(updatedNote)) {
                            is Result.Success -> {
                                Timber.d("EditViewModel - Note update successful")
                                eventChannel.send(EditDetailEvent.NavigateToViewDetail)
                            }
                            is Result.Error -> {
                                Timber.e("EditViewModel - Note update failed: ${result.message}")
                                eventChannel.send(EditDetailEvent.ShowValidationError("Failed to update note"))
                            }

                            is Result.Loading -> TODO()
                        }
                    }
                }
            }

            is EditDetailAction.OnTitleChange -> {
                Timber.d("EditViewModel - Title changed to: '${action.title}'")
                _state.update { it.copy(title = action.title) }
            }

            is EditDetailAction.OnContentChange -> {
                if (action.content != _state.value.content) {
                    Timber.d("EditViewModel - Content changed from '${_state.value.content}' to '${action.content}'")
                    _state.update { it.copy(content = action.content) }
                }
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
                Timber.d("EditViewModel - Loading details for note ID: $noteId")
                when (val result = notesRepository.getNoteById(noteId)) {
                    is Result.Success -> {
                        result.data?.let { note ->
                            Timber.d("EditViewModel - Found note: $note")
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
                        } ?: run {
                            Timber.e("EditViewModel - Note with ID $noteId not found")
                        }
                    }
                    is Result.Error -> {
                        Timber.e("EditViewModel - Failed to fetch note: ${result.message}")
                    }
                    is Result.Loading -> { /* handle loading */ }
                }
            } catch (e: Exception) {
                Timber.e(e, "EditViewModel - Error loading note details")
            }
        }
    }


    private fun getCurrentTimestamp(): String {
        return java.time.Instant.now().toString()
    }
}