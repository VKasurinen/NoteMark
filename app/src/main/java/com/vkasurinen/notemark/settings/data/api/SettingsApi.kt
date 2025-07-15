package com.vkasurinen.notemark.settings.data.api

import com.vkasurinen.notemark.notes.data.requests.NoteRequest
import com.vkasurinen.notemark.notes.data.requests.NoteResponse
import com.vkasurinen.notemark.notes.data.requests.PaginatedNotesResponse
import io.ktor.client.HttpClient
import io.ktor.client.call.body
import io.ktor.client.request.delete
import io.ktor.client.request.get
import io.ktor.client.request.parameter
import io.ktor.client.request.post
import io.ktor.client.request.put
import io.ktor.client.request.setBody
import io.ktor.http.ContentType
import io.ktor.http.contentType
import timber.log.Timber

class SettingsApi(
    private val httpClient: HttpClient
) {
    suspend fun logout(refreshToken: String) {
        try {
            httpClient.post("https://notemark.pl-coding.com/api/auth/logout") {
                contentType(ContentType.Application.Json)
                setBody(mapOf("refreshToken" to refreshToken))
            }
        } catch (e: Exception) {
            Timber.e(e, "Logout API call failed")
            throw e
        }
    }
}