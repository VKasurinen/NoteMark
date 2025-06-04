package com.vkasurinen.notemark.app.navigation

import androidx.compose.runtime.Composable
import androidx.navigation.NavHostController
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import com.vkasurinen.notemark.auth.presentation.landing.LandingRoot
import com.vkasurinen.notemark.auth.presentation.register.RegisterRoot

@Composable
fun NavigationRoot(
    navController: NavHostController
) {
    NavHost(
        navController = navController,
        startDestination = NavigationRoute.Login.route
    ) {
        composable(NavigationRoute.Login.route) {
            LandingRoot(
                navController = navController
            )
        }
        composable(NavigationRoute.Register.route) {
            RegisterRoot()
        }
    }
}