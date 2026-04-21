package cz.cvut.fel.kindlma7.flashcards.ui.component

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.FilledTonalButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import cz.cvut.fel.kindlma7.flashcards.ui.theme.FlashcardsTheme
//TODO: zkontrolovat quality modifiery podle SM-2 specifikace
//TODO: Upravit barvy podle nového theme

enum class ReviewRating(val label: String, val quality: Int) {
    Again("Again", 0),
    Hard("Hard", 2),
    Okay("Okay", 3),
    Easy("Easy", 5),
}

@Composable
fun FlashcardReview(
    onRating: (ReviewRating) -> Unit,
    modifier: Modifier = Modifier,
) {
    Row(
        modifier = modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.spacedBy(6.dp),
    ) {
        ReviewRating.entries.forEach { rating ->
            FilledTonalButton(
                onClick = { onRating(rating) },
                modifier = Modifier.weight(1f),
                colors = ButtonDefaults.filledTonalButtonColors(
                    containerColor = rating.containerColor(),
                    contentColor = rating.contentColor(),
                ),
            ) {
                Column(horizontalAlignment = Alignment.CenterHorizontally) {
                    Text(
                        text = rating.label,
                        style = MaterialTheme.typography.labelLarge,
                    )
                }
            }
        }
    }
}

@Composable
private fun ReviewRating.containerColor(): Color = when (this) {
    ReviewRating.Again -> MaterialTheme.colorScheme.errorContainer
    ReviewRating.Hard -> MaterialTheme.colorScheme.tertiaryContainer
    ReviewRating.Okay -> MaterialTheme.colorScheme.secondaryContainer
    ReviewRating.Easy -> MaterialTheme.colorScheme.primaryContainer
}

@Composable
private fun ReviewRating.contentColor(): Color = when (this) {
    ReviewRating.Again -> MaterialTheme.colorScheme.onErrorContainer
    ReviewRating.Hard -> MaterialTheme.colorScheme.onTertiaryContainer
    ReviewRating.Okay -> MaterialTheme.colorScheme.onSecondaryContainer
    ReviewRating.Easy -> MaterialTheme.colorScheme.onPrimaryContainer
}

@Preview(showBackground = true)
@Composable
private fun FlashcardReviewPreview() {
    FlashcardsTheme {
        FlashcardReview(
            onRating = {},
            modifier = Modifier.padding(16.dp),
        )
    }
}
