package com.vkasurinen.notemark.auth.data.repository

import com.vkasurinen.notemark.auth.data.responses.LoginResponse
import com.vkasurinen.notemark.core.domain.util.Result
import com.vkasurinen.notemark.auth.data.api.AuthApi
import com.vkasurinen.notemark.auth.data.requests.LoginRequest
import com.vkasurinen.notemark.auth.data.requests.RegisterRequest
import com.vkasurinen.notemark.auth.domain.repository.AuthRepository

class AuthRepositoryImpl(
    private val authApi: AuthApi,
//    private val sessionStorage: SessionStorage
) : AuthRepository {

    override suspend fun login(
        email: String,
        password: String
    ): Result<LoginResponse> {
        return try {
            val response = authApi.login(
                LoginRequest(
                    email = email,
                    password = password
                )
            )
            Result.Success(response)
        } catch (e: Exception) {
            Result.Error(e.message ?: "An error occurred")
        }
    }

    override suspend fun register(
        username: String,
        email: String,
        password: String
    ): Result<Unit> {
        return try {
            authApi.register(
                RegisterRequest(
                    email = email,
                    username = username,
                    password = password
                )
            )
            Result.Success(Unit)
        } catch (e: Exception) {
            Result.Error(e.message ?: "An error occurred")
        }
    }


}