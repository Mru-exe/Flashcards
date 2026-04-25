package cz.cvut.fel.kindlma7.flashcards.ui.screen.dashboard

import cz.cvut.fel.kindlma7.flashcards.domain.DeckRetentionStat

data class DashboardUiState(
    val dueCount: Int = 0,
    val reviewedThisMonth: Int = 0,
    val reviewedLifetime: Int = 0,
    val top3Decks: List<DeckRetentionStat> = emptyList(),
)
