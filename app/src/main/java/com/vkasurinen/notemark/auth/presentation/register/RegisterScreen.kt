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
import androidx.compose.ui.unit.sp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.lifecycle.viewmodel.compose.viewModel
import com.vkasurinen.notemark.R
import com.vkasurinen.notemark.core.presentation.designsystem.buttons.NoteMarkButton
import com.vkasurinen.notemark.core.presentation.designsystem.text_fields.NoteTextInput
import com.vkasurinen.notemark.core.presentation.designsystem.theme.EyeOpen
import com.vkasurinen.notemark.core.presentation.designsystem.theme.NoteMarkTheme

@Composable
fun RegisterRoot(
    viewModel: RegisterViewModel = viewModel()
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

    var username by remember { mutableStateOf("") }
    var email by remember { mutableStateOf("") }
    var password by remember { mutableStateOf("") }
    var password2 by remember { mutableStateOf("") }

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

                NoteTextInput(
                    label = stringResource(R.string.username),
                    value = username,
                    placeholder = "John.doe",
                    onValueChange = {username = it}
                )

                Spacer(modifier = Modifier.height(10.dp))

                NoteTextInput(
                    label = stringResource(R.string.email),
                    value = email,
                    placeholder = "john.doe@example.com",
                    onValueChange = {email = it}
                )

                Spacer(modifier = Modifier.height(10.dp))

                NoteTextInput(
                    label = stringResource(R.string.password),
                    value = password,
                    placeholder = "Password",
                    onValueChange = {password = it},
                    trailingIcon = {
                        androidx.compose.material3.Icon(
                            imageVector = Icons.Filled.EyeOpen,
                            contentDescription = "Eye Open Icon"
                        )
                    }
                )

                Spacer(modifier = Modifier.height(10.dp))

                NoteTextInput(
                    label = stringResource(R.string.repeat_password),
                    value = password,
                    placeholder = "Password",
                    onValueChange = {password = it},
                    trailingIcon = {
                        androidx.compose.material3.Icon(
                            imageVector = Icons.Filled.EyeOpen,
                            contentDescription = "Eye Open Icon"
                        )
                    }
                )

                Spacer(modifier = Modifier.height(20.dp))

                NoteMarkButton(
                    text = "Create account",
                    onClick = { },
                    modifier = Modifier.fillMaxWidth(),
                    enabled = false
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