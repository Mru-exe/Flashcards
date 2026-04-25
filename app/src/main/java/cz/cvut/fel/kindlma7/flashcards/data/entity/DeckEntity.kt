package cz.cvut.fel.kindlma7.flashcards.data.entity

import androidx.room.Entity
import androidx.room.ForeignKey
import androidx.room.Index
import androidx.room.PrimaryKey

@Entity(
    tableName = "decks",
    foreignKeys = [ForeignKey(
        entity = TopicEntity::class,
        parentColumns = ["id"],
        childColumns = ["topicId"],
        onDelete = ForeignKey.SET_NULL
    )],
    indices = [Index("topicId")]
)
data class DeckEntity(
    @PrimaryKey(autoGenerate = true) val id: Long = 0,
    val name: String,
    val topicId: Int? = null,
    val topic: String,
    val createdAt: Long = System.currentTimeMillis()
)
