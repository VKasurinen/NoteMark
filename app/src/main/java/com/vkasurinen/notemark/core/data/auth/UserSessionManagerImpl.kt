package com.vkasurinen.notemark.core.data.auth

import com.vkasurinen.notemark.core.domain.SessionStorage
import com.vkasurinen.notemark.core.domain.UserSessionManager

class UserSessionManagerImpl(
    private val sessionStorage: SessionStorage
) : UserSessionManager {
    override suspend fun getCurrentUsername(): String? {
        return sessionStorage.get()?.username
    }
}