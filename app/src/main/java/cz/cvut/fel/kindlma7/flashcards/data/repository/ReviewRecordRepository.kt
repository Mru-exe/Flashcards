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

    fun observeCountSince(since: Long): Flow<Int> = reviewRecordDao.observeCountSince(since)

    fun observeCount(): Flow<Int> = reviewRecordDao.observeCount()

    fun observeTop3DecksByRetention(): Flow<List<DeckRetentionStat>> =
        reviewRecordDao.observeTop3DecksByRetention().map { list ->
            list.map { DeckRetentionStat(it.deckName, it.retentionRate.toFloat(), it.reviewCount) }
        }
}
