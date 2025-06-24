@file:OptIn(ExperimentalMaterial3Api::class, ExperimentalMaterial3Api::class,
    ExperimentalMaterial3Api::class
)

package com.vkasurinen.notemark.notes.presentation.notes_overview

import android.content.res.Configuration
import android.widget.Toast
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
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

@Composable
fun NotesScreenRoot(
    navController: NavHostController,
    username: String? = null,
    viewModel: NotesViewModel = koinViewModel()
) {
    val state by viewModel.state.collectAsStateWithLifecycle()
    val context = LocalContext.current

    ObserveAsEvents(viewModel.events) { event ->
        when (event) {
            is NotesEvent.NavigateToDetail -> {
                navController.navigate("${NavigationRoute.Detail.route}/${event.noteId}") {
                    popUpTo(NavigationRoute.Detail.route) {inclusive = true}
                    launchSingleTop = true
                }
            }
            is NotesEvent.Error -> {
                Toast.makeText(context, event.error.asString(context), Toast.LENGTH_SHORT).show()
            }
        }
    }

    // Update username in ViewModel when changed
    LaunchedEffect(username) {
        username?.let {
            viewModel.onAction(NotesAction.UpdateUsername(it))
        }
    }

    NotesScreen(
        state = state,
        onAction = { action ->
            viewModel.onAction(action)
        }
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
        onFabClick = { onAction(NotesAction.CreateNewNote) }
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
                    .fillMaxSize()
                    .background(MaterialTheme.colorScheme.onSurfaceVariant.copy(alpha = 0.2f)),
                contentPadding = PaddingValues(8.dp),
                horizontalArrangement = Arrangement.spacedBy(8.dp),
                verticalItemSpacing = 8.dp
            ) {
                items(state.notes.size) { index ->
                    val note = state.notes[index]
                    if (note != null) {
                        NoteCard(
                            date = note.createdAt,
                            title = note.title,
                            description = note.content
                        )
                    }
                }
            }
        }
    }
}

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
                        lastEditedAt = "2023-10-01"
                    ),
                    Note(
                        id = "2",
                        title = "Note 2",
                        content = "Content of Note 2",
                        createdAt = "2023-10-02",
                        lastEditedAt = "2023-10-02"
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