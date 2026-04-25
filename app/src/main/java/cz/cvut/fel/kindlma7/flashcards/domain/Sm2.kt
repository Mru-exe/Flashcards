package cz.cvut.fel.kindlma7.flashcards.domain

private const val MIN_EASE_FACTOR = 1.3f
private const val MS_PER_DAY = 24 * 60 * 60 * 1000L

fun applySmReview(card: Flashcard, quality: Int): Flashcard {
    val newEF = (card.easeFactor + (0.1f - (5 - quality) * (0.08f + (5 - quality) * 0.02f)))
        .coerceAtLeast(MIN_EASE_FACTOR)

    return if (quality < 3) {
        card.copy(
            easeFactor = newEF,
            repetitions = 0,
            interval = 1,
            nextReviewAt = System.currentTimeMillis() + MS_PER_DAY,
        )
    } else {
        val newInterval = when (card.repetitions) {
            0 -> 1
            1 -> 6
            else -> (card.interval * card.easeFactor).toInt().coerceAtLeast(1)
        }
        card.copy(
            easeFactor = newEF,
            repetitions = card.repetitions + 1,
            interval = newInterval,
            nextReviewAt = System.currentTimeMillis() + newInterval * MS_PER_DAY,
        )
    }
}
