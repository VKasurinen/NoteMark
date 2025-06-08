package com.vkasurinen.notemark.auth.data.api

import com.vkasurinen.notemark.auth.data.requests.LoginRequest
import com.vkasurinen.notemark.auth.data.requests.RegisterRequest
import com.vkasurinen.notemark.auth.data.responses.LoginResponse
import io.ktor.client.*
import io.ktor.client.call.*
import io.ktor.client.request.*
import io.ktor.client.statement.*
import io.ktor.http.ContentType
import io.ktor.http.contentType
import timber.log.Timber

class AuthApi(
    private val httpClient: HttpClient
) {


    suspend fun register(request: RegisterRequest): HttpResponse {
        return try {
            val response = httpClient.post("https://notemark.pl-coding.com/api/auth/register") {
                contentType(ContentType.Application.Json)
                setBody(request)
            }
            Timber.tag("AuthApi").d("Request: $request, Response: ${response.status}")
            response
        } catch (e: Exception) {
            Timber.tag("AuthApi").e("Error: ${e.message}")
            throw e
        }
    }

    suspend fun login(request: LoginRequest): LoginResponse {
        return try {
            Timber.tag("AuthApi").d("Sending login request: $request")
            val response: LoginResponse = httpClient.post("https://notemark.pl-coding.com/api/auth/login") {
                contentType(ContentType.Application.Json)
                setBody(request)
            }.body()
            Timber.tag("AuthApi").d("Login successful")
            response
        } catch (e: Exception) {
            Timber.tag("AuthApi").e(e, "Login failed")
            throw e
        }
    }
}