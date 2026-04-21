package cz.cvut.fel.kindlma7.flashcards.data.mapper

import cz.cvut.fel.kindlma7.flashcards.data.entity.FlashcardEntity
import cz.cvut.fel.kindlma7.flashcards.domain.Flashcard

fun FlashcardEntity.toDomain(): Flashcard {
    return Flashcard(
        id = id,
        deckId = deckId,
        question = question,
        answer = answer,
        createdAt = createdAt,
        easeFactor = easeFactor,
        interval = interval,
        repetitions = repetitions,
        nextReviewAt = nextReviewAt
    )
}

fun Flashcard.toEntity(): FlashcardEntity {
    return FlashcardEntity(
        id = id,
        deckId = deckId,
        question = question,
        answer = answer,
        createdAt = createdAt,
        easeFactor = easeFactor,
        interval = interval,
        repetitions = repetitions,
        nextReviewAt = nextReviewAt
    )
}

