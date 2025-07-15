package com.vkasurinen.notemark.settings.domain.repository

interface SettingsRepository {
    suspend fun logout(refreshToken: String)
    suspend fun clearLocalDatabase()
}