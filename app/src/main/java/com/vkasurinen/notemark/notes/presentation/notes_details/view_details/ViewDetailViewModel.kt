package com.vkasurinen.notemark.notes.presentation.notes_details.view_details

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.vkasurinen.notemark.core.presentation.util.UiText
import com.vkasurinen.notemark.core.domain.notes.NotesRepository
import kotlinx.coroutines.channels.Channel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.receiveAsFlow
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import timber.log.Timber
import java.text.SimpleDateFormat
import java.util.Locale
import java.util.TimeZone
import com.vkasurinen.notemark.core.domain.util.Result

class ViewDetailViewModel(
    private val notesRepository: NotesRepository,
    private val noteId: String
) : ViewModel() {

    private val _state = MutableStateFlow(ViewDetailState())
    val state = _state
        .stateIn(
            scope = viewModelScope,
            started = SharingStarted.WhileSubscribed(5_000L),
            initialValue = ViewDetailState()
        )

    private val eventChannel = Channel<ViewDetailEvent>()
    val events = eventChannel.receiveAsFlow()

    init {
        loadNoteDetails()
    }

    fun onAction(action: ViewDetailAction) {
        when (action) {
            ViewDetailAction.NavigateBack -> {
                viewModelScope.launch {
                    eventChannel.send(ViewDetailEvent.NavigateBack)
                }
            }
            ViewDetailAction.NavigateToEdit -> {
                viewModelScope.launch {
                    eventChannel.send(ViewDetailEvent.NavigateToEditDetail)
                }
            }
            ViewDetailAction.NavigateToReader -> {
                viewModelScope.launch {
                    eventChannel.send(ViewDetailEvent.NavigateToReaderDetail)
                }
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
                        eventChannel.send(ViewDetailEvent.Error(UiText.Dynamic("Note not found")))
                    }
                } else {
                    eventChannel.send(ViewDetailEvent.Error(UiText.Dynamic("Failed to load note")))
                }
            } catch (e: Exception) {
                Timber.e(e, "Error loading note details")
                eventChannel.send(ViewDetailEvent.Error(UiText.Dynamic("Unknown error occurred")))
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