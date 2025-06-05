package com.vkasurinen.notemark.core.data.networking

data class RefreshTokenResponse(
    val accessToken: String,
    val refreshToken: String
)
