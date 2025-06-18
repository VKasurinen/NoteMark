@file:OptIn(ExperimentalMaterial3Api::class, ExperimentalMaterial3Api::class)

package com.vkasurinen.notemark.notes.presentation

import android.widget.Toast
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.ModalDrawerSheet
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavHostController
import com.vkasurinen.notemark.core.presentation.designsystem.layouts.NoteMarkScaffold
import com.vkasurinen.notemark.core.presentation.designsystem.theme.NoteMarkTheme
import com.vkasurinen.notemark.core.presentation.util.ObserveAsEvents
import com.vkasurinen.notemark.notes.presentation.components.UserInitialsBox
import com.vkasurinen.notemark.notes.presentation.components.getUserInitials
import org.koin.androidx.compose.koinViewModel

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
    ) {
        Column(
            modifier = Modifier
                .padding(it)
                .fillMaxSize()
        ) {
            Text("Welcome, ${state.username ?: "Guest"}")
        }
    }
}


@Preview(
    showBackground = true
)
@Composable
private fun Preview() {
    NoteMarkTheme {
        NotesScreen(
            state = NotesState(),
            onAction = {}
        )
    }
}