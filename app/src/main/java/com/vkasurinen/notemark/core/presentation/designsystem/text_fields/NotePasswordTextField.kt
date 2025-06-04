package com.vkasurinen.notemark.core.presentation.designsystem.text_fields

import androidx.compose.material3.IconButton
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.res.vectorResource
import com.vkasurinen.notemark.R
import android.content.res.Configuration
import android.widget.Space
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.text.BasicSecureTextField
import androidx.compose.foundation.text.BasicTextField
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.foundation.text.input.TextFieldState
import androidx.compose.foundation.text.input.TextObfuscationMode
import androidx.compose.foundation.text.input.rememberTextFieldState
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Lock
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
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.vkasurinen.notemark.core.presentation.designsystem.theme.NoteMarkTheme


@Composable
fun NotePasswordTextField(
    state: TextFieldState,
    modifier: Modifier = Modifier,
    isPasswordVisible: Boolean,
    onTogglePasswordVisibility: () -> Unit,
    label: String? = null,
    placeholder: String,
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

            BasicSecureTextField(
                state = state,
                textObfuscationMode = if (isPasswordVisible) {
                    TextObfuscationMode.Visible
                } else {
                    TextObfuscationMode.Hidden
                },
                modifier = Modifier
                    .onFocusChanged {
                        isFocused = it.isFocused
                    },
                textStyle = MaterialTheme.typography.bodyMedium.copy(
                    color = MaterialTheme.colorScheme.onSurface
                ),
                keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Password),
                decorator = @Composable { innerBox ->
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Box(
                            modifier = Modifier.weight(1f)
                        ) {
                            if (state.text.isEmpty() && !isFocused) {
                                Text(
                                    text = placeholder,
                                    style = MaterialTheme.typography.bodyMedium,
                                    color = MaterialTheme.colorScheme.onSurfaceVariant,
                                    modifier = Modifier.fillMaxWidth()
                                )
                            }
                            innerBox()
                        }
                        IconButton(
                            modifier = Modifier.size(20.dp),
                            onClick = onTogglePasswordVisibility
                        ) {
                            Icon(
                                imageVector = if (isPasswordVisible) {
                                    ImageVector.vectorResource(id = R.drawable.eye_open)
                                } else {
                                    ImageVector.vectorResource(id = R.drawable.eye_closed)
                                },
                                contentDescription = if (isPasswordVisible) {
                                    stringResource(id = R.string.hide_password)
                                } else {
                                    stringResource(id = R.string.show_password)
                                },
                                tint = MaterialTheme.colorScheme.onSurfaceVariant
                            )
                        }
                    }
                }
            )
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

@Preview(
    uiMode = Configuration.UI_MODE_NIGHT_YES
)
@Preview(
    showBackground = true
)
@Composable
fun NotePasswordTextFieldPreview() {
    NoteMarkTheme {

        Column(
            modifier = Modifier.padding(16.dp)
        ) {
            NotePasswordTextField(
                state = rememberTextFieldState(),
                label = "Password",
                placeholder = "Pekka123",
                supportingText = "Optional error",
                isPasswordVisible = true,
                onTogglePasswordVisibility = {}
            )

            Spacer(modifier = Modifier.height(12.dp))

            NotePasswordTextField(
                state = rememberTextFieldState(),
                label = "Password",
                placeholder = "Pekka123",
                supportingText = "Optional error",
                isPasswordVisible = true,
                onTogglePasswordVisibility = {}
            )

        }

    }
}

