package com.vkasurinen.notemark.app

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import com.vkasurinen.notemark.auth.presentation.landing.LandingRoot
import com.vkasurinen.notemark.core.presentation.designsystem.buttons.NoteMarkButtonSecondary
import com.vkasurinen.notemark.core.presentation.designsystem.theme.NoteMarkTheme

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContent {
            NoteMarkTheme {

                LandingRoot()
            }
        }
    }
}
