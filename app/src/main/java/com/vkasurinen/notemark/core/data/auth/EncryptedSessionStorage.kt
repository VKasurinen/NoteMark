package com.vkasurinen.notemark.core.data.auth

import android.content.Context
import android.util.Log
import androidx.datastore.core.DataStore
import androidx.datastore.preferences.core.Preferences
import androidx.datastore.preferences.core.edit
import androidx.datastore.preferences.core.stringPreferencesKey
import androidx.datastore.preferences.preferencesDataStore
import com.vkasurinen.notemark.core.domain.AuthInfo
import com.vkasurinen.notemark.core.domain.SessionStorage
import kotlinx.coroutines.flow.first
import kotlinx.serialization.json.Json
import timber.log.Timber

class EncryptedSessionStorage(
    private val context: Context
) : SessionStorage {

    private val Context.dataStore: DataStore<Preferences> by preferencesDataStore(name = "auth_info")

    override suspend fun get(): AuthInfo? {
        val preferences = context.dataStore.data.first()
        val stored = preferences[KEY_AUTH_INFO]
        return stored?.let {
            val json = if (it.startsWith(ENCRYPTED_PREFIX)) {
                Timber.tag("EncryptedSessionStorage").e("IN IF")
                val base64 = it.removePrefix(ENCRYPTED_PREFIX)
                EncryptionUtil.decrypt(base64)
            } else {
                Timber.tag("EncryptedSessionStorage").e("Fallbacking to else")
                set(Json.decodeFromString<AuthInfoSerializable>(it).toAuthInfo())
                it
            }
            Json.decodeFromString<AuthInfoSerializable>(json).toAuthInfo()
        }
    }

    override suspend fun set(info: AuthInfo?) {
        context.dataStore.edit { preferences ->
            if (info == null) {
                preferences.remove(KEY_AUTH_INFO)
            } else {
                val json = Json.encodeToString(info.toAuthInfoSerializable())
                val encrypted = EncryptionUtil.encrypt(json)
                preferences[KEY_AUTH_INFO] = "$ENCRYPTED_PREFIX$encrypted"
                Timber.tag("EncryptedSessionStorage").d("Encrypted field: $encrypted")
            }
        }
    }

    companion object {
        private val KEY_AUTH_INFO = stringPreferencesKey("KEY_AUTH_INFO")
        private const val ENCRYPTED_PREFIX = "ENC:"
    }
}