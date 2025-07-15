package com.vkasurinen.notemark.settings.data.repository

import com.vkasurinen.notemark.core.database.dao.NoteDao
import com.vkasurinen.notemark.settings.data.api.SettingsApi
import com.vkasurinen.notemark.settings.domain.repository.SettingsRepository
import timber.log.Timber

class SettingsRepositoryImpl(
    private val noteDao: NoteDao,
    private val settingsApi: SettingsApi
): SettingsRepository {

    override suspend fun logout(refreshToken: String) {
        try {
            settingsApi.logout(refreshToken)
        } catch (e: Exception) {
            Timber.e(e, "Logout API call failed")
        }
    }

    override suspend fun clearLocalDatabase() {
        try {
            noteDao.deleteAllNotes()
        } catch (e: Exception) {
            Timber.e(e, "Failed to clear local database")
        }
    }
}