package cz.cvut.fel.kindlma7.flashcards.data.api

import cz.cvut.fel.kindlma7.flashcards.domain.Topic
import java.net.URLDecoder

class TriviaRepository(private val api: OpenTdbApiService) {

    suspend fun fetchCategories(): List<Topic> {
        val response = api.getCategories()
        return response.triviaCategories.map { category ->
            Topic(id = category.id, name = decode(category.name))
        }
    }

    suspend fun fetchQuestions(categoryId: Int, difficulty: String): List<Pair<String, String>> {
        val response = api.getQuestions(category = categoryId, difficulty = difficulty)
        if (response.responseCode != 0) {
            throw IllegalStateException(errorMessage(response.responseCode))
        }
        return response.results.map { q ->
            decode(q.question) to decode(q.correctAnswer)
        }
    }

    private fun decode(value: String): String = URLDecoder.decode(value, "UTF-8")

    private fun errorMessage(code: Int) = when (code) {
        1 -> "No questions found for this category and difficulty"
        2 -> "Invalid request parameters"
        5 -> "Too many requests — please wait a moment and try again"
        else -> "API error (code $code)"
    }
}
