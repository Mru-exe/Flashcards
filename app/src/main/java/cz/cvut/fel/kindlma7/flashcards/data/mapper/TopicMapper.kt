package cz.cvut.fel.kindlma7.flashcards.data.mapper

import cz.cvut.fel.kindlma7.flashcards.data.entity.TopicEntity
import cz.cvut.fel.kindlma7.flashcards.domain.Topic

fun TopicEntity.toDomain(): Topic {
    return Topic(
        id = id,
        name = name
    )
}

fun Topic.toEntity(): TopicEntity {
    return TopicEntity(
        id = id,
        name = name
    )
}

