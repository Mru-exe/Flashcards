package cz.cvut.fel.kindlma7.flashcards.data.repository

import cz.cvut.fel.kindlma7.flashcards.data.dao.DeckDao
import cz.cvut.fel.kindlma7.flashcards.data.dao.FlashcardDao
import cz.cvut.fel.kindlma7.flashcards.data.mapper.toDomain
import cz.cvut.fel.kindlma7.flashcards.data.mapper.toEntity
import cz.cvut.fel.kindlma7.flashcards.domain.Deck
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map

class DeckRepository(
    private val deckDao: DeckDao,
    private val flashcardDao: FlashcardDao
) {

    fun getAll(): Flow<List<Deck>> =
        deckDao.getAllWithStats(System.currentTimeMillis()).map { results ->
            results.map { it.toDomain() }
        }

    suspend fun getById(id: Long): Deck? {
        val entity = deckDao.getById(id) ?: return null
        val cardCount = flashcardDao.getCardCount(id)
        val dueCount = flashcardDao.getDueCount(id, System.currentTimeMillis())
        return entity.toDomain(cardCount, dueCount)
    }

    fun getByTopic(topicId: Int): Flow<List<Deck>> = deckDao.getByTopic(topicId).map { entities ->
        entities.map { entity ->
            val deckId = entity.id
            val cardCount = flashcardDao.getCardCount(deckId)
            val dueCount = flashcardDao.getDueCount(deckId, System.currentTimeMillis())
            entity.toDomain(cardCount, dueCount)
        }
    }

    fun search(query: String): Flow<List<Deck>> =
        deckDao.searchWithStats(query, System.currentTimeMillis()).map { results ->
            results.map { it.toDomain() }
        }

    suspend fun insert(deck: Deck): Long = deckDao.insert(deck.toEntity())

    suspend fun update(deck: Deck) = deckDao.update(deck.toEntity())

    suspend fun delete(deck: Deck) = deckDao.delete(deck.toEntity())

    suspend fun deleteAll(decks: List<Deck>) = deckDao.deleteAll(decks.map { it.toEntity() })
}
