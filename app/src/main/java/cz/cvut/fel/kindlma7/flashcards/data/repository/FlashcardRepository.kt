package cz.cvut.fel.kindlma7.flashcards.data.repository

import cz.cvut.fel.kindlma7.flashcards.data.dao.FlashcardDao
import cz.cvut.fel.kindlma7.flashcards.data.entity.Flashcard
import kotlinx.coroutines.flow.Flow

class FlashcardRepository(private val flashcardDao: FlashcardDao) {

    fun getByDeck(deckId: Long): Flow<List<Flashcard>> = flashcardDao.getByDeck(deckId)

    suspend fun getById(id: Long): Flashcard? = flashcardDao.getById(id)

    fun getDueCards(deckId: Long): Flow<List<Flashcard>> =
        flashcardDao.getDueCards(deckId, System.currentTimeMillis())

    fun search(deckId: Long, query: String): Flow<List<Flashcard>> =
        flashcardDao.search(deckId, query)

    suspend fun insert(flashcard: Flashcard): Long = flashcardDao.insert(flashcard)

    suspend fun insertAll(flashcards: List<Flashcard>) = flashcardDao.insertAll(flashcards)

    // Call this after computing new SM-2 values to persist the updated scheduling state
    suspend fun update(flashcard: Flashcard) = flashcardDao.update(flashcard)

    suspend fun delete(flashcard: Flashcard) = flashcardDao.delete(flashcard)

    suspend fun deleteAll(flashcards: List<Flashcard>) = flashcardDao.deleteAll(flashcards)
}
