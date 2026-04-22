package cz.cvut.fel.kindlma7.flashcards.ui.screen.flashcardlist

import cz.cvut.fel.kindlma7.flashcards.domain.Deck
import cz.cvut.fel.kindlma7.flashcards.domain.Flashcard

sealed interface FlashcardListUiState {
    data object Loading : FlashcardListUiState
    data class Content(
        val deck: Deck,
        val flashcards: List<Flashcard>,
        val dialog: DialogState? = null,
    ) : FlashcardListUiState
    data class Error(val message: String) : FlashcardListUiState

    sealed interface DialogState {
        data object CreateFlashcard : DialogState
        data class EditFlashcard(val flashcard: Flashcard) : DialogState
        data class ConfirmDelete(val flashcard: Flashcard) : DialogState
    }
}
