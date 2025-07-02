package com.vkasurinen.notemark.notes.presentation.settings

data class SettingsState(
    val paramOne: String = "default",
    val paramTwo: List<String> = emptyList(),
)