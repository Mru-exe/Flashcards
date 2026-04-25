package cz.cvut.fel.kindlma7.flashcards.data.repository

import cz.cvut.fel.kindlma7.flashcards.data.dao.FlashcardDao
import cz.cvut.fel.kindlma7.flashcards.data.mapper.toDomain
import cz.cvut.fel.kindlma7.flashcards.data.mapper.toEntity
import cz.cvut.fel.kindlma7.flashcards.domain.Flashcard
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map

class FlashcardRepository(private val flashcardDao: FlashcardDao) {

    fun getByDeck(deckId: Long): Flow<List<Flashcard>> = flashcardDao.getByDeck(deckId).map { entities ->
        entities.map { it.toDomain() }
    }

    suspend fun getById(id: Long): Flashcard? = flashcardDao.getById(id)?.toDomain()

    fun getDueCards(deckId: Long): Flow<List<Flashcard>> =
        flashcardDao.getDueCards(deckId, System.currentTimeMillis()).map { entities ->
            entities.map { it.toDomain() }
        }

    fun getAllDueCards(): Flow<List<Flashcard>> =
        flashcardDao.getAllDueCards(System.currentTimeMillis()).map { entities ->
            entities.map { it.toDomain() }
        }

    fun search(deckId: Long, query: String): Flow<List<Flashcard>> =
        flashcardDao.search(deckId, query).map { entities ->
            entities.map { it.toDomain() }
        }

    suspend fun insert(flashcard: Flashcard): Long = flashcardDao.insert(flashcard.toEntity())

    suspend fun insertAll(flashcards: List<Flashcard>) = flashcardDao.insertAll(flashcards.map { it.toEntity() })

    // Call this after computing new SM-2 values to persist the updated scheduling state
    suspend fun update(flashcard: Flashcard) = flashcardDao.update(flashcard.toEntity())

    suspend fun delete(flashcard: Flashcard) = flashcardDao.delete(flashcard.toEntity())

    suspend fun deleteAll(flashcards: List<Flashcard>) = flashcardDao.deleteAll(flashcards.map { it.toEntity() })
}
