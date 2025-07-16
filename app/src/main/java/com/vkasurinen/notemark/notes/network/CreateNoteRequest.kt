package com.vkasurinen.notemark.notes.network

data class CreateNoteRequest(
    val id: String,
    val title: String,
    val content: String,
    val createdAt: String,
)
