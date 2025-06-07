package com.vkasurinen.notemark.core.data.networking

import com.vkasurinen.notemark.BuildConfig
import com.vkasurinen.notemark.auth.data.responses.LoginResponse
import com.vkasurinen.notemark.core.domain.AuthInfo
import com.vkasurinen.notemark.core.domain.SessionStorage
import io.ktor.client.HttpClient
import io.ktor.client.call.body
import io.ktor.client.engine.HttpClientEngine
import io.ktor.client.engine.cio.CIO
import io.ktor.client.engine.okhttp.OkHttp
import io.ktor.client.plugins.auth.Auth
import io.ktor.client.plugins.auth.providers.BearerTokens
import io.ktor.client.plugins.auth.providers.bearer
import io.ktor.client.plugins.contentnegotiation.ContentNegotiation
import io.ktor.client.plugins.defaultRequest
import io.ktor.client.plugins.logging.LogLevel
import io.ktor.client.plugins.logging.Logger
import io.ktor.client.plugins.logging.Logging
import io.ktor.client.request.header
import io.ktor.client.request.post
import io.ktor.client.request.setBody
import io.ktor.http.ContentType
import io.ktor.http.contentType
import io.ktor.serialization.kotlinx.json.json
import kotlinx.serialization.json.Json
import okhttp3.OkHttpClient
import timber.log.Timber

class HttpClientFactory(
    private val sessionStorage: SessionStorage
) {

    fun build(engine: HttpClientEngine = OkHttp.create()): HttpClient {
        return HttpClient(engine) {
            install(ContentNegotiation) {
                json(
                    json = Json {
                        ignoreUnknownKeys = true
                    }
                )
            }
            install(Logging) {
                logger = object : Logger {
                    override fun log(message: String) {
                        Timber.d(message)
                    }
                }
                level = LogLevel.ALL
            }
            install(Auth) {
                bearer {
                    loadTokens {
                        val tokenPair = sessionStorage.get()
                        BearerTokens(
                            accessToken = tokenPair?.accessToken ?: "",
                            refreshToken = tokenPair?.refreshToken ?: ""
                        )
                    }

                    refreshTokens {
                        val tokenPair = sessionStorage.get()
                        try {
                            val response: LoginResponse = client.post("https://notemark.pl-coding.com/api/auth/refresh") {
                                contentType(ContentType.Application.Json)
                                setBody(
                                    RefreshTokenRequest(
                                        refreshToken = tokenPair?.refreshToken ?: ""
                                    )
                                )
//                                header("Debug", true)
                            }.body()

                            val newAuthInfo = AuthInfo(
                                accessToken = response.accessToken,
                                refreshToken = response.refreshToken
                            )
                            sessionStorage.set(newAuthInfo)

                            BearerTokens(
                                accessToken = newAuthInfo.accessToken,
                                refreshToken = newAuthInfo.refreshToken
                            )
                        } catch (e: Exception) {
                            Timber.e(e, "Failed to refresh token")
                            BearerTokens(
                                accessToken = "",
                                refreshToken = ""
                            )
                        }
                    }
                }
            }
            defaultRequest {
                header("X-User-Email", BuildConfig.USER_EMAIL)
            }
        }
    }
}