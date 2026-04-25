package cz.cvut.fel.kindlma7.flashcards.domain

data class DeckRetentionStat(
    val deckId: Long,
    val deckName: String,
    val totalReviews: Int,
    val goodReviews: Int,
) {
    val retentionRate: Float get() = goodReviews.toFloat() / totalReviews
}
