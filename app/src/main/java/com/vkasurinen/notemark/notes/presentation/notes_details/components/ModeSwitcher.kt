package com.vkasurinen.notemark.notes.presentation.notes_details.components

import android.content.res.Configuration
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.vkasurinen.notemark.core.presentation.designsystem.theme.Edit
import com.vkasurinen.notemark.core.presentation.designsystem.theme.NoteMarkTheme
import com.vkasurinen.notemark.core.presentation.designsystem.theme.Read


@Composable
fun ModeSwitcher(
    modifier: Modifier = Modifier,
    currentMode: Mode,
    onModeSelected: (Mode) -> Unit
) {
    Row(
        modifier = modifier
            .clip(RoundedCornerShape(12.dp))
            .background(MaterialTheme.colorScheme.onSurfaceVariant.copy(alpha = 0.2f))
            .padding(4.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        ModeSwitcherButton(
            icon = Icons.Default.Edit,
            contentDescription = "Edit Mode",
            selected = currentMode == Mode.EDIT,
            onClick = { onModeSelected(Mode.EDIT) }
        )

        Spacer(modifier = Modifier.width(8.dp))

        ModeSwitcherButton(
            icon = Icons.Default.Read,
            contentDescription = "Read Mode",
            selected = currentMode == Mode.READ,
            onClick = { onModeSelected(Mode.READ) }
        )
    }
}


@Composable
fun ModeSwitcherButton(
    icon: ImageVector,
    contentDescription: String,
    selected: Boolean,
    onClick: () -> Unit
) {
    Box(
        modifier = Modifier
            .clip(RoundedCornerShape(10.dp))
            .background(
                if (selected)
                    MaterialTheme.colorScheme.primary.copy(alpha = 0.2f)
                else
                    Color.Transparent
            )
            .clickable { onClick() }
            .padding(12.dp),
        contentAlignment = Alignment.Center
    ) {
        Icon(
            imageVector = icon,
            contentDescription = contentDescription,
            tint = if (selected) MaterialTheme.colorScheme.primary else MaterialTheme.colorScheme.onSurface
        )
    }
}


enum class Mode {
    VIEW,
    EDIT,
    READ
}



@Preview(showBackground = true)
@Composable
fun ModeSwitcherPreview() {
    NoteMarkTheme {
        var currentMode by remember { mutableStateOf(Mode.VIEW) }

        ModeSwitcher(
            currentMode = currentMode,
            onModeSelected = { selectedMode ->
                currentMode = selectedMode
            }
        )
    }
}

