@file:OptIn(ExperimentalMaterial3Api::class, ExperimentalFoundationApi::class)

package com.vkasurinen.notemark.notes.presentation.notes_overview

import android.content.res.Configuration
import android.widget.Toast
import androidx.compose.foundation.ExperimentalFoundationApi
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.combinedClickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.navigation.NavHostController
import com.vkasurinen.notemark.core.presentation.designsystem.layouts.NoteMarkScaffold
import com.vkasurinen.notemark.core.presentation.designsystem.theme.NoteMarkTheme
import com.vkasurinen.notemark.notes.presentation.notes_overview.components.UserInitialsBox
import com.vkasurinen.notemark.notes.presentation.notes_overview.components.getUserInitials
import org.koin.androidx.compose.koinViewModel
import androidx.compose.foundation.lazy.staggeredgrid.LazyVerticalStaggeredGrid
import androidx.compose.foundation.lazy.staggeredgrid.StaggeredGridCells
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Settings
import androidx.compose.material.icons.outlined.Settings
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.TextButton
import androidx.compose.ui.Alignment
import androidx.compose.ui.draw.alpha
import androidx.compose.ui.platform.LocalConfiguration
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.stringResource
import com.vkasurinen.notemark.R
import com.vkasurinen.notemark.app.navigation.NavigationRoute
import com.vkasurinen.notemark.core.presentation.util.ObserveAsEvents
import com.vkasurinen.notemark.notes.domain.Note
import com.vkasurinen.notemark.notes.presentation.notes_overview.components.NoteCard
import org.koin.compose.getKoin
import org.koin.java.KoinJavaComponent.inject
import timber.log.Timber

@Composable
fun NotesScreenRoot(
    navController: NavHostController,
    viewModel: NotesViewModel = koinViewModel(),
) {
    val state by viewModel.state.collectAsStateWithLifecycle()
    val context = LocalContext.current

    ObserveAsEvents(viewModel.events) { event ->
        when (event) {
            is NotesEvent.NavigateToEditDetail -> {
                navController.navigate("${NavigationRoute.EditDetail.route}/${event.noteId}") {
                    popUpTo(NavigationRoute.Notes.route) { inclusive = false }
                    launchSingleTop = true
                }
            }

            is NotesEvent.NavigateToViewDetail -> {
                navController.navigate("${NavigationRoute.ViewDetail.route}/${event.noteId}") {
                    popUpTo(NavigationRoute.Notes.route) { inclusive = false }
                    launchSingleTop = true
                }
            }

            NotesEvent.NavigateToSettings -> {
                navController.navigate(NavigationRoute.Settings.route) {
                    popUpTo(NavigationRoute.Notes.route) {inclusive = false}
                    launchSingleTop = true
                }
            }

            is NotesEvent.Error -> {
                Toast.makeText(context, event.error.asString(context), Toast.LENGTH_SHORT).show()
            }
        }
    }

    NotesScreen(
        state = state,
        onAction = viewModel::onAction
    )
}

@Composable
fun NotesScreen(
    state: NotesState,
    onAction: (NotesAction) -> Unit
) {
    val initials = state.username?.let { getUserInitials(it) } ?: "NA"
    val configuration = LocalConfiguration.current
    val isLandscape = configuration.orientation == Configuration.ORIENTATION_LANDSCAPE
    val maxCharacters = if (configuration.screenWidthDp < 600) 150 else 250

    NoteMarkScaffold(
        topAppBar = {
            TopAppBar(
                title = {
                    Text(
                        text = "NoteMark",
                        style = MaterialTheme.typography.headlineMedium.copy(
                            fontWeight = FontWeight.SemiBold
                        )
                    )
                },
                actions = {
                    IconButton(
                        onClick = {
                            onAction(NotesAction.NavigateToSettings)
                        }
                    ) {
                        Icon(
                            imageVector = Icons.Outlined.Settings,
                            contentDescription = "Settings"
                        )
                    }

                    Spacer(Modifier.width(12.dp))

                    UserInitialsBox(
                        initials = initials,
                        modifier = Modifier.padding(end = 14.dp)
                    )
                },
                colors = TopAppBarDefaults.topAppBarColors(
                    containerColor = MaterialTheme.colorScheme.surfaceVariant
                ),
            )
        },
        onFabClick = { onAction(NotesAction.CreateNewNote) },
        containerColor = if (state.notes.isEmpty()) {
            MaterialTheme.colorScheme.surfaceVariant
        } else {
            MaterialTheme.colorScheme.onSurfaceVariant.copy(alpha = 0.2f)
        }
    ) { innerPadding ->
        if (state.notes.isEmpty()) {
            Column(
                modifier = Modifier
                    .padding(innerPadding)
                    .fillMaxSize()
                    .background(MaterialTheme.colorScheme.surfaceVariant),
                verticalArrangement = Arrangement.Top,
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                Text(
                    text = stringResource(R.string.empty_board),
                    style = MaterialTheme.typography.titleSmall.copy(
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    ),
                    modifier = Modifier.padding(top = 100.dp)
                )
            }
        } else {
            LazyVerticalStaggeredGrid(
                columns = StaggeredGridCells.Fixed(if (isLandscape) 3 else 2),
                modifier = Modifier
                    .padding(innerPadding)
                    .fillMaxSize(),
                contentPadding = PaddingValues(8.dp),
                horizontalArrangement = Arrangement.spacedBy(8.dp),
                verticalItemSpacing = 8.dp
            ) {
                items(state.notes.size) { index ->
                    val note = state.notes[index]
                    val truncatedContent = if (note.content.length > maxCharacters) {
                        note.content.take(maxCharacters) + "..."
                    } else {
                        note.content
                    }

                    NoteCard(
                        date = note.createdAt,
                        title = note.title,
                        description = truncatedContent,
                        modifier = Modifier
                            .combinedClickable(
                                onClick = { onAction(NotesAction.NavigateToViewDetail(note.id)) },
                                onLongClick = { onAction(NotesAction.ShowDeleteDialog(note)) }
                            )
                    )
                }
            }

            if (state.showDeleteDialog) {
                AlertDialog(
                    onDismissRequest = {
                        state.noteToDelete?.let { onAction(NotesAction.DismissDeleteDialog(it)) }
                    },
                    title = { Text("Delete Note?") },
                    text = { Text("Are you sure you want to delete this note? This action cannot be undone.") },
                    confirmButton = {
                        TextButton(
                            onClick = {
                                state.noteToDelete?.let {
                                    onAction(NotesAction.DeleteNote(it.id))
                                }
                            }
                        ) {
                            Text("Delete", color = MaterialTheme.colorScheme.error)
                        }
                    },
                    dismissButton = {
                        TextButton(
                            onClick = {
                                state.noteToDelete?.let {
                                    onAction(NotesAction.DismissDeleteDialog(it))
                                }
                            }
                        ) {
                            Text("Cancel")
                        }
                    }
                )
            }
        }
    }
}

@Preview(
    showBackground = true,
    widthDp = 800,
    heightDp = 1280,
    name = "Tablet Portrait"
)
@Preview(
    showBackground = true,
    widthDp = 800,
    heightDp = 400,
    name = "Landscape"
)
@Preview(
    showBackground = true
)
@Composable
private fun Preview() {
    NoteMarkTheme {
        NotesScreen(
            state = NotesState(
                notes = listOf(
                    Note(
                        id = "1",
                        title = "Note 1",
                        content = "Content of Note 1",
                        createdAt = "2023-10-01",
                        lastEditedAt = "2025-10-01"
                    ),
                    Note(
                        id = "2",
                        title = "Note 2",
                        content = "Content of Note 2",
                        createdAt = "2025-10-02",
                        lastEditedAt = "2025-02-02"
                    ),
                    Note(
                        id = "3",
                        title = "Note 3",
                        content = "Content of Note 3",
                        createdAt = "2023-10-03",
                        lastEditedAt = "2023-10-03"
                    ),
                    Note(
                        id = "4",
                        title = "Note 4",
                        content = "Content of Note 4",
                        createdAt = "2023-10-04",
                        lastEditedAt = "2023-10-04"
                    ),
                    Note(
                        id = "5",
                        title = "Note 5",
                        content = "Content of Note 5",
                        createdAt = "2023-10-05",
                        lastEditedAt = "2023-10-05"
                    ),
                    Note(
                        id = "6",
                        title = "Note 6",
                        content = "Content of Note 6",
                        createdAt = "2023-10-06",
                        lastEditedAt = "2023-10-06"
                    )
                )
            ),
            onAction = {},
        )
    }
}