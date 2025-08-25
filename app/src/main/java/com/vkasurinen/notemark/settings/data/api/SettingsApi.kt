package com.vkasurinen.notemark.settings.data.api

import io.ktor.client.HttpClient
import io.ktor.client.request.post
import io.ktor.client.request.setBody
import io.ktor.http.ContentType
import io.ktor.http.contentType
import timber.log.Timber

class SettingsApi(
    private val httpClient: HttpClient
) {
    suspend fun logout(refreshToken: String) {
        try {
            httpClient.post("https://notemark-backend-650928036735.europe-north2.run.app/api/auth/logout") {
                contentType(ContentType.Application.Json)
                setBody(mapOf("refreshToken" to refreshToken))
            }
        } catch (e: Exception) {
            Timber.e(e, "Logout API call failed")
            throw e
        }
    }
}