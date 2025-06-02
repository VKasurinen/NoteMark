package com.vkasurinen.notemark.core.presentation.designsystem.buttons

import android.content.res.Configuration
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowForward
import androidx.compose.material.icons.filled.Favorite
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
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
import org.koin.core.component.getScopeName

enum class ButtonVariant {
    PRIMARY, SECONDARY
}

@Composable
fun NoteMarkButton(
    modifier: Modifier = Modifier,
    text: String,
    onClick: () -> Unit,
    variant: ButtonVariant = ButtonVariant.PRIMARY,
    startIcon: ImageVector? = null,
    endIcon: ImageVector? = null,
    iconTint: Color? = null,
    enabled: Boolean = true
) {
    val colors = when (variant) {
        ButtonVariant.PRIMARY -> {
            if (enabled) {
                ButtonDefaults.buttonColors(
                    containerColor = MaterialTheme.colorScheme.primary,
                    contentColor = MaterialTheme.colorScheme.onPrimary,
                )
            } else {
                ButtonDefaults.buttonColors(
                    containerColor = MaterialTheme.colorScheme.surfaceVariant,
                    contentColor = MaterialTheme.colorScheme.onSurfaceVariant,
                )
            }
        }
        ButtonVariant.SECONDARY -> {
            if (enabled) {
                ButtonDefaults.buttonColors(
                    containerColor = MaterialTheme.colorScheme.surface,
                    contentColor = MaterialTheme.colorScheme.onSurface,
                )
            } else {
                ButtonDefaults.buttonColors(
                    containerColor = MaterialTheme.colorScheme.surfaceVariant,
                    contentColor = MaterialTheme.colorScheme.onSurfaceVariant,
                )
            }
        }
    }

    val defaultIconTint = when (variant) {
        ButtonVariant.PRIMARY -> MaterialTheme.colorScheme.onPrimary
        ButtonVariant.SECONDARY -> MaterialTheme.colorScheme.onSurface
    }

    Button(
        onClick = onClick,
        modifier = modifier,
        shape = RoundedCornerShape(10.dp),
        colors = colors,
        enabled = enabled
    ) {
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

            Text(text = text)

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
                variant = ButtonVariant.PRIMARY,
                startIcon = ImageVector.vectorResource(R.drawable.copy)
            )

            // Secondary Variant
            NoteMarkButton(
                text = "Secondary Button",
                onClick = {},
                variant = ButtonVariant.SECONDARY,
                endIcon = ImageVector.vectorResource(R.drawable.copy),
                enabled = false,
                iconTint = Color(0xFF1b1b1c).copy(alpha = 0.40f)
            )

            // Simple buttons
            NoteMarkButton(
                text = "Simple Primary",
                onClick = {},
                variant = ButtonVariant.PRIMARY,
            )

            NoteMarkButton(
                text = "Simple Secondary",
                onClick = {},
                variant = ButtonVariant.PRIMARY,
                enabled = true
            )

            // Disabled buttons
            NoteMarkButton(
                text = "Disabled Primary",
                onClick = {},
                variant = ButtonVariant.PRIMARY,
                enabled = false
            )

            NoteMarkButton(
                text = "Disabled Secondary",
                onClick = {},
                variant = ButtonVariant.SECONDARY,
                enabled = false
            )
        }
    }
}