package cz.cvut.fel.kindlma7.flashcards.data.dao

data class DeckRetentionResult(
    val deckId: Long,
    val deckName: String,
    val retentionRate: Double,
    val reviewCount: Int,
)
