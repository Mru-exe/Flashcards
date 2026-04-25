package cz.cvut.fel.kindlma7.flashcards.ui.screen.decklist

import cz.cvut.fel.kindlma7.flashcards.domain.Deck

sealed interface DeckListUiState {
    data object Loading : DeckListUiState
    data class Content(
        val decks: List<Deck>,
        val searchQuery: String = "",
        val dialog: DialogState? = null,
    ) : DeckListUiState
    data class Error(val message: String) : DeckListUiState

    sealed interface DialogState {
        data object CreateDeck : DialogState
        data class EditDeck(val deck: Deck) : DialogState
        data class ConfirmDelete(val deck: Deck) : DialogState
    }
}
