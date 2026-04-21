package cz.cvut.fel.kindlma7.flashcards.data.entity

import androidx.room.Entity
import androidx.room.ForeignKey
import androidx.room.Index
import androidx.room.PrimaryKey

@Entity(
    tableName = "review_records",
    foreignKeys = [ForeignKey(
        entity = FlashcardEntity::class,
        parentColumns = ["id"],
        childColumns = ["flashcardId"],
        onDelete = ForeignKey.CASCADE
    )],
    indices = [Index("flashcardId")]
)
data class ReviewRecordEntity(
    @PrimaryKey(autoGenerate = true) val id: Long = 0,
    val flashcardId: Long,
    val reviewedAt: Long = System.currentTimeMillis(),
    val quality: Int  // SM-2 quality rating: 0–5
)
