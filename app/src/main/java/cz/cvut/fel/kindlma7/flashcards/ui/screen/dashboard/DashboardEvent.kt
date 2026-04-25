package cz.cvut.fel.kindlma7.flashcards.ui.screen.dashboard

sealed interface DashboardEvent {
    data object ReviewAll : DashboardEvent
}

sealed interface DashboardEffect {
    data object NavigateToReviewAll : DashboardEffect
}
