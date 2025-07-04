package com.vkasurinen.notemark.notes.presentation.notes_details.reader_details

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.vkasurinen.notemark.core.domain.util.Result
import com.vkasurinen.notemark.core.presentation.util.UiText
import com.vkasurinen.notemark.notes.domain.repository.NotesRepository
import com.vkasurinen.notemark.notes.presentation.notes_details.view_details.ViewDetailEvent
import kotlinx.coroutines.Job
import kotlinx.coroutines.channels.Channel
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.onStart
import kotlinx.coroutines.flow.receiveAsFlow
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import timber.log.Timber
import java.text.SimpleDateFormat
import java.util.Locale
import java.util.TimeZone

class ReaderViewModel(
    private val notesRepository: NotesRepository,
    private val noteId: String
) : ViewModel() {

    private val _uiVisible = MutableStateFlow(false)
    val uiVisible = _uiVisible.asStateFlow()

    private val _state = MutableStateFlow(ReaderState())
    val state = _state.asStateFlow()

    private var autoHideJob: Job? = null

    private val eventChannel = Channel<ReaderEvent>()
    val events = eventChannel.receiveAsFlow()

    init {
        loadNoteDetails()
    }


    private fun resetAutoHideTimer() {
        autoHideJob?.cancel()
        autoHideJob = viewModelScope.launch {
            delay(3_000)
            _state.value = state.value.copy(uiVisible = false)
        }
    }


    private fun toggleUI() {
        val current = state.value
        _state.value = current.copy(uiVisible = !current.uiVisible)
        resetAutoHideTimer()
    }


    fun onScrollOrInteraction() {
        autoHideJob?.cancel()
        autoHideJob = viewModelScope.launch {
            delay(5000)
            _uiVisible.value = false
        }
    }

    fun onAction(action: ReaderAction) {
        when (action) {
            ReaderAction.NavigateBack -> {
                viewModelScope.launch {
                    eventChannel.send(ReaderEvent.NavigateBack)
                }
            }
            ReaderAction.NavigateToEdit -> {
                viewModelScope.launch {
                    eventChannel.send(ReaderEvent.NavigateToEditDetail)
                }
            }
            ReaderAction.NavigateToReader -> {
                viewModelScope.launch {
                    eventChannel.send(ReaderEvent.NavigateToReaderDetail)
                }
            }

            ReaderAction.ToggleUI -> {
                toggleUI()
            }
        }
    }

    private fun loadNoteDetails() {
        viewModelScope.launch {
            try {
                val result = notesRepository.getNotes(page = 1, size = 20)
                if (result is Result.Success) {
                    val note = result.data?.find { it.id == noteId }
                    if (note != null) {
                        _state.update {
                            it.copy(
                                id = note.id,
                                title = note.title,
                                content = note.content,
                                createdAt = formatISODate(note.createdAt),
                                lastEditedAt = formatISODate(note.lastEditedAt)
                            )
                        }
                    } else {
                        eventChannel.send(ReaderEvent.Error(UiText.Dynamic("Note not found")))
                    }
                } else {
                    eventChannel.send(ReaderEvent.Error(UiText.Dynamic("Failed to load note")))
                }
            } catch (e: Exception) {
                Timber.e(e, "Error loading note details")
                eventChannel.send(ReaderEvent.Error(UiText.Dynamic("Unknown error occurred")))
            }
        }
    }

    private fun formatISODate(isoDate: String): String {
        return try {
            val isoFormat = SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss.SSS'Z'", Locale.getDefault())
            isoFormat.timeZone = TimeZone.getTimeZone("UTC")
            val date = isoFormat.parse(isoDate)

            val displayFormat = SimpleDateFormat("dd MMM yyyy, HH:mm", Locale.getDefault())
            displayFormat.format(date ?: return isoDate)
        } catch (e: Exception) {
            Timber.e(e, "Error formatting date")
            isoDate
        }
    }

}