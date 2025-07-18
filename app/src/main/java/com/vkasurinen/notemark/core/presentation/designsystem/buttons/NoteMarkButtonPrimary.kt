package com.vkasurinen.notemark.core.presentation.designsystem.buttons

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.IntrinsicSize
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.res.vectorResource
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.vkasurinen.notemark.R
import com.vkasurinen.notemark.core.presentation.designsystem.theme.NoteMarkTheme


@Composable
fun NoteMarkButton(
    modifier: Modifier = Modifier,
    text: String,
    onClick: () -> Unit,
    startIcon: ImageVector? = null,
    endIcon: ImageVector? = null,
    iconTint: Color? = null,
    enabled: Boolean = true,
    isLoading: Boolean = false
) {
    val colors = if (enabled) {
        ButtonDefaults.buttonColors(
            containerColor = MaterialTheme.colorScheme.primary,
            contentColor = MaterialTheme.colorScheme.onPrimary,
        )
    } else {
        ButtonDefaults.buttonColors(
            containerColor = MaterialTheme.colorScheme.onSurfaceVariant.copy(alpha = 0.2f),
            contentColor = MaterialTheme.colorScheme.onSurfaceVariant,
        )
    }

    val defaultIconTint = if (enabled) {
        MaterialTheme.colorScheme.onPrimary
    } else {
        MaterialTheme.colorScheme.onSurface
    }

    Button(
        onClick = {
            if (!isLoading && enabled) {
                onClick()
            }
        },
        modifier = modifier.height(IntrinsicSize.Min),
        shape = RoundedCornerShape(10.dp),
        colors = colors,
        elevation = ButtonDefaults.buttonElevation(
            defaultElevation = 0.dp,
            pressedElevation = 2.dp
        ),
        enabled = enabled
    ) {
        if (isLoading) {
            CircularProgressIndicator(
                modifier = Modifier.size(20.dp),
                color = MaterialTheme.colorScheme.onPrimary,
                strokeWidth = 2.dp
            )
        } else {
            Row(
                horizontalArrangement = Arrangement.Center,
                verticalAlignment = Alignment.CenterVertically
            ) {
                startIcon?.let { icon ->
                    Icon(
                        imageVector = icon,
                        contentDescription = null,
                        tint = iconTint ?: defaultIconTint,
                        modifier = Modifier.size(18.dp)
                    )
                    Spacer(modifier = Modifier.width(8.dp))
                }

                Text(
                    text = text,
                    modifier = Modifier.padding(horizontal = 8.dp)
                )

                endIcon?.let { icon ->
                    Spacer(modifier = Modifier.width(8.dp))
                    Icon(
                        imageVector = icon,
                        contentDescription = null,
                        tint = iconTint ?: defaultIconTint,
                        modifier = Modifier.size(18.dp)
                    )
                }
            }
        }
    }
}



@Preview(showBackground = true)
//@Preview(showBackground = true, uiMode = Configuration.UI_MODE_NIGHT_YES)
@Composable
private fun NoteMarkButtonPreview() {
    NoteMarkTheme {
        Column(
            modifier = Modifier.padding(16.dp),
            verticalArrangement = Arrangement.spacedBy(16.dp)
        ) {
            // Primary Variant
            NoteMarkButton(
                text = "Primary Button",
                onClick = {},
                startIcon = ImageVector.vectorResource(R.drawable.copy)
            )

            // Secondary Variant
            NoteMarkButton(
                text = "Secondary Button",
                onClick = {},
                endIcon = ImageVector.vectorResource(R.drawable.copy),
                enabled = false,
                iconTint = Color(0xFF1b1b1c).copy(alpha = 0.40f)
            )

            // Simple buttons
            NoteMarkButton(
                text = "Simple Primary",
                onClick = {},
            )

            NoteMarkButton(
                text = "Simple Secondary",
                onClick = {},
                enabled = true
            )

            // Disabled buttons
            NoteMarkButton(
                text = "Disabled Primary",
                onClick = {},
                enabled = false
            )

            NoteMarkButton(
                text = "Disabled Secondary",
                onClick = {},
                enabled = false
            )
        }
    }
}