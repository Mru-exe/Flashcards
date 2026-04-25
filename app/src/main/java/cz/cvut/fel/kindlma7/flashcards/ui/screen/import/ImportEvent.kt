package cz.cvut.fel.kindlma7.flashcards.ui.screen.import

import android.net.Uri
import cz.cvut.fel.kindlma7.flashcards.domain.Topic

sealed interface ImportEvent {
    data class SelectTab(val tab: ImportTab) : ImportEvent

    data class SelectTopic(val topic: Topic) : ImportEvent
    data class SelectDifficulty(val difficulty: Difficulty) : ImportEvent
    data class UpdateTriviaSearch(val query: String) : ImportEvent
    data class UpdateTriviaDeckName(val name: String) : ImportEvent
    data object ImportFromTrivia : ImportEvent

    data class UpdateCsvDeckName(val name: String) : ImportEvent
    data class CsvFileSelected(val uri: Uri, val fileName: String, val lines: List<String>) : ImportEvent
    data object ImportFromCsv : ImportEvent

    data object RetryLoadTopics : ImportEvent
}
