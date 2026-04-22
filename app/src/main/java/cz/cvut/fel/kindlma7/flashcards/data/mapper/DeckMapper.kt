package cz.cvut.fel.kindlma7.flashcards.data.mapper

import cz.cvut.fel.kindlma7.flashcards.data.entity.DeckEntity
import cz.cvut.fel.kindlma7.flashcards.domain.Deck

fun DeckEntity.toDomain(cardCount: Int, dueCount: Int): Deck {
    return Deck(
        id = id,
        name = name,
        topicId = topicId,
        cardCount = cardCount,
        dueCount = dueCount
    )
}

fun Deck.toEntity(): DeckEntity {
    return DeckEntity(
        id = id,
        name = name,
        topicId = topicId
    )
}