package com.vkasurinen.notemark.core.presentation.designsystem.text_fields

import android.content.res.Configuration
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.text.BasicTextField
import androidx.compose.foundation.text.input.TextFieldState
import androidx.compose.foundation.text.input.rememberTextFieldState
import androidx.compose.material.icons.Icons
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.focus.onFocusChanged
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.vkasurinen.notemark.core.presentation.designsystem.theme.EyeOpen
import com.vkasurinen.notemark.core.presentation.designsystem.theme.NoteMarkTheme


@Composable
fun NoteTextInput(
    state: TextFieldState,
    modifier: Modifier = Modifier,
    label: String? = null,
    placeholder: String? = null,
    borderColor: Color = MaterialTheme.colorScheme.primary,
    supportingText: String? = null,
    supportingTextColor: Color? = null,
) {
    var isFocused by remember { mutableStateOf(false) }

    Column(modifier = modifier.padding(vertical = 4.dp)) {
        label?.let {
            Text(
                text = it,
                style = MaterialTheme.typography.labelMedium,
                color = MaterialTheme.colorScheme.onSurface
            )
            Spacer(modifier = Modifier.height(6.dp))
        }

        Box(
            modifier = Modifier
                .fillMaxWidth()
                .border(
                    width = if (isFocused) 1.dp else 0.dp,
                    color = if (isFocused) borderColor else Color.Transparent,
                    shape = MaterialTheme.shapes.medium
                )
                .background(
                    color = if (isFocused) Color.White else MaterialTheme.colorScheme.onSurfaceVariant.copy(alpha = 0.2f),
                    shape = MaterialTheme.shapes.medium
                )
                .padding(horizontal = 12.dp, vertical = 18.dp)
        ) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                verticalAlignment = Alignment.CenterVertically
            ) {
                BasicTextField(
                    state = state,
                    modifier = Modifier
                        .weight(1f)
                        .onFocusChanged {
                            isFocused = it.isFocused
                        },
                    textStyle = MaterialTheme.typography.bodyMedium.copy(
                        color = MaterialTheme.colorScheme.onSurface
                    ),
                    decorator = { innerTextField ->
                        if (state.text.isEmpty() && placeholder != null) {
                            Text(
                                text = placeholder,
                                style = MaterialTheme.typography.bodyMedium,
                                color = MaterialTheme.colorScheme.onSurfaceVariant
                            )
                        }
                        innerTextField()
                    },
                )
            }
        }

        supportingText?.let {
            Spacer(modifier = Modifier.height(6.dp))
            Text(
                text = it,
                style = MaterialTheme.typography.labelMedium,
                color = supportingTextColor ?: MaterialTheme.colorScheme.onSurfaceVariant
            )
        }
    }
}


@Preview(showBackground = true)
@Composable
fun DefaultPreview() {
    NoteMarkTheme {
        val emptyState = rememberTextFieldState()
        val filledState = rememberTextFieldState("Sample text")

        Column(modifier = Modifier.padding(16.dp)) {
            NoteTextInput(
                label = "Email",
                state = emptyState,
                placeholder = "Enter your email",
                supportingText = "Required field",
                borderColor = MaterialTheme.colorScheme.primary,
            )

            Spacer(modifier = Modifier.height(20.dp))

            NoteTextInput(
                label = "Password",
                state = filledState,
                placeholder = "Enter your password",
                supportingText = "Invalid password",
                supportingTextColor = MaterialTheme.colorScheme.error,
                borderColor = MaterialTheme.colorScheme.error
            )
        }
    }
}

@Preview(showBackground = true, uiMode = Configuration.UI_MODE_NIGHT_YES)
@Composable
fun DarkModePreview() {
    NoteMarkTheme {
        val state = rememberTextFieldState()

        Column(modifier = Modifier.padding(16.dp)) {
            NoteTextInput(
                label = "Dark Mode Input",
                state = state,
                placeholder = "Works in dark mode",
                supportingText = "Dark theme support",
                borderColor = MaterialTheme.colorScheme.primary
            )
        }
    }
}

