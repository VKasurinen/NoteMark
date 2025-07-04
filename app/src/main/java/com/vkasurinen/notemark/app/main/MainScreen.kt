package com.vkasurinen.notemark.app.main

import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.rememberNavController
import com.vkasurinen.notemark.app.navigation.NavigationRoute
import com.vkasurinen.notemark.app.navigation.authGraph
import com.vkasurinen.notemark.app.navigation.mainGraph
import com.vkasurinen.notemark.core.presentation.designsystem.theme.NoteMarkTheme
import org.koin.androidx.compose.koinViewModel

@Composable
fun MainScreen() {
    val mainViewModel: MainViewModel = koinViewModel()
    val state by mainViewModel.state.collectAsStateWithLifecycle()
    val navController = rememberNavController()

    if (!state.isCheckingAuth) {
        NoteMarkTheme {
            NavHost(
                navController = navController,
                startDestination = if (state.isLoggedIn) "main" else "auth"
            ) {
                authGraph(navController)
                mainGraph(navController)
            }
        }
    }
}