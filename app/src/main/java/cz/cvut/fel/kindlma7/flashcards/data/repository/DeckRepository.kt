package cz.cvut.fel.kindlma7.flashcards.data.repository

import cz.cvut.fel.kindlma7.flashcards.data.dao.DeckDao
import cz.cvut.fel.kindlma7.flashcards.data.entity.DeckEntity
import kotlinx.coroutines.flow.Flow

class DeckRepository(private val deckDao: DeckDao) {

    fun getAll(): Flow<List<DeckEntity>> = deckDao.getAll()

    suspend fun getById(id: Long): DeckEntity? = deckDao.getById(id)

    fun getByTopic(topicId: Int): Flow<List<DeckEntity>> = deckDao.getByTopic(topicId)

    suspend fun insert(deckEntity: DeckEntity): Long = deckDao.insert(deckEntity)

    suspend fun update(deckEntity: DeckEntity) = deckDao.update(deckEntity)

    suspend fun delete(deckEntity: DeckEntity) = deckDao.delete(deckEntity)

    suspend fun deleteAll(deckEntities: List<DeckEntity>) = deckDao.deleteAll(deckEntities)
}
