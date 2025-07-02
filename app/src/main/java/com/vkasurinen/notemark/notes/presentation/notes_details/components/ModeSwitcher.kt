package com.vkasurinen.notemark.notes.presentation.notes_details.components

import android.content.res.Configuration
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.tooling.preview.Preview
import com.vkasurinen.notemark.core.presentation.designsystem.theme.NoteMarkTheme


@Composable
fun ModeSwitcher(modifier: Modifier = Modifier) {

}

@Preview(
    uiMode = Configuration.UI_MODE_NIGHT_YES
)
@Preview(
    showBackground = true
)
@Composable
private fun Preview() {
    NoteMarkTheme{
        ModeSwitcher()
    }
}