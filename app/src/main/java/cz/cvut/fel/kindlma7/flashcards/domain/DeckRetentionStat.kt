package cz.cvut.fel.kindlma7.flashcards.domain

data class DeckRetentionStat(
    val deckName: String,
    val retentionRate: Float,
    val reviewCount: Int,
)
