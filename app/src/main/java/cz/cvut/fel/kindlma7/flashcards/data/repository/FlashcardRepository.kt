package cz.cvut.fel.kindlma7.flashcards.data.repository

import cz.cvut.fel.kindlma7.flashcards.data.dao.FlashcardDao
import cz.cvut.fel.kindlma7.flashcards.data.entity.FlashcardEntity
import kotlinx.coroutines.flow.Flow

class FlashcardRepository(private val flashcardDao: FlashcardDao) {

    fun getByDeck(deckId: Long): Flow<List<FlashcardEntity>> = flashcardDao.getByDeck(deckId)

    suspend fun getById(id: Long): FlashcardEntity? = flashcardDao.getById(id)

    fun getDueCards(deckId: Long): Flow<List<FlashcardEntity>> =
        flashcardDao.getDueCards(deckId, System.currentTimeMillis())

    fun search(deckId: Long, query: String): Flow<List<FlashcardEntity>> =
        flashcardDao.search(deckId, query)

    suspend fun insert(flashcardEntity: FlashcardEntity): Long = flashcardDao.insert(flashcardEntity)

    suspend fun insertAll(flashcardEntities: List<FlashcardEntity>) = flashcardDao.insertAll(flashcardEntities)

    // Call this after computing new SM-2 values to persist the updated scheduling state
    suspend fun update(flashcardEntity: FlashcardEntity) = flashcardDao.update(flashcardEntity)

    suspend fun delete(flashcardEntity: FlashcardEntity) = flashcardDao.delete(flashcardEntity)

    suspend fun deleteAll(flashcardEntities: List<FlashcardEntity>) = flashcardDao.deleteAll(flashcardEntities)
}
