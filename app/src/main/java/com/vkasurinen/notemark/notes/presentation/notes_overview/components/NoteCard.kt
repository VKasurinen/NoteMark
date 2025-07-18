package com.vkasurinen.notemark.notes.presentation.notes_overview.components

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.widthIn
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.platform.LocalConfiguration
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.vkasurinen.notemark.core.presentation.designsystem.theme.NoteMarkTheme
import java.time.Instant
import java.time.LocalDate
import java.time.ZoneId
import java.time.format.DateTimeFormatter
import java.util.Locale


@Composable
fun NoteCard(
    modifier: Modifier = Modifier,
    date: String,
    title: String,
    description: String
) {

    val configuration = LocalConfiguration.current
    val maxCharacters = if (configuration.screenWidthDp < 600) 150 else 250
    val truncatedDescription = if (description.length > maxCharacters) {
        description.take(maxCharacters) + "..."
    } else {
        description
    }

    Column(
        modifier = modifier
            .clip(RoundedCornerShape(12.dp))
            .background(MaterialTheme.colorScheme.surfaceVariant)
            .fillMaxWidth()
            .widthIn(max = 150.dp)
            .padding(12.dp)
    ) {
        Text(
            text = formatDate(date),
            color = MaterialTheme.colorScheme.primary,
            fontSize = 16.sp,
            fontWeight = FontWeight.W600
        )

        Spacer(modifier = Modifier.height(4.dp))

        Text(
            text = title,
            color = MaterialTheme.colorScheme.onSurface,
            style = MaterialTheme.typography.titleSmall.copy(
                lineHeight = 20.sp,
                fontWeight = FontWeight.SemiBold
            ),
            maxLines = 2,
            overflow = TextOverflow.Ellipsis
        )

        Spacer(modifier = Modifier.height(4.dp))

        Text(
            text = truncatedDescription,
            color = MaterialTheme.colorScheme.onSurfaceVariant,
            style = MaterialTheme.typography.labelSmall.copy(
                fontSize = 15.sp
            ),
            maxLines = Int.MAX_VALUE,
            overflow = TextOverflow.Ellipsis
        )

    }

}


fun formatDate(date: String): String {
    val parsedDate = Instant.parse(date).atZone(ZoneId.systemDefault()).toLocalDate()
    val currentYear = LocalDate.now().year
    val formatter = if (parsedDate.year == currentYear) {
        DateTimeFormatter.ofPattern("dd MMM", Locale.getDefault())
    } else {
        DateTimeFormatter.ofPattern("dd MMM yyyy", Locale.getDefault())
    }
    return parsedDate.format(formatter)
}

@Preview(
    showBackground = true
)
@Composable
private fun Preview() {
    NoteMarkTheme {
        NoteCard(
            date = "19 APR",
            title = "Title",
            description = "asdddddddddddddddddddddddddasdddddddddddddddddddddddddddddddddddddddddddddddddddddddddddddadssssssssssssssssssssssssssssssssssssssssssssssssssssdddddd"
        )
    }
}