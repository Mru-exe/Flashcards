package cz.cvut.fel.kindlma7.flashcards.data.repository

import cz.cvut.fel.kindlma7.flashcards.data.dao.TopicDao
import cz.cvut.fel.kindlma7.flashcards.data.mapper.toDomain
import cz.cvut.fel.kindlma7.flashcards.data.mapper.toEntity
import cz.cvut.fel.kindlma7.flashcards.domain.Topic
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map

class TopicRepository(private val topicDao: TopicDao) {

    fun getAll(): Flow<List<Topic>> = topicDao.getAll().map { entities ->
        entities.map { it.toDomain() }
    }

    suspend fun getById(id: Int): Topic? = topicDao.getById(id)?.toDomain()

    // TODO: napojit na api
    suspend fun syncFromApi(topics: List<Topic>) = topicDao.insertAll(topics.map { it.toEntity() })

    suspend fun delete(topic: Topic) = topicDao.delete(topic.toEntity())
}
