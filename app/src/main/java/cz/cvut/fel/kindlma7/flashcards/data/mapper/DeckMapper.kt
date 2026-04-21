package cz.cvut.fel.kindlma7.flashcards.data.mapper

import cz.cvut.fel.kindlma7.flashcards.data.entity.DeckEntity
import cz.cvut.fel.kindlma7.flashcards.domain.Deck

fun DeckEntity.toDomain(): Deck {
    return Deck(
        id = id,
        name = name,
        topicId = topicId
    )
}

fun Deck.toEntity(): DeckEntity {
    return DeckEntity(
        id = id,
        name = name,
        topicId = topicId
    )
}