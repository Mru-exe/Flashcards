package cz.cvut.fel.kindlma7.flashcards.data.dao

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import cz.cvut.fel.kindlma7.flashcards.data.dao.DeckRetentionResult
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
    fun observeCountSince(since: Long): Flow<Int>

    @Query("SELECT COUNT(*) FROM review_records")
    fun observeCount(): Flow<Int>

    @Query("""
        SELECT f.deckId, d.name AS deckName,
               CAST(SUM(CASE WHEN r.quality >= 3 THEN 1 ELSE 0 END) AS REAL) / COUNT(*) AS retentionRate,
               COUNT(*) AS reviewCount
        FROM review_records r
        INNER JOIN flashcards f ON r.flashcardId = f.id
        INNER JOIN decks d ON f.deckId = d.id
        GROUP BY f.deckId
        ORDER BY retentionRate DESC
        LIMIT 3
    """)
    fun observeTop3DecksByRetention(): Flow<List<DeckRetentionResult>>
}
