package com.vkasurinen.notemark.core.presentation.designsystem.buttons

import android.content.res.Configuration
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.vkasurinen.notemark.core.presentation.designsystem.theme.NoteMarkTheme

@Composable
fun NoteMarkButtonSecondary(
    text: String,
    onClick: () -> Unit,
    modifier: Modifier = Modifier,
) {
    val buttonShape = MaterialTheme.shapes.medium

    Button(
        modifier = modifier
            .border(
                width = 1.dp,
                color = MaterialTheme.colorScheme.primary,
                shape = buttonShape
            ),
        onClick = onClick,
        shape = buttonShape,
        colors = ButtonDefaults.buttonColors(
            containerColor = MaterialTheme.colorScheme.onPrimary,
        ),
    ) {
        Text(
            text = text,
            style = MaterialTheme.typography.bodyLarge,
            color = MaterialTheme.colorScheme.primary,
        )
    }
}

@Preview(
    uiMode = Configuration.UI_MODE_NIGHT_YES,
    showBackground = true
)
@Preview(
    showBackground = true
)
@Composable
private fun NoteMarkButtonSecondaryPreview() {
    NoteMarkTheme {
        NoteMarkButtonSecondary(
            text = "Hello world!",
            onClick = {},
        )
    }
}