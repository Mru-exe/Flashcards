package cz.cvut.fel.kindlma7.flashcards.data

import androidx.room.Room
import androidx.test.core.app.ApplicationProvider
import androidx.test.ext.junit.runners.AndroidJUnit4
import cz.cvut.fel.kindlma7.flashcards.data.entity.DeckEntity
import cz.cvut.fel.kindlma7.flashcards.data.entity.FlashcardEntity
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.test.runTest
import org.junit.After
import org.junit.Assert
import org.junit.Before
import org.junit.Test
import org.junit.runner.RunWith

@RunWith(AndroidJUnit4::class)
class FlashcardEntityDaoTest {
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
    fun flashcard_insertAndGetByDeck() = runTest {
        val deckEntityId = db.deckDao().insert(DeckEntity(name = "Bio"))
        db.flashcardDao().insert(FlashcardEntity(deckId = deckEntityId, question = "Q1", answer = "A1"))
        db.flashcardDao().insert(FlashcardEntity(deckId = deckEntityId, question = "Q2", answer = "A2"))
        val cards = db.flashcardDao().getByDeck(deckEntityId).first()
        Assert.assertEquals(2, cards.size)
    }

    @Test
    fun flashcard_delete_cascadesFromDeck() = runTest {
        val deckEntityId = db.deckDao().insert(DeckEntity(name = "Cascade"))
        db.flashcardDao().insert(FlashcardEntity(deckId = deckEntityId, question = "Q", answer = "A"))
        val deck = db.deckDao().getById(deckEntityId)!!
        db.deckDao().delete(deck)
        val cards = db.flashcardDao().getByDeck(deckEntityId).first()
        Assert.assertTrue(cards.isEmpty())
    }

    @Test
    fun flashcard_getDueCards_returnsOnlyOverdue() = runTest {
        val deckEntityId = db.deckDao().insert(DeckEntity(name = "Study"))
        val now = System.currentTimeMillis()
        db.flashcardDao().insert(
            FlashcardEntity(
                deckId = deckEntityId,
                question = "Due",
                answer = "A",
                nextReviewAt = now - 1000
            )
        )
        db.flashcardDao().insert(
            FlashcardEntity(
                deckId = deckEntityId,
                question = "Future",
                answer = "B",
                nextReviewAt = now + 100_000
            )
        )
        val due = db.flashcardDao().getDueCards(deckEntityId, now).first()
        Assert.assertEquals(1, due.size)
        Assert.assertEquals("Due", due[0].question)
    }

    @Test
    fun flashcard_search_matchesQuestionAndAnswer() = runTest {
        val deckEntityId = db.deckDao().insert(DeckEntity(name = "Lang"))
        db.flashcardDao()
            .insert(FlashcardEntity(deckId = deckEntityId, question = "What is Kotlin?", answer = "A language"))
        db.flashcardDao().insert(
            FlashcardEntity(
                deckId = deckEntityId,
                question = "What is Java?",
                answer = "Also a language"
            )
        )
        db.flashcardDao()
            .insert(FlashcardEntity(deckId = deckEntityId, question = "Unrelated", answer = "Nope"))
        val results = db.flashcardDao().search(deckEntityId, "language").first()
        Assert.assertEquals(2, results.size)
    }

    @Test
    fun flashcard_update_persistsSmState() = runTest {
        val deckEntityId = db.deckDao().insert(DeckEntity(name = "SM2"))
        val cardId =
            db.flashcardDao().insert(FlashcardEntity(deckId = deckEntityId, question = "Q", answer = "A"))
        val card = db.flashcardDao().getById(cardId)!!
        db.flashcardDao().update(card.copy(easeFactor = 2.1f, interval = 6, repetitions = 3))
        val updated = db.flashcardDao().getById(cardId)!!
        Assert.assertEquals(2.1f, updated.easeFactor, 0.001f)
        Assert.assertEquals(6, updated.interval)
        Assert.assertEquals(3, updated.repetitions)
    }
}