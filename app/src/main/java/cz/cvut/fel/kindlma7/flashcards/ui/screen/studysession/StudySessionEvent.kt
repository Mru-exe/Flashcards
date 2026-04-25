package cz.cvut.fel.kindlma7.flashcards.ui.screen.studysession

import cz.cvut.fel.kindlma7.flashcards.ui.component.ReviewRating

sealed interface StudySessionEvent {
    data class SubmitRating(val rating: ReviewRating) : StudySessionEvent
    data object NavigateBack : StudySessionEvent
}
