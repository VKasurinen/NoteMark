package com.vkasurinen.notemark.notes.presentation.notes_details.view_details

data class ViewDetailState(
    val id: String = "",
    val title: String = "Note title",
    val content: String = "",
    val createdAt: String = "",
    val lastEditedAt: String = "",
)