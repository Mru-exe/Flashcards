package cz.cvut.fel.kindlma7.flashcards

import android.content.Context
import cz.cvut.fel.kindlma7.flashcards.data.AppDatabase
import cz.cvut.fel.kindlma7.flashcards.data.repository.DeckRepository
import cz.cvut.fel.kindlma7.flashcards.data.repository.FlashcardRepository
import cz.cvut.fel.kindlma7.flashcards.data.repository.ReviewRecordRepository
import cz.cvut.fel.kindlma7.flashcards.data.repository.TopicRepository

class AppContainer(context: Context) {
    private val db = AppDatabase.getInstance(context)

    val deckRepository = DeckRepository(db.deckDao(), db.flashcardDao())
    val flashcardRepository = FlashcardRepository(db.flashcardDao())
    val reviewRecordRepository = ReviewRecordRepository(db.reviewRecordDao())
    val topicRepository = TopicRepository(db.topicDao())
}
