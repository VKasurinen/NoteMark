package com.vkasurinen.notemark.app.navigation

import androidx.compose.runtime.Composable
import androidx.navigation.NavHostController
import androidx.navigation.NavType
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.navArgument
import com.vkasurinen.notemark.auth.presentation.landing.LandingScreenRoot
import com.vkasurinen.notemark.auth.presentation.login.LoginScreenRoot
import com.vkasurinen.notemark.auth.presentation.register.RegisterScreenRoot
import com.vkasurinen.notemark.notes.presentation.notes_details.DetailRoot
import com.vkasurinen.notemark.notes.presentation.notes_overview.NotesScreenRoot

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
        composable(NavigationRoute.Notes.route) {
            NotesScreenRoot(navController = navController)
        }

        composable(
            route = "${NavigationRoute.Detail.route}/{noteId}",
            arguments = listOf(navArgument("noteId") { type = NavType.StringType })
        ) { backStackEntry ->
            val noteId = backStackEntry.arguments?.getString("noteId") ?: return@composable
            DetailRoot(navController = navController, noteId = noteId)
        }


    }
}