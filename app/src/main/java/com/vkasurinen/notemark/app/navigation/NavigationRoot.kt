package com.vkasurinen.notemark.app.navigation

import androidx.compose.runtime.Composable
import androidx.navigation.NavGraphBuilder
import androidx.navigation.NavHostController
import androidx.navigation.NavType
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.navArgument
import androidx.navigation.navigation
import com.vkasurinen.notemark.auth.presentation.landing.LandingScreenRoot
import com.vkasurinen.notemark.auth.presentation.login.LoginScreenRoot
import com.vkasurinen.notemark.auth.presentation.register.RegisterScreenRoot
import com.vkasurinen.notemark.notes.presentation.notes_details.edit_details.EditDetailScreenRoot
import com.vkasurinen.notemark.notes.presentation.notes_details.reader_details.ReaderScreenRoot
import com.vkasurinen.notemark.notes.presentation.notes_details.view_details.ViewDetailScreenRoot
import com.vkasurinen.notemark.notes.presentation.notes_overview.NotesScreenRoot
import com.vkasurinen.notemark.settings.SettingsScreenRoot


fun NavGraphBuilder.authGraph(navController: NavHostController) {
    navigation(
        startDestination = NavigationRoute.Landing.route,
        route = "auth"
    ) {
        composable(NavigationRoute.Landing.route) {
            LandingScreenRoot(navController)
        }
        composable(NavigationRoute.Login.route) {
            LoginScreenRoot(navController)
        }
        composable(NavigationRoute.Register.route) {
            RegisterScreenRoot(navController)
        }
    }
}

fun NavGraphBuilder.mainGraph(navController: NavHostController) {
    navigation(
        startDestination = NavigationRoute.Notes.route,
        route = "main"
    ) {
        composable(NavigationRoute.Notes.route) {
            NotesScreenRoot(navController)
        }
        composable(NavigationRoute.Settings.route) {
            SettingsScreenRoot(navController)
        }
        composable(
            "${NavigationRoute.ViewDetail.route}/{noteId}"
        ) { backStackEntry ->
            val noteId = backStackEntry.arguments?.getString("noteId") ?: return@composable
            ViewDetailScreenRoot(navController, noteId)
        }
        composable(
            "${NavigationRoute.EditDetail.route}/{noteId}"
        ) { backStackEntry ->
            val noteId = backStackEntry.arguments?.getString("noteId") ?: return@composable
            EditDetailScreenRoot(navController, noteId)
        }

        composable(
            "${NavigationRoute.ReaderDetail.route}/{noteId}"
        ) { backStackEntry ->
            val noteId = backStackEntry.arguments?.getString("noteId") ?: return@composable
            ReaderScreenRoot(navController, noteId)
        }
    }
}






//    NavHost(
//        navController = navController,
//        startDestination = NavigationRoute.Landing.route
//    ) {
//        composable(NavigationRoute.Landing.route) {
//            LandingScreenRoot(navController = navController)
//        }
//        composable(NavigationRoute.Register.route) {
//            RegisterScreenRoot(navController = navController)
//        }
//        composable(NavigationRoute.Login.route) {
//            LoginScreenRoot(navController = navController)
//        }
//        composable(NavigationRoute.Notes.route) {
//            NotesScreenRoot(navController = navController)
//        }
//
//        composable(
//            route = "${NavigationRoute.ViewDetail.route}/{noteId}",
//            arguments = listOf(navArgument("noteId") { type = NavType.StringType })
//        ) { backStackEntry ->
//            val noteId = backStackEntry.arguments?.getString("noteId") ?: return@composable
//            ViewDetailScreenRoot(navController = navController, noteId = noteId)
//        }
//
//        composable(
//            route = "${NavigationRoute.EditDetail.route}/{noteId}",
//            arguments = listOf(navArgument("noteId") { type = NavType.StringType })
//        ) { backStackEntry ->
//            val noteId = backStackEntry.arguments?.getString("noteId") ?: return@composable
//            EditDetailScreenRoot(navController = navController, noteId = noteId)
//        }
//
//        composable(NavigationRoute.Settings.route) {
//            SettingsScreenRoot(navController = navController)
//        }
