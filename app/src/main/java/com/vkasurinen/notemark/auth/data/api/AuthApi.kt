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

class AuthApi(private val httpClient: HttpClient) {

    suspend fun register(request: RegisterRequest): HttpResponse {
        return httpClient.post("https://notemark.pl-coding.com/api/auth/register") {
            contentType(ContentType.Application.Json)
            setBody(request)
        }
    }

    suspend fun login(request: LoginRequest): LoginResponse {
        return httpClient.post("https://notemark.pl-coding.com/api/auth/login") {
            setBody(request)
        }.body()
    }
}