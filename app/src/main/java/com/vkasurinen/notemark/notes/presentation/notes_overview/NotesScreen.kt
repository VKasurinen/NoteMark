@file:OptIn(ExperimentalMaterial3Api::class, ExperimentalMaterial3Api::class,
    ExperimentalMaterial3Api::class
)

package com.vkasurinen.notemark.notes.presentation.notes_overview

import android.content.res.Configuration
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
import androidx.compose.ui.platform.LocalContext
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
import androidx.compose.ui.platform.LocalConfiguration
import androidx.compose.ui.res.stringResource
import com.vkasurinen.notemark.R
import com.vkasurinen.notemark.notes.presentation.notes_overview.components.NoteCard

@Composable
fun NotesScreenRoot(
    navController: NavHostController,
    username: String? = null,
    viewModel: NotesViewModel = koinViewModel()
) {
    val state by viewModel.state.collectAsStateWithLifecycle()
    val context = LocalContext.current

    LaunchedEffect(username) {
        username?.let {
            viewModel.onAction(NotesAction.UpdateUsername(it))
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
    onAction: (NotesAction) -> Unit,
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
        onFabClick = {}
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
                    NoteCard(
                        date = note.date,
                        title = note.title,
                        description = note.description
                    )
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
                        date = "19 APR",
                        title = "Meeting Notes",
                        description = "Discuss project milestones and deadlines."
                    ),
                    Note(
                        date = "20 APR",
                        title = "Shopping List",
                        description = "Milk, eggs, bread, and coffee."
                    ),
                    Note(
                        date = "21 APR",
                        title = "Workout Plan",
                        description = "Morning run and evening yoga session."
                    ),
                    Note(
                        date = "22 APR",
                        title = "Book Recommendations",
                        description = "Read 'Atomic Habits' and 'Deep Work'."
                    ),
                    Note(
                        date = "23 APR",
                        title = "Weekend Plans",
                        description = "Visit the park and watch a movie."
                    ),
                    Note(
                        date = "24 APR",
                        title = "Weekend Plans 2",
                        description = "Visit 2 the park and watch a movie."
                    )
                )
            ),
            onAction = {}
        )
    }
}