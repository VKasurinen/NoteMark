package com.vkasurinen.notemark.app.navigation

import androidx.compose.runtime.Composable
import androidx.glance.text.Text
import androidx.navigation.NavHostController
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import com.vkasurinen.notemark.auth.presentation.landing.LandingScreen
import com.vkasurinen.notemark.auth.presentation.landing.LandingScreenRoot
import com.vkasurinen.notemark.auth.presentation.login.LoginScreen
import com.vkasurinen.notemark.auth.presentation.login.LoginScreenRoot
import com.vkasurinen.notemark.auth.presentation.register.RegisterScreenRoot

@Composable
fun NavigationRoot(
    navController: NavHostController
) {
    NavHost(
        navController = navController,
        startDestination = NavigationRoute.Landing.route
    ) {
        composable(NavigationRoute.Landing.route) {
            LandingScreenRoot(
                navController = navController
            )
        }
        composable(NavigationRoute.Register.route) {
            RegisterScreenRoot(
                navController = navController
            )
        }
        composable(NavigationRoute.Login.route) {
            LoginScreenRoot(
                navController = navController
            )
        }
        composable("blank") {
            Text("In blank screen")
        }
    }
}