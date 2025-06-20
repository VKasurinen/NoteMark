package com.vkasurinen.notemark.notes.presentation.components

fun getUserInitials(username: String): String {
    val words = username.trim().split(" ")
    return when (words.size) {
        1 -> words[0].take(2).uppercase() // Single-word name
        else -> "${words.first().first()}${words.last().first()}".uppercase() // Two or more words
    }
}