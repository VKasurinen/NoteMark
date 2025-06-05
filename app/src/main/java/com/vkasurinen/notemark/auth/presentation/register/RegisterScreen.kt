package com.vkasurinen.notemark.auth.presentation.register

import android.content.res.Configuration
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.platform.LocalConfiguration
import androidx.compose.ui.tooling.preview.Preview
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.vkasurinen.notemark.auth.presentation.register.components.RegisterLandscapePhone
import com.vkasurinen.notemark.auth.presentation.register.components.RegisterLandscapeTablet
import com.vkasurinen.notemark.auth.presentation.register.components.RegisterPortraitPhone
import com.vkasurinen.notemark.auth.presentation.register.components.RegisterPortraitTablet
import com.vkasurinen.notemark.core.presentation.designsystem.theme.NoteMarkTheme
import org.koin.androidx.compose.koinViewModel

@Composable
fun RegisterRoot(
    viewModel: RegisterViewModel = koinViewModel()
) {
    val state by viewModel.state.collectAsStateWithLifecycle()

    RegisterScreen(
        state = state,
        onAction = viewModel::onAction
    )
}


@Composable
fun RegisterScreen(
    state: RegisterState,
    onAction: (RegisterAction) -> Unit,
) {
    val configuration = LocalConfiguration.current
    val isLandscape = configuration.orientation == Configuration.ORIENTATION_LANDSCAPE
    val isTablet = configuration.smallestScreenWidthDp >= 600

    when {
        isTablet && isLandscape -> {
            RegisterLandscapeTablet(state, onAction)
        }
        isTablet -> {
            RegisterPortraitTablet(state, onAction)
        }
        isLandscape -> {
            RegisterLandscapePhone(state, onAction)
        }
        else -> {
            RegisterPortraitPhone(state, onAction)
        }
    }
}


@Preview(
    showBackground = true,
    widthDp = 800,
    heightDp = 400,
    name = "Landscape"
)
@Preview(
    showBackground = true,
    widthDp = 400,
    heightDp = 800,
    name = "Portrait"
)
@Preview(
    showBackground = true,
    widthDp = 800,
    heightDp = 1280,
    name = "Tablet Portrait"
)
@Preview(
    showBackground = true,
    widthDp = 1280,
    heightDp = 800,
    name = "Tablet Landscape"
)
@Composable
private fun Preview() {
    NoteMarkTheme {
        RegisterScreen(
            state = RegisterState(),
            onAction = {}
        )
    }
}