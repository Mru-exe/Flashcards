package cz.cvut.fel.kindlma7.flashcards.domain

data class Deck(
    val id: Long = 0,
    val name: String,
    val topicId: Int? = null,
    val cardCount: Int,
    val dueCount: Int
)