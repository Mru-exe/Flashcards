package cz.cvut.fel.kindlma7.flashcards.data.mapper

import cz.cvut.fel.kindlma7.flashcards.data.entity.ReviewRecordEntity
import cz.cvut.fel.kindlma7.flashcards.domain.ReviewRecord

fun ReviewRecordEntity.toDomain(): ReviewRecord {
    return ReviewRecord(
        id = id,
        flashcardId = flashcardId,
        reviewedAt = reviewedAt,
        quality = quality
    )
}

fun ReviewRecord.toEntity(): ReviewRecordEntity {
    return ReviewRecordEntity(
        id = id,
        flashcardId = flashcardId,
        reviewedAt = reviewedAt,
        quality = quality
    )
}

