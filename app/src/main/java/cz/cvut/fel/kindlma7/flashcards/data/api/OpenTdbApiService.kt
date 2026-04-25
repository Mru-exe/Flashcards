package cz.cvut.fel.kindlma7.flashcards.data.api

import retrofit2.http.GET
import retrofit2.http.Query

interface OpenTdbApiService {

    @GET("api_category.php")
    suspend fun getCategories(): CategoriesResponse

    @GET("api.php")
    suspend fun getQuestions(
        @Query("amount") amount: Int = 20,
        @Query("category") category: Int,
        @Query("difficulty") difficulty: String,
        @Query("type") type: String = "multiple",
        @Query("encode") encode: String = "url3986",
    ): QuestionsResponse
}
