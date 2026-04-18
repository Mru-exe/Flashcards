package cz.cvut.fel.kindlma7.flashcards.data.entity

import androidx.room.Entity
import androidx.room.ForeignKey
import androidx.room.Index
import androidx.room.PrimaryKey

@Entity(
    tableName = "flashcards",
    foreignKeys = [ForeignKey(
        entity = Deck::class,
        parentColumns = ["id"],
        childColumns = ["deckId"],
        onDelete = ForeignKey.CASCADE
    )],
    indices = [Index("deckId")]
)
data class Flashcard(
    @PrimaryKey(autoGenerate = true) val id: Long = 0,
    val deckId: Long,
    val question: String,
    val answer: String,
    val createdAt: Long = System.currentTimeMillis(),
    // SM-2 scheduling state
    val easeFactor: Float = 2.5f,   // starts at 2.5, updated after each review
    val interval: Int = 1,           // days until next review
    val repetitions: Int = 0,        // consecutive successful reviews
    val nextReviewAt: Long = System.currentTimeMillis()
)
