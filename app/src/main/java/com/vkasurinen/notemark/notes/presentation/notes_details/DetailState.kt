package com.vkasurinen.notemark.notes.presentation.notes_details

data class DetailState(
    val id: String = "",
    val title: String = "Note title",
    val content: String = "",
    val originalTitle: String = "",
    val originalContent: String = "",
    val createdAt: String = "",
    val lastEditedAt: String = "",
    val showDiscardDialog: Boolean = false
)
