package cz.cvut.fel.kindlma7.flashcards.data

import androidx.room.Room
import androidx.test.core.app.ApplicationProvider
import androidx.test.ext.junit.runners.AndroidJUnit4
import cz.cvut.fel.kindlma7.flashcards.data.entity.Deck
import cz.cvut.fel.kindlma7.flashcards.data.entity.Flashcard
import cz.cvut.fel.kindlma7.flashcards.data.entity.ReviewRecord
import cz.cvut.fel.kindlma7.flashcards.data.entity.Topic
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.test.runTest
import org.junit.After
import org.junit.Assert
import org.junit.Before
import org.junit.Test
import org.junit.runner.RunWith

@RunWith(AndroidJUnit4::class)
class ReviewRecordDaoTest {
    private lateinit var db: AppDatabase

    @Before
    fun createDb() {
        db = Room.inMemoryDatabaseBuilder(
            ApplicationProvider.getApplicationContext(),
            AppDatabase::class.java
        ).allowMainThreadQueries().build()
    }

    @After
    fun closeDb() = db.close()

    @Test
    fun reviewRecord_insert_andGetByFlashcard() = runTest {
        val deckId = db.deckDao().insert(Deck(name = "Review"))
        val cardId =
            db.flashcardDao().insert(Flashcard(deckId = deckId, question = "Q", answer = "A"))
        db.reviewRecordDao().insert(ReviewRecord(flashcardId = cardId, quality = 4))
        db.reviewRecordDao().insert(ReviewRecord(flashcardId = cardId, quality = 3))
        val records = db.reviewRecordDao().getByFlashcard(cardId).first()
        Assert.assertEquals(2, records.size)
    }

    @Test
    fun reviewRecord_delete_cascadesFromFlashcard() = runTest {
        val deckId = db.deckDao().insert(Deck(name = "Cascade2"))
        val cardId =
            db.flashcardDao().insert(Flashcard(deckId = deckId, question = "Q", answer = "A"))
        db.reviewRecordDao().insert(ReviewRecord(flashcardId = cardId, quality = 5))
        val card = db.flashcardDao().getById(cardId)!!
        db.flashcardDao().delete(card)
        val records = db.reviewRecordDao().getByFlashcard(cardId).first()
        Assert.assertTrue(records.isEmpty())
    }

    @Test
    fun reviewRecord_countByDeckSince_countsCorrectly() = runTest {
        val deckId = db.deckDao().insert(Deck(name = "Stats"))
        val cardId =
            db.flashcardDao().insert(Flashcard(deckId = deckId, question = "Q", answer = "A"))
        val now = System.currentTimeMillis()
        db.reviewRecordDao()
            .insert(ReviewRecord(flashcardId = cardId, reviewedAt = now - 500, quality = 4))
        db.reviewRecordDao()
            .insert(ReviewRecord(flashcardId = cardId, reviewedAt = now - 200, quality = 3))
        db.reviewRecordDao()
            .insert(ReviewRecord(flashcardId = cardId, reviewedAt = now - 2_000_000, quality = 5))
        val count = db.reviewRecordDao().countByDeckSince(deckId, now - 1000)
        Assert.assertEquals(2, count)
    }

    @Test
    fun reviewRecord_getByFlashcardSince_filtersOldRecords() = runTest {
        val deckId = db.deckDao().insert(Deck(name = "Filter"))
        val cardId =
            db.flashcardDao().insert(Flashcard(deckId = deckId, question = "Q", answer = "A"))
        val now = System.currentTimeMillis()
        db.reviewRecordDao()
            .insert(ReviewRecord(flashcardId = cardId, reviewedAt = now - 100, quality = 5))
        db.reviewRecordDao()
            .insert(ReviewRecord(flashcardId = cardId, reviewedAt = now - 5000, quality = 2))
        val recent = db.reviewRecordDao().getByFlashcardSince(cardId, now - 1000)
        Assert.assertEquals(1, recent.size)
        Assert.assertEquals(5, recent[0].quality)
    }
}