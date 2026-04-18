package cz.cvut.fel.kindlma7.flashcards.data.dao

import androidx.room.Dao
import androidx.room.Delete
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import androidx.room.Update
import cz.cvut.fel.kindlma7.flashcards.data.entity.Deck
import kotlinx.coroutines.flow.Flow

@Dao
interface DeckDao {

    @Insert(onConflict = OnConflictStrategy.ABORT)
    suspend fun insert(deck: Deck): Long

    @Update
    suspend fun update(deck: Deck)

    @Delete
    suspend fun delete(deck: Deck)

    @Delete
    suspend fun deleteAll(decks: List<Deck>)

    @Query("SELECT * FROM decks ORDER BY createdAt DESC")
    fun getAll(): Flow<List<Deck>>

    @Query("SELECT * FROM decks WHERE id = :id")
    suspend fun getById(id: Long): Deck?

    @Query("SELECT * FROM decks WHERE topicId = :topicId ORDER BY createdAt DESC")
    fun getByTopic(topicId: Int): Flow<List<Deck>>
}
