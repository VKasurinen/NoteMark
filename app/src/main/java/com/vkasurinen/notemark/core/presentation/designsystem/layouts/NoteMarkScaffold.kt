package com.vkasurinen.notemark.core.presentation.designsystem.layouts

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.offset
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import com.vkasurinen.notemark.core.presentation.designsystem.theme.OnSurfaceVariant
import com.vkasurinen.notemark.core.presentation.designsystem.theme.Surface

@Composable
fun NoteMarkScaffold(
    modifier: Modifier = Modifier,
    topAppBar: @Composable () -> Unit = {},
    onFabClick: () -> Unit = {},
    content: @Composable (PaddingValues) -> Unit
) {
    Scaffold(
        topBar = topAppBar,
        floatingActionButton = {
            Box(
                modifier = Modifier
                    .size(56.dp)
                    .shadow(6.dp, shape = RoundedCornerShape(20.dp))
                    .background(
                        brush = Brush.verticalGradient(
                            colors = listOf(Color(0xFF58A1F8), Color(0xFF5A4CF7))
                        ),
                        shape = RoundedCornerShape(20.dp)
                    )
                    .clickable { onFabClick() },
                contentAlignment = Alignment.Center
            ) {
                Icon(
                    imageVector = Icons.Default.Add,
                    contentDescription = "Add",
                    tint = Color.White
                )
            }
        },
        containerColor = MaterialTheme.colorScheme.surfaceVariant,
        modifier = modifier
    ) { padding ->
        content(padding)
    }
}