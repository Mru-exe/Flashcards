package cz.cvut.fel.kindlma7.flashcards.domain

data class Flashcard(
    val id: Long = 0,
    val deckId: Long,
    val question: String,
    val answer: String,
    val createdAt: Long = System.currentTimeMillis(),
    // SM-2 scheduling state
    val easeFactor: Float = 2.5f,
    val interval: Int = 1,
    val repetitions: Int = 0,
    val nextReviewAt: Long = System.currentTimeMillis()
)

