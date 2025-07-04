package com.vkasurinen.notemark.auth.presentation.login

import android.content.res.Configuration
import android.widget.Toast
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.platform.LocalConfiguration
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavHostController
import com.vkasurinen.notemark.R
import com.vkasurinen.notemark.app.navigation.NavigationRoute
import com.vkasurinen.notemark.auth.presentation.login.components.LoginForm
import com.vkasurinen.notemark.auth.presentation.login.components.LoginLandscapePhone
import com.vkasurinen.notemark.auth.presentation.login.components.LoginLandscapeTablet
import com.vkasurinen.notemark.auth.presentation.login.components.LoginPortraitPhone
import com.vkasurinen.notemark.auth.presentation.login.components.LoginPortraitTablet
import com.vkasurinen.notemark.auth.presentation.register.RegisterAction
import com.vkasurinen.notemark.auth.presentation.register.RegisterState
import com.vkasurinen.notemark.auth.presentation.register.components.RegisterForm
import com.vkasurinen.notemark.auth.presentation.register.components.RegisterLandscapePhone
import com.vkasurinen.notemark.auth.presentation.register.components.RegisterLandscapeTablet
import com.vkasurinen.notemark.auth.presentation.register.components.RegisterPortraitPhone
import com.vkasurinen.notemark.auth.presentation.register.components.RegisterPortraitTablet
import com.vkasurinen.notemark.core.presentation.designsystem.theme.NoteMarkTheme
import com.vkasurinen.notemark.core.presentation.util.ObserveAsEvents
import org.koin.androidx.compose.koinViewModel

@Composable
fun LoginScreenRoot(
    navController: NavHostController,
    viewModel: LoginViewModel = koinViewModel(),
) {
    val state by viewModel.state.collectAsStateWithLifecycle()
    val context = LocalContext.current

    ObserveAsEvents(viewModel.events) { event ->
        when (event) {
            is LoginEvent.NavigateToRegister -> {
                navController.navigate(NavigationRoute.Register.route) {
                    popUpTo(NavigationRoute.Login.route) { inclusive = true }
                    launchSingleTop = true
                }
            }
            is LoginEvent.Error -> {
                Toast.makeText(context, event.error.asString(context), Toast.LENGTH_SHORT).show()
            }
            is LoginEvent.LoginSuccess -> {
                navController.navigate(NavigationRoute.Notes.route) {
                    popUpTo(NavigationRoute.Login.route) { inclusive = true }
                    launchSingleTop = true
                }
            }
        }
    }

    LoginScreen(
        state = state,
        onAction = viewModel::onAction
    )
}

@Composable
fun LoginScreen(
    state: LoginState,
    onAction: (LoginAction) -> Unit,
) {
    val configuration = LocalConfiguration.current
    val isLandscape = configuration.orientation == Configuration.ORIENTATION_LANDSCAPE
    val isTablet = configuration.smallestScreenWidthDp >= 600

    when {
        isTablet && isLandscape -> {
            LoginLandscapeTablet(state, onAction)
        }
        isTablet -> {
            LoginPortraitTablet(state, onAction)
        }
        isLandscape -> {
            LoginLandscapePhone(state, onAction)
        }
        else -> {
            LoginPortraitPhone(state, onAction)
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
        LoginScreen(
            state = LoginState(
                isLoading = false
            ),
            onAction = {}
        )
    }
}