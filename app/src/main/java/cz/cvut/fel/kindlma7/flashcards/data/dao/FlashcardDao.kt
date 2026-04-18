package cz.cvut.fel.kindlma7.flashcards.data.dao

import androidx.room.Dao
import androidx.room.Delete
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import androidx.room.Update
import cz.cvut.fel.kindlma7.flashcards.data.entity.Flashcard
import kotlinx.coroutines.flow.Flow

@Dao
interface FlashcardDao {

    @Insert(onConflict = OnConflictStrategy.ABORT)
    suspend fun insert(flashcard: Flashcard): Long

    @Insert(onConflict = OnConflictStrategy.ABORT)
    suspend fun insertAll(flashcards: List<Flashcard>)

    // Used after each SM-2 review to persist updated scheduling state
    @Update
    suspend fun update(flashcard: Flashcard)

    @Delete
    suspend fun delete(flashcard: Flashcard)

    @Delete
    suspend fun deleteAll(flashcards: List<Flashcard>)

    @Query("SELECT * FROM flashcards WHERE deckId = :deckId ORDER BY createdAt ASC")
    fun getByDeck(deckId: Long): Flow<List<Flashcard>>

    @Query("SELECT * FROM flashcards WHERE id = :id")
    suspend fun getById(id: Long): Flashcard?

    // Cards whose next review time has passed — used by the SM-2 study session
    @Query("SELECT * FROM flashcards WHERE deckId = :deckId AND nextReviewAt <= :now ORDER BY nextReviewAt ASC")
    fun getDueCards(deckId: Long, now: Long): Flow<List<Flashcard>>

    // Full-text search within a deck (A3: filtering/search)
    @Query("SELECT * FROM flashcards WHERE deckId = :deckId AND (question LIKE '%' || :query || '%' OR answer LIKE '%' || :query || '%')")
    fun search(deckId: Long, query: String): Flow<List<Flashcard>>
}
