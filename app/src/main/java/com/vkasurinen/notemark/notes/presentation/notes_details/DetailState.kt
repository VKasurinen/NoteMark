package com.vkasurinen.notemark.notes.presentation.notes_details

data class DetailState(
    val title: String = "",
    val content: String = "",
    val originalTitle: String = "",
    val originalContent: String = "",
    val showDiscardDialog: Boolean = false
)
