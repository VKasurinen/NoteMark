package com.vkasurinen.notemark.settings.presentation.components

enum class SyncInterval(val minutes: Int?) {
    MANUAL(null),
    FIVE_MINUTES(5),
    FIFTEEN_MINUTES(15),
    HOURLY(60);

    override fun toString(): String {
        return when (this) {
            MANUAL -> "Manual"
            FIVE_MINUTES -> "Five minutes"
            FIFTEEN_MINUTES -> "Fifteen minutes"
            HOURLY -> "Hourly"
        }
    }
}

