package com.vkasurinen.notemark.auth.domain.repository

import com.vkasurinen.notemark.auth.data.responses.LoginResponse
import com.vkasurinen.notemark.core.domain.util.Result

interface AuthRepository {
    suspend fun login(email: String, password: String): Result<LoginResponse>
    suspend fun register(username: String, email: String, password: String): Result<Unit>
}