package cz.cvut.fel.kindlma7.flashcards.ui.screen.dashboard

import cz.cvut.fel.kindlma7.flashcards.domain.DeckRetentionStat

sealed interface DashboardUiState {
    data object Loading : DashboardUiState
    data class Content(
        val totalDueCards: Int,
        val reviewsThisMonth: Int,
        val reviewsLifetime: Int,
        val topDecks: List<DeckRetentionStat>,
    ) : DashboardUiState
    data class Error(val message: String) : DashboardUiState
}
