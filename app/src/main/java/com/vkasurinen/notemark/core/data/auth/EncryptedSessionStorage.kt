package com.vkasurinen.notemark.core.data.auth

import android.content.Context
import androidx.datastore.core.DataStore
import androidx.datastore.preferences.core.Preferences
import androidx.datastore.preferences.core.edit
import androidx.datastore.preferences.core.stringPreferencesKey
import androidx.datastore.preferences.preferencesDataStore
import com.vkasurinen.notemark.core.domain.AuthInfo
import com.vkasurinen.notemark.core.domain.SessionStorage
import kotlinx.coroutines.flow.first
import kotlinx.serialization.decodeFromString
import kotlinx.serialization.encodeToString
import kotlinx.serialization.json.Json

class EncryptedSessionStorage(
    private val context: Context
) : SessionStorage {

    private val Context.dataStore: DataStore<Preferences> by preferencesDataStore(name = "auth_info")

    override suspend fun get(): AuthInfo? {
        val preferences = context.dataStore.data.first()
        val json = preferences[KEY_AUTH_INFO]
        return json?.let {
            Json.decodeFromString<AuthInfoSerializable>(it).toAuthInfo()
        }
    }

    override suspend fun set(info: AuthInfo?) {
        context.dataStore.edit { preferences ->
            if (info == null) {
                preferences.remove(KEY_AUTH_INFO)
            } else {
                val json = Json.encodeToString(info.toAuthInfoSerializable())
                preferences[KEY_AUTH_INFO] = json
            }
        }
    }

    companion object {
        private val KEY_AUTH_INFO = stringPreferencesKey("KEY_AUTH_INFO")
    }
}