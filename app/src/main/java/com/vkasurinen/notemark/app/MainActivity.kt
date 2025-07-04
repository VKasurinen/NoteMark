package com.vkasurinen.notemark.app

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.core.splashscreen.SplashScreen.Companion.installSplashScreen
import androidx.lifecycle.lifecycleScope
import com.vkasurinen.notemark.app.main.MainScreen
import com.vkasurinen.notemark.auth.domain.repository.AuthRepository
import com.vkasurinen.notemark.core.domain.util.Result
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import org.koin.android.ext.android.inject

class MainActivity : ComponentActivity() {

    private val authRepository: AuthRepository by inject()


    override fun onCreate(savedInstanceState: Bundle?) {
        installSplashScreen()
        super.onCreate(savedInstanceState)

        enableEdgeToEdge()
        setContent {
            MainScreen()
        }
    }

    private fun testRegister() {
        lifecycleScope.launch(Dispatchers.IO) {
            val result = authRepository.register(
                username = "VKasurinen123",
                email = "kokeilu1234@test.com",
                password = "Test12345",
            )
            when (result) {
                is Result.Success -> {
                    // Handle successful registration
                    println("Registration successful: ${result.data}")
                }
                is Result.Error -> {
                    // Handle registration error
                    println("Registration error: ${result.message}")
                }
                is Result.Loading -> {
                    // Handle loading state
                    println("Loading...")
                }

                else -> {}
            }
        }
    }

}
