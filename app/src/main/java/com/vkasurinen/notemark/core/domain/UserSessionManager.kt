package com.vkasurinen.notemark.core.domain

interface UserSessionManager {
    suspend fun getCurrentUsername(): String?
}