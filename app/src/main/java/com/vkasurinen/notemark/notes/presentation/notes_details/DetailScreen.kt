package com.vkasurinen.notemark.notes.presentation.notes_details

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.safeContentPadding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.text.BasicTextField
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.alpha
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.vkasurinen.notemark.core.presentation.designsystem.buttons.NoteMarkButton
import com.vkasurinen.notemark.core.presentation.designsystem.buttons.NoteMarkButtonSecondary
import com.vkasurinen.notemark.core.presentation.designsystem.buttons.NoteMarkButtonTertiary
import com.vkasurinen.notemark.core.presentation.designsystem.theme.Cross
import com.vkasurinen.notemark.core.presentation.designsystem.theme.Inter
import com.vkasurinen.notemark.core.presentation.designsystem.theme.NoteMarkTheme
import org.koin.androidx.compose.koinViewModel
import androidx.compose.material3.AlertDialog
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.ui.platform.LocalContext
import androidx.navigation.NavHostController

@Composable
fun DetailRoot(
    navController: NavHostController,
    noteId: String,
    viewModel: DetailViewModel = koinViewModel()
) {
    val state by viewModel.state.collectAsStateWithLifecycle()

    LaunchedEffect(noteId) {
        viewModel.loadNoteDetails(noteId)
    }

    DetailScreen(
        state = state,
        onAction = viewModel::onAction,
        navController = navController
    )
}

@Composable
fun DetailScreen(
    state: DetailState,
    onAction: (DetailAction) -> Unit,
    navController: NavHostController
) {
    var showDiscardDialog by remember { mutableStateOf(false) }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(MaterialTheme.colorScheme.surfaceVariant)
            .padding(vertical = 24.dp)
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(8.dp),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            IconButton(
                onClick = {
                    val hasChanges = state.title != state.originalTitle || state.content != state.originalContent
                    if (hasChanges) {
                        showDiscardDialog = true
                    } else {
                        navController.popBackStack()
                    }
                }
            ) {
                Icon(
                    imageVector = Icons.Filled.Cross,
                    contentDescription = "Close",
                    tint = MaterialTheme.colorScheme.onSurface
                )
            }

            NoteMarkButtonTertiary(
                text = "SAVE NOTE",
                onClick = { onAction(DetailAction.OnSaveClick) }
            )
        }

        Column(
            modifier = Modifier
                .fillMaxSize()
                .verticalScroll(rememberScrollState())
                .padding(16.dp)
        ) {
            BasicTextField(
                value = state.title,
                onValueChange = { onAction(DetailAction.OnTitleChange(it)) },
                textStyle = TextStyle(
                    fontSize = 20.sp,
                    fontWeight = FontWeight.Bold,
                    color = MaterialTheme.colorScheme.onSurface
                ),
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(bottom = 8.dp)
            )

            HorizontalDivider(
                modifier = Modifier
                    .fillMaxWidth()
                    .alpha(0.5f),
                color = MaterialTheme.colorScheme.onSurfaceVariant
            )

            Spacer(modifier = Modifier.height(16.dp))

            BasicTextField(
                value = state.content,
                onValueChange = { onAction(DetailAction.OnContentChange(it)) },
                textStyle = TextStyle(
                    fontSize = 16.sp,
                    color = MaterialTheme.colorScheme.onSurface
                ),
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(bottom = 8.dp),
                decorationBox = { innerTextField ->
                    Box {
                        if (state.content.isEmpty()) {
                            Text(
                                text = "Enter your content here...",
                                style = TextStyle(
                                    fontSize = 16.sp,
                                    color = MaterialTheme.colorScheme.onSurfaceVariant
                                )
                            )
                        }
                        innerTextField()
                    }
                }
            )
        }
    }

    if (showDiscardDialog) {
        AlertDialog(
            onDismissRequest = { showDiscardDialog = false },
            title = { Text("Discard Changes?") },
            text = { Text("You have unsaved changes. If you discard now, all changes will be lost.") },
            confirmButton = {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.Center,
                    verticalAlignment = Alignment.CenterVertically
                ) {

                    NoteMarkButtonTertiary(
                        text = "Keep Editing",
                        onClick = { showDiscardDialog = false }
                    )
                    Spacer(modifier = Modifier.width(12.dp))
                    NoteMarkButton(
                        text = "Discard",
                        onClick = {
                            showDiscardDialog = false
                            navController.popBackStack()
                        }
                    )
                }
            }
        )
    }
}


@Preview
@Composable
private fun Preview() {
    NoteMarkTheme {
        DetailScreen(
            state = DetailState(
                title = "Note Title",
                content = "Amet minim mollit non deserunt ullamco est sit aliqua dolor do amet sint.",
                originalTitle = "Original Note Title",
                originalContent = "Original content of the note."
            ),
            onAction = {},
            navController = NavHostController(LocalContext.current)
        )
    }
}