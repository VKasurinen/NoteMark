package com.vkasurinen.notemark.app.navigation

import androidx.compose.runtime.Composable
import androidx.navigation.NavHostController
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import com.vkasurinen.notemark.auth.presentation.landing.LandingRoot
import com.vkasurinen.notemark.auth.presentation.login.LoginScreen
import com.vkasurinen.notemark.auth.presentation.login.LoginScreenRoot
import com.vkasurinen.notemark.auth.presentation.register.RegisterRoot

@Composable
fun NavigationRoot(
    navController: NavHostController
) {
    NavHost(
        navController = navController,
        startDestination = NavigationRoute.Landing.route
    ) {
        composable(NavigationRoute.Landing.route) {
            LandingRoot(
                navController = navController
            )
        }
        composable(NavigationRoute.Register.route) {
            RegisterRoot(
                navController = navController
            )
        }
        composable(NavigationRoute.Login.route) {
            LoginScreenRoot(
                navController = navController
            )
        }
    }
}