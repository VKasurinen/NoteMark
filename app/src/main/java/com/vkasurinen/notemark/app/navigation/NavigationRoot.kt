package com.vkasurinen.notemark.app.navigation

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.navigation.NavHostController
import androidx.navigation.NavType
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.navArgument
import com.vkasurinen.notemark.auth.presentation.landing.LandingScreenRoot
import com.vkasurinen.notemark.auth.presentation.login.LoginScreenRoot
import com.vkasurinen.notemark.auth.presentation.register.RegisterScreenRoot
import com.vkasurinen.notemark.notes.presentation.NotesScreenRoot

@Composable
fun NavigationRoot(
    navController: NavHostController
) {
    NavHost(
        navController = navController,
        startDestination = NavigationRoute.Landing.route
    ) {
        composable(NavigationRoute.Landing.route) {
            LandingScreenRoot(navController = navController)
        }
        composable(NavigationRoute.Register.route) {
            RegisterScreenRoot(navController = navController)
        }
        composable(NavigationRoute.Login.route) {
            LoginScreenRoot(navController = navController)
        }
        composable(
            route = "${NavigationRoute.Notes.route}?username={username}",
            arguments = listOf(
                navArgument("username") {
                    type = NavType.StringType
                    nullable = true
                    defaultValue = null
                }
            )
        ) { backStackEntry ->
            NotesScreenRoot(
                navController = navController,
                username = backStackEntry.arguments?.getString("username")
            )
        }
    }
}