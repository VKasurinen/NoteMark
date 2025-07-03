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
import com.vkasurinen.notemark.notes.presentation.notes_details.edit_details.EditDetailScreenRoot
import com.vkasurinen.notemark.notes.presentation.notes_details.view_details.ViewDetailScreenRoot
import com.vkasurinen.notemark.notes.presentation.notes_overview.NotesScreenRoot
import com.vkasurinen.notemark.settings.SettingsScreenRoot

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
            route = "${NavigationRoute.ViewDetail.route}/{noteId}",
            arguments = listOf(navArgument("noteId") { type = NavType.StringType })
        ) { backStackEntry ->
            val noteId = backStackEntry.arguments?.getString("noteId") ?: return@composable
            ViewDetailScreenRoot(navController = navController, noteId = noteId)
        }

        composable(
            route = "${NavigationRoute.EditDetail.route}/{noteId}",
            arguments = listOf(navArgument("noteId") { type = NavType.StringType })
        ) { backStackEntry ->
            val noteId = backStackEntry.arguments?.getString("noteId") ?: return@composable
            EditDetailScreenRoot(navController = navController, noteId = noteId)
        }

        composable(NavigationRoute.Settings.route) {
            SettingsScreenRoot(navController = navController)
        }


    }
}