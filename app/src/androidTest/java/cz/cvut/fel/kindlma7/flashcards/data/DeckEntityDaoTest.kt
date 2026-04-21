package cz.cvut.fel.kindlma7.flashcards.data

import androidx.room.Room
import androidx.test.core.app.ApplicationProvider
import androidx.test.ext.junit.runners.AndroidJUnit4
import cz.cvut.fel.kindlma7.flashcards.data.entity.DeckEntity
import cz.cvut.fel.kindlma7.flashcards.data.entity.TopicEntity
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.test.runTest
import org.junit.After
import org.junit.Assert
import org.junit.Before
import org.junit.Test
import org.junit.runner.RunWith

@RunWith(AndroidJUnit4::class)
class DeckEntityDaoTest {
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
    fun deck_insertAndGetById() = runTest {
        val id = db.deckDao().insert(DeckEntity(name = "Science"))
        val deck = db.deckDao().getById(id)
        Assert.assertNotNull(deck)
        Assert.assertEquals("Science", deck!!.name)
    }

    @Test
    fun deck_getAll_returnsInsertedDecks() = runTest {
        db.deckDao().insert(DeckEntity(name = "History"))
        db.deckDao().insert(DeckEntity(name = "Math"))
        val decks = db.deckDao().getAll().first()
        Assert.assertEquals(2, decks.size)
    }

    @Test
    fun deck_delete_removesFromDb() = runTest {
        val id = db.deckDao().insert(DeckEntity(name = "ToDelete"))
        val deck = db.deckDao().getById(id)!!
        db.deckDao().delete(deck)
        Assert.assertNull(db.deckDao().getById(id))
    }

    @Test
    fun deck_getByTopic_filtersCorrectly() = runTest {
        db.topicDao()
            .insertAll(listOf(TopicEntity(id = 1, name = "General"), TopicEntity(id = 2, name = "Sports")))
        db.deckDao().insert(DeckEntity(name = "General Deck", topicId = 1))
        db.deckDao().insert(DeckEntity(name = "Sports Deck", topicId = 2))
        val decks = db.deckDao().getByTopic(1).first()
        Assert.assertEquals(1, decks.size)
        Assert.assertEquals("General Deck", decks[0].name)
    }

    @Test
    fun deck_update_persistsChanges() = runTest {
        val id = db.deckDao().insert(DeckEntity(name = "Old"))
        val deck = db.deckDao().getById(id)!!
        db.deckDao().update(deck.copy(name = "New"))
        Assert.assertEquals("New", db.deckDao().getById(id)!!.name)
    }
}