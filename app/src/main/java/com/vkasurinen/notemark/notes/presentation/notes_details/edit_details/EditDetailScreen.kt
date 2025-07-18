package com.vkasurinen.notemark.notes.presentation.notes_details.edit_details

import android.content.res.Configuration
import android.widget.Toast
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
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
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.vkasurinen.notemark.core.presentation.designsystem.buttons.NoteMarkButton
import com.vkasurinen.notemark.core.presentation.designsystem.buttons.NoteMarkButtonTertiary
import com.vkasurinen.notemark.core.presentation.designsystem.theme.Cross
import com.vkasurinen.notemark.core.presentation.designsystem.theme.NoteMarkTheme
import org.koin.androidx.compose.koinViewModel
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.ui.focus.FocusRequester
import androidx.compose.ui.focus.focusRequester
import androidx.compose.ui.platform.LocalConfiguration
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.TextRange
import androidx.compose.ui.text.input.TextFieldValue
import androidx.navigation.NavHostController
import com.vkasurinen.notemark.app.navigation.NavigationRoute
import com.vkasurinen.notemark.core.presentation.designsystem.theme.SpaceGrotesk
import com.vkasurinen.notemark.core.presentation.util.ObserveAsEvents
import com.vkasurinen.notemark.notes.presentation.notes_details.components.Mode
import com.vkasurinen.notemark.notes.presentation.notes_details.components.ModeSwitcher
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.Job
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import org.koin.core.parameter.parametersOf
import timber.log.Timber

@Composable
fun EditDetailScreenRoot(
    navController: NavHostController,
    noteId: String,
    viewModel: EditDetailViewModel = koinViewModel() { parametersOf(noteId) }
) {
    val state by viewModel.state.collectAsStateWithLifecycle()
    val context = LocalContext.current

    ObserveAsEvents(viewModel.events) { event ->
        when (event) {
            is EditDetailEvent.NavigateToViewDetail -> {
                navController.navigate("${NavigationRoute.ViewDetail.route}/$noteId") {
                    popUpTo(NavigationRoute.Notes.route) { inclusive = false }
                    launchSingleTop = true
                }
            }

            EditDetailEvent.NavigateToReaderDetail -> {
                navController.navigate("${NavigationRoute.ReaderDetail.route}/$noteId") {
                    popUpTo(NavigationRoute.Notes.route) { inclusive = false }
                    launchSingleTop = true
                }
            }

            is EditDetailEvent.ShowValidationError -> {
                Toast.makeText(context, event.message, Toast.LENGTH_SHORT).show()
            }
        }
    }


    if (state.id.isNotBlank()) {
        EditDetailScreen(
            state = state,
            onAction = viewModel::onAction,
            navController = navController
        )
    } else {
        Box(
            modifier = Modifier.fillMaxSize(),
            contentAlignment = Alignment.Center
        ) {
            CircularProgressIndicator()
        }
    }
}

@Composable
fun EditDetailScreen(
    state: EditDetailState,
    onAction: (EditDetailAction) -> Unit,
    navController: NavHostController
) {
    val configuration = LocalConfiguration.current
    val isLandscape = configuration.orientation == Configuration.ORIENTATION_LANDSCAPE
    var showDiscardDialog by remember { mutableStateOf(false) }
    val focusRequester = remember { FocusRequester() }
    var debounceJob by remember { mutableStateOf<Job?>(null) }

    val titleValue = remember(state.title) {
        TextFieldValue(
            text = state.title,
            selection = TextRange(state.title.length)
        )
    }

    LaunchedEffect(Unit) {
        focusRequester.requestFocus()
    }

    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(MaterialTheme.colorScheme.surfaceVariant)
    ) {

        Column(
            modifier = Modifier
                .fillMaxSize()
                .background(MaterialTheme.colorScheme.surfaceVariant)
                .padding(top = if (isLandscape) 40.dp else 50.dp)
        ) {
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 16.dp, vertical = 12.dp),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                IconButton(
                    onClick = {
                        val hasChanges =
                            state.title != state.originalTitle || state.content != state.originalContent
                        if (state.content.isBlank()) {
                            Toast.makeText(
                                navController.context,
                                "Content cannot be empty. Please add some text.",
                                Toast.LENGTH_SHORT
                            ).show()
                        } else if (hasChanges) {
                            showDiscardDialog = true
                        } else {
                            navController.navigate("${NavigationRoute.ViewDetail.route}/${state.id}") {
                                popUpTo(NavigationRoute.Notes.route) { inclusive = false }
                                launchSingleTop = true
                            }
                        }
                    },
                    modifier = Modifier.size(30.dp)
                ) {
                    Icon(
                        imageVector = Icons.Filled.Cross,
                        contentDescription = "Close",
                        tint = MaterialTheme.colorScheme.onSurface,
                        modifier = Modifier.size(22.dp)
                    )
                }

            }

            Column(
                modifier = Modifier
                    .weight(1f)
                    .verticalScroll(rememberScrollState())
                    .padding(
                        horizontal = if (isLandscape) 48.dp else 16.dp,
                        vertical = 16.dp
                    )
            ) {
                BasicTextField(
                    value = titleValue,
                    onValueChange = {
                        onAction(EditDetailAction.OnTitleChange(it.text))
                        debounceJob?.cancel()
                        debounceJob = CoroutineScope(Dispatchers.Main).launch {
                            delay(3000)
                            onAction(EditDetailAction.OnSaveClick)
                        }
                    },
                        textStyle = TextStyle(
                        fontSize = 30.sp,
                        fontWeight = FontWeight.Bold,
                        color = MaterialTheme.colorScheme.onSurface,
                        fontFamily = SpaceGrotesk
                    ),
                    modifier = Modifier
                        .fillMaxWidth()
                        .focusRequester(focusRequester),
                    decorationBox = { titleTextField ->
                        Box {
                            if (state.title.isEmpty()) {
                                Text(
                                    text = "Note Title",
                                    style = TextStyle(
                                        fontSize = 30.sp,
                                        fontWeight = FontWeight.Bold,
                                        color = MaterialTheme.colorScheme.onSurfaceVariant,
                                        fontFamily = SpaceGrotesk
                                    )
                                )
                            }
                            titleTextField()
                        }
                    }
                )

                Spacer(modifier = Modifier.height(16.dp))

                HorizontalDivider(
                    modifier = Modifier
                        .fillMaxWidth()
                        .alpha(0.5f),
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )

                Spacer(modifier = Modifier.height(16.dp))

                BasicTextField(
                    value = state.content,
                    onValueChange = { newContent ->
                        Timber.d("Content text field changed: '$newContent'")
                        onAction(EditDetailAction.OnContentChange(newContent))
                        debounceJob?.cancel()
                        debounceJob = CoroutineScope(Dispatchers.Main).launch {
                            delay(3000)
                            Timber.d("Auto-save triggered after debounce")
                            onAction(EditDetailAction.OnSaveClick)
                        }
                    },

                        textStyle = TextStyle(
                        fontSize = 20.sp,
                        color = MaterialTheme.colorScheme.onSurface
                    ),
                    modifier = Modifier
                        .fillMaxWidth()
                        .fillMaxHeight(),
                    decorationBox = { contentTextField ->
                        Box {
                            if (state.content.isEmpty()) {
                                Text(
                                    text = "Tap to enter note content",
                                    style = TextStyle(
                                        fontSize = 16.sp,
                                        color = MaterialTheme.colorScheme.onSurfaceVariant
                                    )
                                )
                            }
                            contentTextField()
                        }
                    }
                )
            }
        }

        Box(
            modifier = Modifier
                .fillMaxSize()
                .padding(bottom = 50.dp),
            contentAlignment = Alignment.BottomCenter
        ) {
            ModeSwitcher(
                currentMode = Mode.EDIT,
                onModeSelected = { mode ->
                    when (mode) {
                        Mode.VIEW -> navController.popBackStack()
                        Mode.READ -> onAction(EditDetailAction.NavigateToReader)
                        else -> {}
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
                            if (state.content.isBlank() || !state.hasSavedOnce) {
                                Toast.makeText(
                                    navController.context,
                                    "Content hasn't been saved yet. Please wait before discarding.",
                                    Toast.LENGTH_SHORT
                                ).show()
                            } else {
                                debounceJob?.cancel()
                                showDiscardDialog = false
                                navController.popBackStack()
                            }
                        }
                    )

                }
            }
        )
    }
}


@Preview(
    showBackground = true,
    widthDp = 800,
    heightDp = 400,
    name = "Landscape"
)
@Composable
fun DetailScreenPreview() {
    NoteMarkTheme {
        EditDetailScreen(
            state = EditDetailState(
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