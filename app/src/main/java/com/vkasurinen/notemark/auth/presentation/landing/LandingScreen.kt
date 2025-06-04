package com.vkasurinen.notemark.auth.presentation.landing

import android.content.res.Configuration
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.IntrinsicSize
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.heightIn
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalConfiguration
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.vkasurinen.notemark.R
import com.vkasurinen.notemark.core.presentation.designsystem.buttons.NoteMarkButton
import com.vkasurinen.notemark.core.presentation.designsystem.buttons.NoteMarkButtonSecondary
import com.vkasurinen.notemark.core.presentation.designsystem.theme.NoteMarkTheme

@Composable
fun LandingRoot() {
    LandingScreen(
        onGetStartedClick = { /* Handle navigation */ },
        onLogInClick = { /* Handle navigation */ }
    )
}

@Composable
fun LandingScreen(
    onGetStartedClick: () -> Unit,
    onLogInClick: () -> Unit,
) {
    val configuration = LocalConfiguration.current
    val isLandscape = configuration.orientation == Configuration.ORIENTATION_LANDSCAPE
    val isTablet = configuration.smallestScreenWidthDp >= 600

    when {
        isLandscape -> {
            Row(
                modifier = Modifier
                    .fillMaxSize()
                    .background(Color(0xFFE0EAFF))
            ) {
                Box(
                    modifier = Modifier
                        .weight(0.6f)
                        .graphicsLayer { alpha = 0.9f }
                ) {
                    Image(
                        painter = painterResource(id = R.drawable.landing_background),
                        contentDescription = "Background with math equations",
                        contentScale = ContentScale.Crop,
                        modifier = Modifier.fillMaxSize()
                    )
                }

                Box(
                    modifier = Modifier
                        .weight(0.7f)
                        .fillMaxHeight(0.80f)
                        .background(
                            color = MaterialTheme.colorScheme.onPrimary.copy(alpha = 0.95f),
                            shape = RoundedCornerShape(topStart = 32.dp, bottomStart = 32.dp)
                        )
                        .align(Alignment.CenterVertically)
                        .padding(32.dp),
                    contentAlignment = Alignment.Center
                ) {
                    LandingContent(
                        onGetStartedClick = onGetStartedClick,
                        onLogInClick = onLogInClick,
                        modifier = Modifier.fillMaxWidth(),
                        textAlign = TextAlign.Start,
                        isLandscape = true
                    )
                }
            }
        }

        isTablet -> {
            Column(
                modifier = Modifier
                    .fillMaxSize()
                    .background(Color(0xFFE0EAFF))
            ) {
                Box(
                    modifier = Modifier
                        .weight(1f)
                        .fillMaxWidth()
                        .graphicsLayer { alpha = 0.9f }
                ) {
                    Image(
                        painter = painterResource(id = R.drawable.landing_background),
                        contentDescription = "Background with math equations",
                        contentScale = ContentScale.Crop,
                        modifier = Modifier.fillMaxSize()
                    )
                }

                Box(
                    modifier = Modifier
                        .fillMaxWidth(0.9f)
                        .height(300.dp)
                        .background(
                            color = MaterialTheme.colorScheme.onPrimary.copy(alpha = 0.95f),
                            shape = RoundedCornerShape(topStart = 32.dp, topEnd = 32.dp)
                        )
                        .align(Alignment.CenterHorizontally)
                        .padding(32.dp),
                    contentAlignment = Alignment.Center
                ) {
                    LandingContent(
                        onGetStartedClick = onGetStartedClick,
                        onLogInClick = onLogInClick,
                        modifier = Modifier.fillMaxWidth(),
                        textAlign = TextAlign.Center
                    )
                }
            }
        }

        else -> {
            Column(
                modifier = Modifier
                    .fillMaxSize()
                    .background(Color(0xFFE0EAFF))
            ) {
                Box(
                    modifier = Modifier
                        .weight(1f)
                        .fillMaxWidth()
                        .graphicsLayer { alpha = 0.9f }
                ) {
                    Image(
                        painter = painterResource(id = R.drawable.landing_background),
                        contentDescription = "Background with math equations",
                        contentScale = ContentScale.Crop,
                        modifier = Modifier.fillMaxSize()
                    )
                }

                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .weight(0.6f)
                        .background(
                            color = MaterialTheme.colorScheme.onPrimary.copy(alpha = 0.9f),
                            shape = RoundedCornerShape(topStart = 32.dp, topEnd = 32.dp)
                        )
                        .align(Alignment.CenterHorizontally)
                        .padding(24.dp)
                ) {
                    LandingContent(
                        onGetStartedClick = onGetStartedClick,
                        onLogInClick = onLogInClick,
                        modifier = Modifier.fillMaxWidth(),
                        textAlign = TextAlign.Start
                    )
                }
            }
        }
    }
}



@Composable
private fun LandingContent(
    onGetStartedClick: () -> Unit,
    onLogInClick: () -> Unit,
    modifier: Modifier = Modifier,
    textAlign: TextAlign = TextAlign.Left,
    isLandscape: Boolean = false
) {
    Column(
        modifier = modifier,
        verticalArrangement = if (isLandscape) Arrangement.SpaceEvenly else Arrangement.Center
    ) {
        Column(
            modifier = Modifier.weight(1f, fill = !isLandscape),
            verticalArrangement = Arrangement.Center
        ) {
            Text(
                text = "Your Own Collection of Notes",
                style = MaterialTheme.typography.headlineLarge,
                fontWeight = FontWeight.Bold,
                color = MaterialTheme.colorScheme.onSurface,
                modifier = Modifier.fillMaxWidth(),
                maxLines = 2,
                lineHeight = 40.sp,
                textAlign = textAlign
            )

            Spacer(modifier = Modifier.height(16.dp))

            Text(
                text = "Capture your thoughts and ideas.",
                style = MaterialTheme.typography.bodyLarge,
                color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.7f),
                textAlign = textAlign,
                modifier = Modifier.fillMaxWidth()
            )
        }

        Spacer(modifier = if (isLandscape) Modifier.height(18.dp) else Modifier.height(20.dp))

        Column(
            modifier = Modifier.fillMaxWidth(),
            verticalArrangement = Arrangement.spacedBy(16.dp)
        ) {
            NoteMarkButton(
                text = "Get Started",
                onClick = onGetStartedClick,
                modifier = Modifier.fillMaxWidth()
            )

            NoteMarkButtonSecondary(
                text = "Log In",
                onClick = onLogInClick,
                modifier = Modifier.fillMaxWidth()
            )
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
    showBackground = true,
    widthDp = 400,
    heightDp = 800,
    name = "Portrait"
)
@Preview(
    showBackground = true,
    widthDp = 800,
    heightDp = 1280,
    name = "Tablet Portrait"
)
@Preview(
    showBackground = true,
    widthDp = 1280,
    heightDp = 800,
    name = "Tablet Landscape"
)
@Composable
private fun Preview() {
    NoteMarkTheme {
        LandingScreen(
            onGetStartedClick = {},
            onLogInClick = {}
        )
    }
}