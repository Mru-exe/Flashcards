package cz.cvut.fel.kindlma7.flashcards.data.repository

import cz.cvut.fel.kindlma7.flashcards.data.dao.DeckDao
import cz.cvut.fel.kindlma7.flashcards.data.entity.Deck
import kotlinx.coroutines.flow.Flow

class DeckRepository(private val deckDao: DeckDao) {

    fun getAll(): Flow<List<Deck>> = deckDao.getAll()

    suspend fun getById(id: Long): Deck? = deckDao.getById(id)

    fun getByTopic(topicId: Int): Flow<List<Deck>> = deckDao.getByTopic(topicId)

    suspend fun insert(deck: Deck): Long = deckDao.insert(deck)

    suspend fun update(deck: Deck) = deckDao.update(deck)

    suspend fun delete(deck: Deck) = deckDao.delete(deck)

    suspend fun deleteAll(decks: List<Deck>) = deckDao.deleteAll(decks)
}
