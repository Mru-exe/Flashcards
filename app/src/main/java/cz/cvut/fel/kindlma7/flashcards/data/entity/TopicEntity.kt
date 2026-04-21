package cz.cvut.fel.kindlma7.flashcards.data.entity

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "topics")
data class TopicEntity(
    @PrimaryKey val id: Int,   // matches OpenTDB category ID
    val name: String
)
