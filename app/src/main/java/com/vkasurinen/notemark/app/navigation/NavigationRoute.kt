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
    data object ViewDetail : NavigationRoute {
        override val route: String = "viewDetails"
    }

    @Serializable
    data object EditDetail : NavigationRoute {
        override val route: String = "editDetails"
    }

    @Serializable
    data object ReaderDetail : NavigationRoute {
        override val route: String = "readerDetails"
    }

    @Serializable
    data object Settings : NavigationRoute {
        override val route: String = "settings"
    }
}