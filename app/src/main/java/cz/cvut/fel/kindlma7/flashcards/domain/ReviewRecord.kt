package cz.cvut.fel.kindlma7.flashcards.domain

data class ReviewRecord(
    val id: Long = 0,
    val flashcardId: Long,
    val reviewedAt: Long = System.currentTimeMillis(),
    val quality: Int  // SM-2 quality rating: 0–5
)

