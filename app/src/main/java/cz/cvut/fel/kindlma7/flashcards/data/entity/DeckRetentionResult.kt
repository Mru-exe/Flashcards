package cz.cvut.fel.kindlma7.flashcards.data.entity

data class DeckRetentionResult(
    val deckId: Long,
    val deckName: String,
    val totalReviews: Int,
    val goodReviews: Int,
)
