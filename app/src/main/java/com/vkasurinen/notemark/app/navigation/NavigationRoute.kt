package com.vkasurinen.notemark.app.navigation

import kotlinx.serialization.Serializable

interface NavigationRoute {
    val route: String

    @Serializable
    data object Landing : NavigationRoute {
        override val route: String = "landing"
    }

    @Serializable
    data object Login : NavigationRoute {
        override val route: String = "login"
    }

    @Serializable
    data object Register : NavigationRoute {
        override val route: String = "register"
    }

    @Serializable
    data object Notes : NavigationRoute {
        override val route: String = "notes"
        fun createRoute(username: String) = "$route?username=$username"
    }

    @Serializable
    data object Detail : NavigationRoute {
        override val route: String = "details"
    }
}