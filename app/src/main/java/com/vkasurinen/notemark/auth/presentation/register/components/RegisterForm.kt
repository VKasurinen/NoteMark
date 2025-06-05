package com.vkasurinen.notemark.auth.presentation.register.components

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.vkasurinen.notemark.R
import com.vkasurinen.notemark.auth.presentation.register.RegisterAction
import com.vkasurinen.notemark.auth.presentation.register.RegisterState
import com.vkasurinen.notemark.core.presentation.designsystem.buttons.NoteMarkButton
import com.vkasurinen.notemark.core.presentation.designsystem.text_fields.NotePasswordTextField
import com.vkasurinen.notemark.core.presentation.designsystem.text_fields.NoteTextField

@Composable
fun RegisterForm(
    state: RegisterState,
    onAction: (RegisterAction) -> Unit
) {
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
                onAction(RegisterAction.OnLoginClick)
            },
        textAlign = TextAlign.Center
    )
}
