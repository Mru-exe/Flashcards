package cz.cvut.fel.kindlma7.flashcards.data.repository

import cz.cvut.fel.kindlma7.flashcards.data.dao.ReviewRecordDao
import cz.cvut.fel.kindlma7.flashcards.data.entity.ReviewRecord
import kotlinx.coroutines.flow.Flow

class ReviewRecordRepository(private val reviewRecordDao: ReviewRecordDao) {

    suspend fun insert(record: ReviewRecord) = reviewRecordDao.insert(record)

    fun getByFlashcard(flashcardId: Long): Flow<List<ReviewRecord>> =
        reviewRecordDao.getByFlashcard(flashcardId)

    suspend fun countByDeckSince(deckId: Long, since: Long): Int =
        reviewRecordDao.countByDeckSince(deckId, since)
}
