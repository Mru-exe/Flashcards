package cz.cvut.fel.kindlma7.flashcards.data.dao

import androidx.room.Dao
import androidx.room.Delete
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import cz.cvut.fel.kindlma7.flashcards.data.entity.TopicEntity
import kotlinx.coroutines.flow.Flow

@Dao
interface TopicDao {

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertAll(topicEntities: List<TopicEntity>)

    @Query("SELECT * FROM topics ORDER BY name ASC")
    fun getAll(): Flow<List<TopicEntity>>

    @Query("SELECT * FROM topics WHERE id = :id")
    suspend fun getById(id: Int): TopicEntity?

    @Delete
    suspend fun delete(topicEntity: TopicEntity)
}
