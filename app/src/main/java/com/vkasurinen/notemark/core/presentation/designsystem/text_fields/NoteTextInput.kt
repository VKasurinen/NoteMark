package com.vkasurinen.notemark.core.presentation.designsystem.text_fields

import android.content.res.Configuration
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.text.BasicTextField
import androidx.compose.material.icons.Icons
import androidx.compose.material3.Button
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Text
import androidx.compose.material3.TextFieldDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.focus.focusModifier
import androidx.compose.ui.focus.onFocusChanged
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.input.TextFieldValue
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.vkasurinen.notemark.core.presentation.designsystem.theme.EyeOpen
import com.vkasurinen.notemark.core.presentation.designsystem.theme.NoteMarkTheme


@Composable
fun NoteTextInput(
    modifier: Modifier = Modifier,
    label: String? = null,
    placeholder: String? = null,
    supportingText: String? = null,
    value: String = "",
    onValueChange: (String) -> Unit = {},
    borderColor: Color = MaterialTheme.colorScheme.primary,
    supportingTextColor: Color? = null,
    trailingIcon: (@Composable () -> Unit)? = null,
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
                    color = if (isFocused) Color.White else MaterialTheme.colorScheme.surface,
                    shape = MaterialTheme.shapes.medium
                )
                .padding(horizontal = 12.dp, vertical = 16.dp)
        ) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                verticalAlignment = Alignment.CenterVertically
            ) {
                BasicTextField(
                    value = value,
                    onValueChange = onValueChange,
                    modifier = Modifier
                        .weight(1f)
                        .onFocusChanged { focusState ->
                            isFocused = focusState.isFocused
                        },
                    textStyle = MaterialTheme.typography.bodyMedium.copy(
                        color = MaterialTheme.colorScheme.onSurface
                    ),
                    decorationBox = { innerTextField ->
                        if (value.isEmpty() && placeholder != null) {
                            Text(
                                text = placeholder,
                                style = MaterialTheme.typography.bodyMedium,
                                color = MaterialTheme.colorScheme.onSurfaceVariant
                            )
                        }
                        innerTextField()
                    },
                    singleLine = true
                )

                trailingIcon?.let {
                    Spacer(modifier = Modifier.width(8.dp))
                    it()
                }
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
fun InteractivePreview() {
    NoteMarkTheme {
        var text by remember { mutableStateOf("") }

        Column(modifier = Modifier.padding(16.dp)) {
            NoteTextInput(
                label = "Label",
                value = text,
                placeholder = "Testi@test.com",
                onValueChange = { text = it },
                supportingText = "Supporting text",
                borderColor = MaterialTheme.colorScheme.primary,
                trailingIcon = {
                    androidx.compose.material3.Icon(
                        imageVector = Icons.Filled.EyeOpen,
                        contentDescription = "Eye Open Icon"
                    )
                }
            )

            Spacer(modifier = Modifier.height(20.dp))

            NoteTextInput(
                label = "Label",
                value = text,
                placeholder = "Testi@test.com",
                onValueChange = { text = it },
                supportingText = "Supporting text",
                supportingTextColor = MaterialTheme.colorScheme.error,
                borderColor = MaterialTheme.colorScheme.error
            )
        }
    }
}

