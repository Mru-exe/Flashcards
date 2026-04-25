package cz.cvut.fel.kindlma7.flashcards

import android.content.Context
import com.jakewharton.retrofit2.converter.kotlinx.serialization.asConverterFactory
import cz.cvut.fel.kindlma7.flashcards.data.AppDatabase
import cz.cvut.fel.kindlma7.flashcards.data.api.OpenTdbApiService
import cz.cvut.fel.kindlma7.flashcards.data.api.TriviaRepository
import cz.cvut.fel.kindlma7.flashcards.data.repository.DeckRepository
import cz.cvut.fel.kindlma7.flashcards.data.repository.FlashcardRepository
import cz.cvut.fel.kindlma7.flashcards.data.repository.ReviewRecordRepository
import cz.cvut.fel.kindlma7.flashcards.data.repository.TopicRepository
import kotlinx.serialization.json.Json
import okhttp3.MediaType.Companion.toMediaType
import retrofit2.Retrofit

class AppContainer(context: Context) {
    private val db = AppDatabase.getInstance(context)

    val deckRepository = DeckRepository(db.deckDao(), db.flashcardDao())
    val flashcardRepository = FlashcardRepository(db.flashcardDao())
    val reviewRecordRepository = ReviewRecordRepository(db.reviewRecordDao())
    val topicRepository = TopicRepository(db.topicDao())

    private val json = Json { ignoreUnknownKeys = true }
    private val retrofit = Retrofit.Builder()
        .baseUrl("https://opentdb.com/")
        .addConverterFactory(json.asConverterFactory("application/json".toMediaType()))
        .build()

    val triviaRepository = TriviaRepository(retrofit.create(OpenTdbApiService::class.java))
}
