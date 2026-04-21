package cz.cvut.fel.kindlma7.flashcards.data.repository

import cz.cvut.fel.kindlma7.flashcards.data.dao.TopicDao
import cz.cvut.fel.kindlma7.flashcards.data.entity.TopicEntity
import kotlinx.coroutines.flow.Flow

class TopicRepository(private val topicDao: TopicDao) {

    fun getAll(): Flow<List<TopicEntity>> = topicDao.getAll()

    suspend fun getById(id: Int): TopicEntity? = topicDao.getById(id)

    // TODO: napojit na api
    suspend fun syncFromApi(topicEntities: List<TopicEntity>) = topicDao.insertAll(topicEntities)

    suspend fun delete(topicEntity: TopicEntity) = topicDao.delete(topicEntity)
}
