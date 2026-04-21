package cz.cvut.fel.kindlma7.flashcards.data

import android.content.Context
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase
import cz.cvut.fel.kindlma7.flashcards.data.dao.DeckDao
import cz.cvut.fel.kindlma7.flashcards.data.dao.FlashcardDao
import cz.cvut.fel.kindlma7.flashcards.data.dao.ReviewRecordDao
import cz.cvut.fel.kindlma7.flashcards.data.dao.TopicDao
import cz.cvut.fel.kindlma7.flashcards.data.entity.DeckEntity
import cz.cvut.fel.kindlma7.flashcards.data.entity.FlashcardEntity
import cz.cvut.fel.kindlma7.flashcards.data.entity.ReviewRecordEntity
import cz.cvut.fel.kindlma7.flashcards.data.entity.TopicEntity

@Database(
    entities = [TopicEntity::class, DeckEntity::class, FlashcardEntity::class, ReviewRecordEntity::class],
    version = 1,
    exportSchema = false
)
abstract class AppDatabase : RoomDatabase() {

    abstract fun topicDao(): TopicDao
    abstract fun deckDao(): DeckDao
    abstract fun flashcardDao(): FlashcardDao
    abstract fun reviewRecordDao(): ReviewRecordDao

    companion object {
        @Volatile
        private var instance: AppDatabase? = null

        fun getInstance(context: Context): AppDatabase =
            instance ?: synchronized(this) {
                instance ?: Room.databaseBuilder(
                    context.applicationContext,
                    AppDatabase::class.java,
                    "flashcards.db"
                ).build().also { instance = it }
            }
    }
}
