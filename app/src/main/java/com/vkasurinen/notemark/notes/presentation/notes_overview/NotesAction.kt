package com.vkasurinen.notemark.notes.presentation.notes_overview

import com.vkasurinen.notemark.notes.domain.Note

sealed interface NotesAction {
    data class UpdateUsername(val username: String) : NotesAction
    data object CreateNewNote : NotesAction
    data class NavigateToEditDetail(val noteId: String) : NotesAction
    data class NavigateToViewDetail(val noteId: String) : NotesAction
    data object NavigateToSettings : NotesAction
    data class DeleteNote(val noteId: String) : NotesAction
    data class ShowDeleteDialog(val note: Note) : NotesAction
    data class DismissDeleteDialog(val note: Note) : NotesAction
}