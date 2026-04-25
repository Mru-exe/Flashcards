package cz.cvut.fel.kindlma7.flashcards.ui.component

import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.animation.core.tween
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.heightIn
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.ElevatedCard
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import cz.cvut.fel.kindlma7.flashcards.ui.theme.FlashcardsTheme

enum class FlashcardSide { QUESTION, ANSWER }

@Composable
fun FlashcardFace(
    front: String,
    back: String,
    onFlipped: () -> Unit = {},
    modifier: Modifier = Modifier,
) {
    var flipped by remember { mutableStateOf(false) }
    var clickable by remember { mutableStateOf(true) }
    val rotation by animateFloatAsState(
        targetValue = if (flipped) 180f else 0f,
        animationSpec = tween(durationMillis = 300),
        label = "card-flip",
        finishedListener = {
            clickable = true
            if (flipped) onFlipped()
        },
    )

    val showBack = rotation > 90f
    val side = if (showBack) FlashcardSide.ANSWER else FlashcardSide.QUESTION
    val text = if (showBack) back else front
    val containerColor = when (side) {
        FlashcardSide.QUESTION -> MaterialTheme.colorScheme.primaryContainer
        FlashcardSide.ANSWER -> MaterialTheme.colorScheme.secondaryContainer
    }
    val contentColor = when (side) {
        FlashcardSide.QUESTION -> MaterialTheme.colorScheme.onPrimaryContainer
        FlashcardSide.ANSWER -> MaterialTheme.colorScheme.onSecondaryContainer
    }
    val label = when (side) {
        FlashcardSide.QUESTION -> "Question"
        FlashcardSide.ANSWER -> "Answer"
    }

    ElevatedCard(
        onClick = {
            if (clickable) {
                clickable = false
                flipped = !flipped
            }
        },
        modifier = modifier
            .fillMaxWidth()
            .heightIn(min = 200.dp)
            .graphicsLayer {
                rotationY = rotation
                cameraDistance = 12f * density
            },
        colors = CardDefaults.elevatedCardColors(containerColor = containerColor),
    ) {
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .heightIn(min = 200.dp)
                .padding(24.dp)
                .graphicsLayer { rotationY = if (showBack) 180f else 0f },
            contentAlignment = Alignment.Center,
        ) {
            Text(
                text = text,
                style = MaterialTheme.typography.headlineSmall,
                color = contentColor,
                textAlign = TextAlign.Center,
            )
            Text(
                text = label,
                style = MaterialTheme.typography.labelSmall,
                color = contentColor.copy(alpha = 0.6f),
                modifier = Modifier.align(Alignment.TopStart),
            )
        }
    }
}

@Preview(showBackground = true)
@Composable
private fun FlashcardFacePreview() {
    FlashcardsTheme {
        FlashcardFace(
            front = "What does the SM-2 algorithm optimize?",
            back = "The spacing between repetitions to minimize forgetting.",
            modifier = Modifier.padding(16.dp),
        )
    }
}
