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
class TopicDaoTest {
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
    fun topic_insertAllAndGetAll() = runTest {
        val topics = listOf(Topic(9, "General Knowledge"), Topic(11, "Film"), Topic(21, "Sports"))
        db.topicDao().insertAll(topics)
        val result = db.topicDao().getAll().first()
        Assert.assertEquals(3, result.size)
    }

    @Test
    fun topic_getById_returnsCorrectTopic() = runTest {
        db.topicDao().insertAll(listOf(Topic(9, "General Knowledge")))
        val topic = db.topicDao().getById(9)
        Assert.assertNotNull(topic)
        Assert.assertEquals("General Knowledge", topic!!.name)
    }

    @Test
    fun topic_insertAll_replacesOnConflict() = runTest {
        db.topicDao().insertAll(listOf(Topic(9, "Old Name")))
        db.topicDao().insertAll(listOf(Topic(9, "New Name")))
        val topic = db.topicDao().getById(9)
        Assert.assertEquals("New Name", topic!!.name)
    }

    @Test
    fun topic_delete_setsNullOnDecks() = runTest {
        db.topicDao().insertAll(listOf(Topic(1, "Science")))
        val deckId = db.deckDao().insert(Deck(name = "Biology", topicId = 1))
        db.topicDao().delete(Topic(1, "Science"))
        val deck = db.deckDao().getById(deckId)
        Assert.assertNull(deck!!.topicId)
    }
}