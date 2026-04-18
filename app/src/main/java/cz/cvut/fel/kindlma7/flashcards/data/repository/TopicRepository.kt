package cz.cvut.fel.kindlma7.flashcards.data.repository

import cz.cvut.fel.kindlma7.flashcards.data.dao.TopicDao
import cz.cvut.fel.kindlma7.flashcards.data.entity.Topic
import kotlinx.coroutines.flow.Flow

class TopicRepository(private val topicDao: TopicDao) {

    fun getAll(): Flow<List<Topic>> = topicDao.getAll()

    suspend fun getById(id: Int): Topic? = topicDao.getById(id)

    // TODO: napojit na api
    suspend fun syncFromApi(topics: List<Topic>) = topicDao.insertAll(topics)

    suspend fun delete(topic: Topic) = topicDao.delete(topic)
}
