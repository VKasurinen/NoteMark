package com.vkasurinen.notemark.notes.domain

data class Note(
    val id: String,
    val title: String,
    val content: String,
    val createdAt: String,
    val lastEditedAt: String
)
