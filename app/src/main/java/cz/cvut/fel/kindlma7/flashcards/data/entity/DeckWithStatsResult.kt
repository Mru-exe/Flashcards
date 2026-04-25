package cz.cvut.fel.kindlma7.flashcards.data.entity

data class DeckWithStatsResult(
    val id: Long,
    val name: String,
    val topicId: Int?,
    val topic: String,
    val createdAt: Long,
    val cardCount: Int,
    val dueCount: Int,
)
