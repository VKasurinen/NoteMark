package com.vkasurinen.notemark.notes.presentation.notes_details.reader_details

data class ReaderState(
    val id: String = "",
    val title: String = "Note title",
    val content: String = "",
    val createdAt: String = "",
    val lastEditedAt: String = "",
    val uiVisible: Boolean = true,
)