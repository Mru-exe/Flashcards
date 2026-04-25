package cz.cvut.fel.kindlma7.flashcards.ui.screen.studysession

import cz.cvut.fel.kindlma7.flashcards.domain.Flashcard

sealed interface StudySessionUiState {
    data object Loading : StudySessionUiState
    data class Active(
        val deckName: String,
        val currentCard: Flashcard,
        val currentIndex: Int,
        val totalCards: Int,
    ) : StudySessionUiState
    data class Empty(val deckName: String) : StudySessionUiState
    data class Complete(val deckName: String, val reviewedCount: Int) : StudySessionUiState
    data class Error(val message: String) : StudySessionUiState
}
