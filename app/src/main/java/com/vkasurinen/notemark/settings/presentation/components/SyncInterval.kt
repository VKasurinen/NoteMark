package com.vkasurinen.notemark.settings.presentation.components

enum class SyncInterval(val minutes: Int?) {
    MANUAL(null),
    FIVE_MINUTES(5),
    FIFTEEN_MINUTES(15),
    HOURLY(60)
}

