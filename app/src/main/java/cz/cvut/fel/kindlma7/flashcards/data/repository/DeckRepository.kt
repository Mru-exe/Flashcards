package cz.cvut.fel.kindlma7.flashcards.data.repository

import cz.cvut.fel.kindlma7.flashcards.data.dao.DeckDao
import cz.cvut.fel.kindlma7.flashcards.data.mapper.toDomain
import cz.cvut.fel.kindlma7.flashcards.data.mapper.toEntity
import cz.cvut.fel.kindlma7.flashcards.domain.Deck
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map

class DeckRepository(private val deckDao: DeckDao) {

    fun getAll(): Flow<List<Deck>> = deckDao.getAll().map { entities ->
        entities.map { it.toDomain() }
    }

    suspend fun getById(id: Long): Deck? = deckDao.getById(id)?.toDomain()

    fun getByTopic(topicId: Int): Flow<List<Deck>> = deckDao.getByTopic(topicId).map { entities ->
        entities.map { it.toDomain() }
    }

    suspend fun insert(deck: Deck): Long = deckDao.insert(deck.toEntity())

    suspend fun update(deck: Deck) = deckDao.update(deck.toEntity())

    suspend fun delete(deck: Deck) = deckDao.delete(deck.toEntity())

    suspend fun deleteAll(decks: List<Deck>) = deckDao.deleteAll(decks.map { it.toEntity() })
}
