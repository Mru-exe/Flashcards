package cz.cvut.fel.kindlma7.flashcards.data.dao

import androidx.room.Dao
import androidx.room.Delete
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import androidx.room.Update
import cz.cvut.fel.kindlma7.flashcards.data.entity.DeckEntity
import kotlinx.coroutines.flow.Flow

@Dao
interface DeckDao {

    @Insert(onConflict = OnConflictStrategy.ABORT)
    suspend fun insert(deckEntity: DeckEntity): Long

    @Update
    suspend fun update(deckEntity: DeckEntity)

    @Delete
    suspend fun delete(deckEntity: DeckEntity)

    @Delete
    suspend fun deleteAll(deckEntities: List<DeckEntity>)

    @Query("SELECT * FROM decks ORDER BY createdAt DESC")
    fun getAll(): Flow<List<DeckEntity>>

    @Query("SELECT * FROM decks WHERE id = :id")
    suspend fun getById(id: Long): DeckEntity?

    @Query("SELECT * FROM decks WHERE topicId = :topicId ORDER BY createdAt DESC")
    fun getByTopic(topicId: Int): Flow<List<DeckEntity>>

    @Query("SELECT * FROM decks WHERE name LIKE '%' || :query || '%' ORDER BY createdAt DESC")
    fun search(query: String): Flow<List<DeckEntity>>
}
