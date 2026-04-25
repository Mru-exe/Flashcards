package cz.cvut.fel.kindlma7.flashcards.data.dao

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import cz.cvut.fel.kindlma7.flashcards.data.entity.DeckRetentionResult
import cz.cvut.fel.kindlma7.flashcards.data.entity.ReviewRecordEntity
import kotlinx.coroutines.flow.Flow

@Dao
interface ReviewRecordDao {

    @Insert(onConflict = OnConflictStrategy.ABORT)
    suspend fun insert(record: ReviewRecordEntity)

    @Query("SELECT * FROM review_records WHERE flashcardId = :flashcardId ORDER BY reviewedAt DESC")
    fun getByFlashcard(flashcardId: Long): Flow<List<ReviewRecordEntity>>

    // Used to calculate streak — reviews for a card within a time window
    @Query("SELECT * FROM review_records WHERE flashcardId = :flashcardId AND reviewedAt >= :since ORDER BY reviewedAt DESC")
    suspend fun getByFlashcardSince(flashcardId: Long, since: Long): List<ReviewRecordEntity>

    // Count of all reviews across a deck since a given time (e.g. today) — useful for stats/streak
    @Query("""
        SELECT COUNT(*) FROM review_records
        WHERE flashcardId IN (SELECT id FROM flashcards WHERE deckId = :deckId)
        AND reviewedAt >= :since
    """)
    suspend fun countByDeckSince(deckId: Long, since: Long): Int

    @Query("SELECT COUNT(*) FROM review_records WHERE reviewedAt >= :since")
    fun countAllSince(since: Long): Flow<Int>

    @Query("SELECT COUNT(*) FROM review_records")
    fun countAll(): Flow<Int>

    @Query("""
        SELECT d.id as deckId, d.name as deckName,
               COUNT(rr.id) as totalReviews,
               SUM(CASE WHEN rr.quality >= 3 THEN 1 ELSE 0 END) as goodReviews
        FROM decks d
        INNER JOIN flashcards f ON f.deckId = d.id
        INNER JOIN review_records rr ON rr.flashcardId = f.id
        GROUP BY d.id, d.name
        HAVING COUNT(rr.id) > 0
        ORDER BY CAST(SUM(CASE WHEN rr.quality >= 3 THEN 1 ELSE 0 END) AS REAL) / COUNT(rr.id) DESC
        LIMIT 3
    """)
    fun getTop3ByRetention(): Flow<List<DeckRetentionResult>>
}
