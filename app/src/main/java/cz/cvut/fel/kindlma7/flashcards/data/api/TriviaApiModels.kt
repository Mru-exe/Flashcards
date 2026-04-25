package cz.cvut.fel.kindlma7.flashcards.data.api

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
data class CategoriesResponse(
    @SerialName("trivia_categories") val triviaCategories: List<TriviaApiCategory>
)

@Serializable
data class TriviaApiCategory(
    val id: Int,
    val name: String,
)

@Serializable
data class QuestionsResponse(
    @SerialName("response_code") val responseCode: Int,
    val results: List<TriviaApiQuestion>,
)

@Serializable
data class TriviaApiQuestion(
    val question: String,
    @SerialName("correct_answer") val correctAnswer: String,
)
