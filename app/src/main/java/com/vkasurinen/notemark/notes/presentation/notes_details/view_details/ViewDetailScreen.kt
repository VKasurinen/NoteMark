package com.vkasurinen.notemark.notes.presentation.notes_details.view_details

import android.widget.Toast
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
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ChevronLeft
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
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
import androidx.compose.ui.draw.clip
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavHostController
import com.vkasurinen.notemark.app.navigation.NavigationRoute
import com.vkasurinen.notemark.core.presentation.designsystem.theme.Inter
import com.vkasurinen.notemark.core.presentation.designsystem.theme.NoteMarkTheme
import com.vkasurinen.notemark.core.presentation.designsystem.theme.SpaceGrotesk
import com.vkasurinen.notemark.core.presentation.util.ObserveAsEvents
import com.vkasurinen.notemark.notes.presentation.notes_details.components.Mode
import com.vkasurinen.notemark.notes.presentation.notes_details.components.ModeSwitcher
import com.vkasurinen.notemark.settings.SettingsAction
import io.ktor.http.parametersOf
import org.koin.androidx.compose.koinViewModel
import org.koin.core.parameter.parametersOf
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

@Composable
fun ViewDetailScreenRoot(
    navController: NavHostController,
    noteId: String,
    viewModel: ViewDetailViewModel = koinViewModel { parametersOf(noteId) }
) {
    val state by viewModel.state.collectAsStateWithLifecycle()
    val context = LocalContext.current

    ObserveAsEvents(viewModel.events) { event ->
        when (event) {
            ViewDetailEvent.NavigateBack -> {
                navController.popBackStack()
            }
            ViewDetailEvent.NavigateToEditDetail -> {
                navController.navigate("${NavigationRoute.EditDetail.route}/$noteId") {
                    popUpTo(NavigationRoute.ViewDetail.route) { inclusive = true }
                }
            }
            ViewDetailEvent.NavigateToReaderDetail -> {
                navController.navigate("${NavigationRoute.ReaderDetail.route}/$noteId") {
                    popUpTo(NavigationRoute.ViewDetail.route) { inclusive = true }
                }
            }
            is ViewDetailEvent.Error -> {
                Toast.makeText(context, event.error.asString(context), Toast.LENGTH_SHORT).show()
            }
        }
    }

    ViewDetailScreen(
        state = state,
        onAction = viewModel::onAction
    )
}

@Composable
fun ViewDetailScreen(
    state: ViewDetailState,
    onAction: (ViewDetailAction) -> Unit,
) {
    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(MaterialTheme.colorScheme.surfaceVariant)
    ) {
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(top = 50.dp, bottom = 30.dp)
        ) {
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 18.dp, vertical = 4.dp)
            ) {
                Box(
                    modifier = Modifier
                        .clip(RoundedCornerShape(8.dp))
                        .clickable { onAction(ViewDetailAction.NavigateBack) }
                        .padding(vertical = 8.dp)
                        .align(Alignment.CenterStart)
                ) {
                    Row(
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Icon(
                            imageVector = Icons.Default.ChevronLeft,
                            contentDescription = "Back",
                            tint = MaterialTheme.colorScheme.onSurfaceVariant,
                            modifier = Modifier.size(24.dp)
                        )

                        Spacer(modifier = Modifier.width(12.dp))

                        Text(
                            text = "ALL NOTES",
                            style = MaterialTheme.typography.titleSmall.copy(
                                color = MaterialTheme.colorScheme.onSurfaceVariant,
                                fontWeight = FontWeight.Bold
                            )
                        )
                    }
                }
            }


            Spacer(modifier = Modifier.height(18.dp))

            Column(
                modifier = Modifier
                    .weight(1f)
                    .padding(horizontal = 16.dp)
                    .verticalScroll(rememberScrollState())
            ) {
                Text(
                    text = state.title,
                    style = TextStyle(
                        fontSize = 30.sp,
                        fontWeight = FontWeight.Bold,
                        color = MaterialTheme.colorScheme.onSurface,
                        fontFamily = SpaceGrotesk
                    )
                )

                Spacer(modifier = Modifier.height(16.dp))

                HorizontalDivider(
                    modifier = Modifier
                        .fillMaxWidth()
                        .alpha(0.5f),
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )

                Spacer(modifier = Modifier.height(16.dp))

                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(90.dp)
                ) {
                    Column {
                        Text(
                            text = "Date created",
                            style = MaterialTheme.typography.labelLarge.copy(
                                color = MaterialTheme.colorScheme.onSurfaceVariant
                            )
                        )
                        Spacer(modifier = Modifier.height(4.dp))
                        Text(
                            text = formatDate(state.createdAt, isCreationDate = true),
                            style = MaterialTheme.typography.labelLarge.copy(
                                color = MaterialTheme.colorScheme.onSurface,
                                fontFamily = SpaceGrotesk
                            )
                        )
                    }

                    Column(horizontalAlignment = Alignment.Start) {
                        Text(
                            text = "Last edited",
                            style = MaterialTheme.typography.labelLarge.copy(
                                color = MaterialTheme.colorScheme.onSurfaceVariant
                            )
                        )
                        Spacer(modifier = Modifier.height(4.dp))
                        Text(
                            text = formatDate(state.lastEditedAt),
                            style = MaterialTheme.typography.labelLarge.copy(
                                color = MaterialTheme.colorScheme.onSurface,
                                fontFamily = SpaceGrotesk
                            )
                        )
                    }
                }

                Spacer(modifier = Modifier.height(16.dp))

                HorizontalDivider(
                    modifier = Modifier
                        .fillMaxWidth()
                        .alpha(0.5f),
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )

                Spacer(modifier = Modifier.height(16.dp))

                Text(
                    text = state.content,
                    style = TextStyle(
                        fontSize = 16.sp,
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )
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
                currentMode = Mode.VIEW,
                onModeSelected = { mode ->
                    when (mode) {
                        Mode.EDIT -> onAction(ViewDetailAction.NavigateToEdit)
                        Mode.READ -> onAction(ViewDetailAction.NavigateToReader)
                        else -> {}
                    }
                }
            )
        }
    }
}


fun formatDate(date: String, isCreationDate: Boolean = false): String {
    if (date.isBlank()) {
        return "Unknown date"
    }

    return try {
        val formatter = SimpleDateFormat("dd MMM yyyy, HH:mm", Locale.getDefault())
        val parsedDate = formatter.parse(date)

        if (!isCreationDate) {
            val now = System.currentTimeMillis()
            if (parsedDate != null && now - parsedDate.time < 5 * 60 * 1000) {
                return "Just now"
            }
        }

        formatter.format(parsedDate ?: Date())
    } catch (e: Exception) {
        date
    }
}

@Preview(showBackground = true)
@Composable
fun ModeSwitcherPreview() {
    NoteMarkTheme {
        var currentMode by remember { mutableStateOf(Mode.EDIT) } // Set to a supported mode

        ModeSwitcher(
            currentMode = currentMode,
            onModeSelected = { selectedMode ->
                currentMode = selectedMode
            }
        )
    }
}