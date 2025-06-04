package com.vkasurinen.notemark.app.navigation

import kotlinx.serialization.Serializable

interface NavigationRoute {
    val route: String

    @Serializable
    data object Login : NavigationRoute {
        override val route: String = "login"
    }

    @Serializable
    data object Register : NavigationRoute {
        override val route: String = "register"
    }
}