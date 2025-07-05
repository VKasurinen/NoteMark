package com.vkasurinen.notemark.notes.presentation.notes_details.reader_details

import android.annotation.SuppressLint
import android.app.Activity
import android.content.pm.ActivityInfo
import android.widget.Toast
import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.gestures.detectTapGestures
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
import androidx.compose.foundation.layout.wrapContentWidth
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
import androidx.compose.runtime.DisposableEffect
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.alpha
import androidx.compose.ui.draw.clip
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavHostController
import com.vkasurinen.notemark.app.navigation.NavigationRoute
import com.vkasurinen.notemark.core.presentation.designsystem.theme.NoteMarkTheme
import com.vkasurinen.notemark.core.presentation.designsystem.theme.SpaceGrotesk
import com.vkasurinen.notemark.core.presentation.util.ObserveAsEvents
import com.vkasurinen.notemark.notes.presentation.notes_details.components.Mode
import com.vkasurinen.notemark.notes.presentation.notes_details.components.ModeSwitcher
import com.vkasurinen.notemark.notes.presentation.notes_details.view_details.ViewDetailAction
import com.vkasurinen.notemark.notes.presentation.notes_details.view_details.ViewDetailEvent
import com.vkasurinen.notemark.notes.presentation.notes_details.view_details.formatDate
import kotlinx.coroutines.Job
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import org.koin.androidx.compose.koinViewModel
import org.koin.core.parameter.parametersOf

@SuppressLint("SourceLockedOrientationActivity")
@Composable
fun ReaderScreenRoot(
    navController: NavHostController,
    noteId: String,
    viewModel: ReaderViewModel = koinViewModel { parametersOf(noteId) }
) {
    val state by viewModel.state.collectAsStateWithLifecycle()
    val context = LocalContext.current
    val activity = context as? Activity



    DisposableEffect(Unit) {
        activity?.requestedOrientation = ActivityInfo.SCREEN_ORIENTATION_LANDSCAPE

        onDispose {
            activity?.requestedOrientation = ActivityInfo.SCREEN_ORIENTATION_UNSPECIFIED

        }
    }


    ObserveAsEvents(viewModel.events) { event ->
        when (event) {
            ReaderEvent.NavigateToEditDetail -> {
                navController.navigate("${NavigationRoute.EditDetail.route}/$noteId") {
                    popUpTo(NavigationRoute.ReaderDetail.route) { inclusive = true }
                }
            }
            ReaderEvent.NavigateToViewDetail -> {
                navController.navigate("${NavigationRoute.ViewDetail.route}/$noteId") {
                    popUpTo(NavigationRoute.ReaderDetail.route) { inclusive = true }
                }
            }
            is ReaderEvent.Error -> {
                Toast.makeText(context, event.error.asString(context), Toast.LENGTH_SHORT).show()
            }
        }
    }

    ReaderScreen(
        state = state,
        onAction = viewModel::onAction,
        onScrollOrInteraction = viewModel::onScrollOrInteraction
    )

}

@Composable
fun ReaderScreen(
    state: ReaderState,
    onAction: (ReaderAction) -> Unit,
    onScrollOrInteraction: () -> Unit
) {
    val scrollState = rememberScrollState()

    LaunchedEffect(scrollState.isScrollInProgress) {
        if (scrollState.isScrollInProgress) {
            onScrollOrInteraction()
        }
    }

    val fadeAnim by animateFloatAsState(targetValue = if (state.uiVisible) 1f else 0f)

    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(MaterialTheme.colorScheme.surfaceVariant)
            .pointerInput(Unit) {
                detectTapGestures {
                    onAction(ReaderAction.ToggleUI)
                }
            }
    ) {

    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(top = 10.dp)
    ) {
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 18.dp, vertical = 2.dp)
                .alpha(fadeAnim)
        ) {
            Box(
                modifier = Modifier
                    .clip(RoundedCornerShape(8.dp))
                    .clickable { onAction(ReaderAction.NavigateToView) }
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


        Box(
            modifier = Modifier
                .fillMaxSize()
                .padding(horizontal = 20.dp)
                .weight(1f)
        ) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.Center
            ) {
                Column(
                    modifier = Modifier
                        .padding(horizontal = 16.dp)
                        .verticalScroll(scrollState),
                ) {
                    Text(
                        text = state.title,
                        modifier = Modifier.fillMaxWidth(0.7f),
                        style = TextStyle(
                            textAlign = TextAlign.Start,
                            fontSize = 30.sp,
                            fontWeight = FontWeight.Bold,
                            color = MaterialTheme.colorScheme.onSurface,
                            fontFamily = SpaceGrotesk
                        )
                    )


                    Spacer(modifier = Modifier.height(16.dp))

                    HorizontalDivider(
                        modifier = Modifier
                            .fillMaxWidth(0.7f)
                            .align(Alignment.CenterHorizontally)
                            .alpha(0.5f),
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )

                    Spacer(modifier = Modifier.height(16.dp))

                    Row(
                        modifier = Modifier
                            .wrapContentWidth()
                            .align(Alignment.Start),
                        horizontalArrangement = Arrangement.spacedBy(32.dp)
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

                        Spacer(modifier = Modifier.width(60.dp))


                        Column {
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
                            .fillMaxWidth(0.7f)
                            .align(Alignment.CenterHorizontally)
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
        }
    }
}

    Box(
        modifier = Modifier
            .fillMaxSize()
            .padding(bottom = 20.dp)
            .alpha(fadeAnim),
        contentAlignment = Alignment.BottomCenter
    ) {
        ModeSwitcher(
            currentMode = Mode.READ,
            onModeSelected = { mode ->
                when (mode) {
                    Mode.EDIT -> onAction(ReaderAction.NavigateToEdit)
                    Mode.READ -> onAction(ReaderAction.NavigateToView)
                    else -> {}
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
@Preview
@Composable
private fun Preview() {
    NoteMarkTheme {
        val scrollState = rememberScrollState()
        ReaderScreen(
            state = ReaderState(
                createdAt = "12.4.2025",
                lastEditedAt = "Just now",
                content = "asdasdaddsadsasdadsasdasddasdasasd"
            ),


            onAction = {},
            onScrollOrInteraction = {}
        )
    }
}