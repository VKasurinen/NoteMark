package com.vkasurinen.notemark.auth.presentation.register

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.lifecycle.viewmodel.compose.viewModel
import com.vkasurinen.notemark.R
import com.vkasurinen.notemark.core.presentation.designsystem.buttons.NoteMarkButton
import com.vkasurinen.notemark.core.presentation.designsystem.text_fields.NotePasswordTextField
import com.vkasurinen.notemark.core.presentation.designsystem.text_fields.NoteTextField
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
    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(MaterialTheme.colorScheme.primary)
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .fillMaxHeight()
                .padding(top = 50.dp)
                .clip(RoundedCornerShape(topEnd = 20.dp, topStart = 20.dp))
                .background(MaterialTheme.colorScheme.surfaceVariant)
        ) {
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(20.dp)
            ) {

                Text(
                    text = stringResource(R.string.create_account),
                    style = MaterialTheme.typography.headlineLarge,
                    fontWeight = FontWeight.Bold,
                    color = MaterialTheme.colorScheme.onSurface,
                    modifier = Modifier.fillMaxWidth(),
                    maxLines = 2,
                )

                Spacer(modifier = Modifier.height(6.dp))

                Text(
                    text = stringResource(R.string.capture_toughts),
                    style = MaterialTheme.typography.bodyLarge,
                    color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.7f),
                    modifier = Modifier.fillMaxWidth()
                )

                Spacer(modifier = Modifier.height(20.dp))

                NoteTextField(
                    label = stringResource(R.string.username),
                    state = state.username,
                    placeholder = "John.doe",
                    supportingText = "Use between 3 and 20 characters",
                    isError = !state.isUsernameValid && state.username.text.isNotEmpty(),
                    errorText = state.usernameError ?: "Username must be at least 3 or max 20 characters"
                )

                Spacer(modifier = Modifier.height(10.dp))

                NoteTextField(
                    label = stringResource(R.string.email),
                    state = state.email,
                    placeholder = "john.doe@example.com",
                    isError = !state.isEmailValid && state.email.text.isNotEmpty(),
                    errorText = "Invalid email provided"
                )

                Spacer(modifier = Modifier.height(10.dp))

                NotePasswordTextField(
                    state = state.password,
                    isPasswordVisible = state.isPasswordVisible,
                    onTogglePasswordVisibility = {
                        onAction(RegisterAction.OnTogglePasswordVisibilityClick(isPassword1 = true))
                    },
                    label = stringResource(R.string.password),
                    placeholder = "Password",
                    supportingText = "Use 8+ characters with a number or symbol",
                    isError = !state.passwordValidationState.isValidPassword && state.password.text.isNotEmpty(),
                    errorText = state.passwordValidationState.errorMessage
                )

                Spacer(modifier = Modifier.height(10.dp))

                NotePasswordTextField(
                    state = state.password2,
                    isPasswordVisible = state.isPassword2Visible,
                    onTogglePasswordVisibility = {
                        onAction(RegisterAction.OnTogglePasswordVisibilityClick(isPassword1 = false))
                    },
                    label = stringResource(R.string.repeat_password),
                    placeholder = "Password",
                    isError = !state.isPasswordMatching && state.password2.text.isNotEmpty(),
                    errorText = "Passwords do not match"
                )

                Spacer(modifier = Modifier.height(20.dp))

                NoteMarkButton(
                    text = "Create account",
                    onClick = { onAction(RegisterAction.OnRegisterClick) },
                    modifier = Modifier.fillMaxWidth(),
                    enabled = state.canRegister
                )

                Spacer(modifier = Modifier.height(20.dp))

                Text(
                    text = "Already have an account?",
                    style = MaterialTheme.typography.titleSmall,
                    color = MaterialTheme.colorScheme.primary,
                    modifier = Modifier
                        .fillMaxWidth()
                        .clickable {
                            // Handle navigation to login screen
                            onAction(RegisterAction.OnLoginClick)
                        },
                    textAlign = TextAlign.Center
                )
            }
        }
    }
}

@Preview
@Composable
private fun Preview() {
    NoteMarkTheme {
        RegisterScreen(
            state = RegisterState(),
            onAction = {}
        )
    }
}