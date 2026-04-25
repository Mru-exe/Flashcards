package cz.cvut.fel.kindlma7.flashcards.data.repository

import cz.cvut.fel.kindlma7.flashcards.data.dao.ReviewRecordDao
import cz.cvut.fel.kindlma7.flashcards.data.mapper.toDomain
import cz.cvut.fel.kindlma7.flashcards.data.mapper.toEntity
import cz.cvut.fel.kindlma7.flashcards.domain.DeckRetentionStat
import cz.cvut.fel.kindlma7.flashcards.domain.ReviewRecord
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map

class ReviewRecordRepository(private val reviewRecordDao: ReviewRecordDao) {

    suspend fun insert(record: ReviewRecord) = reviewRecordDao.insert(record.toEntity())

    fun getByFlashcard(flashcardId: Long): Flow<List<ReviewRecord>> =
        reviewRecordDao.getByFlashcard(flashcardId).map { entities ->
            entities.map { it.toDomain() }
        }

    suspend fun countByDeckSince(deckId: Long, since: Long): Int =
        reviewRecordDao.countByDeckSince(deckId, since)

    suspend fun countAllSince(since: Long): Int = reviewRecordDao.countAllSince(since)

    suspend fun countAll(): Int = reviewRecordDao.countAll()

    suspend fun getTop3ByRetention(): List<DeckRetentionStat> =
        reviewRecordDao.getTop3ByRetention().map { r ->
            DeckRetentionStat(r.deckId, r.deckName, r.totalReviews, r.goodReviews)
        }
}
