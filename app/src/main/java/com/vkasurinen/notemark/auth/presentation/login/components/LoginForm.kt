package com.vkasurinen.notemark.auth.presentation.login.components

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
import androidx.compose.ui.unit.dp
import com.vkasurinen.notemark.R
import com.vkasurinen.notemark.auth.presentation.login.LoginAction
import com.vkasurinen.notemark.auth.presentation.login.LoginState
import com.vkasurinen.notemark.core.presentation.designsystem.buttons.NoteMarkButton
import com.vkasurinen.notemark.core.presentation.designsystem.text_fields.NotePasswordTextField
import com.vkasurinen.notemark.core.presentation.designsystem.text_fields.NoteTextField

@Composable
fun LoginForm(
    state: LoginState,
    onAction: (LoginAction) -> Unit
) {

    NoteTextField(
        label = stringResource(R.string.email),
        state = state.email,
        placeholder = "john.doe@example.com"
    )

    Spacer(modifier = Modifier.height(10.dp))

    NotePasswordTextField(
        state = state.password,
        isPasswordVisible = state.isPasswordVisible,
        onTogglePasswordVisibility = {
            onAction(LoginAction.OnTogglePasswordVisibilityClick)
        },
        label = stringResource(R.string.password),
        placeholder = "Password",
    )

    Spacer(modifier = Modifier.height(20.dp))

    NoteMarkButton(
        text = "Log In",
        onClick = { onAction(LoginAction.OnLoginClick) },
        modifier = Modifier.fillMaxWidth(),
        enabled = state.canLogin,
        isLoading = state.isLoading
    )

    Spacer(modifier = Modifier.height(20.dp))

    Text(
        text = "Don't have an account?",
        style = MaterialTheme.typography.titleSmall,
        color = MaterialTheme.colorScheme.primary,
        modifier = Modifier
            .fillMaxWidth()
            .clickable {
                onAction(LoginAction.OnRegisterClick)
            },
        textAlign = TextAlign.Center
    )
}
