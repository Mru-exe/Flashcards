package cz.cvut.fel.kindlma7.flashcards.ui.screen.flashcardlist

import cz.cvut.fel.kindlma7.flashcards.domain.Flashcard

sealed interface FlashcardListEvent {
    data object ShowCreateFlashcardDialog : FlashcardListEvent
    data class ShowEditFlashcardDialog(val flashcard: Flashcard) : FlashcardListEvent
    data class ShowDeleteConfirmation(val flashcard: Flashcard) : FlashcardListEvent
    data object DismissDialog : FlashcardListEvent

    data class SubmitCreateFlashcard(val question: String, val answer: String) : FlashcardListEvent
    data class SubmitEditFlashcard(val flashcard: Flashcard, val newQuestion: String, val newAnswer: String) : FlashcardListEvent
    data class ConfirmDeleteFlashcard(val flashcard: Flashcard) : FlashcardListEvent

    data object NavigateBack : FlashcardListEvent
    data object StartStudySession : FlashcardListEvent

    data class UpdateSearchQuery(val query: String) : FlashcardListEvent
}
