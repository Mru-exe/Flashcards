package cz.cvut.fel.kindlma7.flashcards.ui.screen.import

import android.net.Uri
import cz.cvut.fel.kindlma7.flashcards.domain.Topic

sealed interface ImportUiState {
    data object Loading : ImportUiState
    data class Content(
        val selectedTab: ImportTab = ImportTab.TRIVIA,
        val trivia: TriviaState = TriviaState(),
        val csv: CsvState = CsvState(),
        val importing: Boolean = false,
    ) : ImportUiState
    data class Error(val message: String) : ImportUiState
}

enum class ImportTab { TRIVIA, CSV }

data class TriviaState(
    val allTopics: List<Topic> = emptyList(),
    val searchQuery: String = "",
    val selectedTopic: Topic? = null,
    val selectedDifficulty: Difficulty = Difficulty.EASY,
    val deckName: String = "",
) {
    val filteredTopics: List<Topic>
        get() = if (searchQuery.isBlank()) allTopics
        else allTopics.filter { it.name.contains(searchQuery, ignoreCase = true) }
}

data class CsvState(
    val deckName: String = "",
    val fileUri: Uri? = null,
    val fileName: String? = null,
    val rowCount: Int? = null,
    val lines: List<String> = emptyList(),
)

enum class Difficulty(val apiValue: String, val label: String) {
    EASY("easy", "Easy"),
    MEDIUM("medium", "Medium"),
    HARD("hard", "Hard"),
}
