package cz.cvut.fel.kindlma7.flashcards.data.repository

import cz.cvut.fel.kindlma7.flashcards.data.dao.ReviewRecordDao
import cz.cvut.fel.kindlma7.flashcards.data.entity.ReviewRecordEntity
import kotlinx.coroutines.flow.Flow

class ReviewRecordRepository(private val reviewRecordDao: ReviewRecordDao) {

    suspend fun insert(record: ReviewRecordEntity) = reviewRecordDao.insert(record)

    fun getByFlashcard(flashcardId: Long): Flow<List<ReviewRecordEntity>> =
        reviewRecordDao.getByFlashcard(flashcardId)

    suspend fun countByDeckSince(deckId: Long, since: Long): Int =
        reviewRecordDao.countByDeckSince(deckId, since)
}
