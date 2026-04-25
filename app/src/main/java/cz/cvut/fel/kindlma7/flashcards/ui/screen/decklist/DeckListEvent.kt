package cz.cvut.fel.kindlma7.flashcards.ui.screen.decklist

import cz.cvut.fel.kindlma7.flashcards.domain.Deck

sealed interface DeckListEvent {
    data object ShowCreateDeckDialog : DeckListEvent
    data class ShowEditDeckDialog(val deck: Deck) : DeckListEvent
    data class ShowDeleteConfirmation(val deck: Deck) : DeckListEvent
    data object DismissDialog : DeckListEvent

    data class SubmitCreateDeck(val name: String) : DeckListEvent
    data class SubmitRenameDeck(val deck: Deck, val newName: String) : DeckListEvent
    data class ConfirmDeleteDeck(val deck: Deck) : DeckListEvent

    data class OpenFlashcards(val deck: Deck) : DeckListEvent
    data class OpenStudySession(val deck: Deck) : DeckListEvent

    data class UpdateSearchQuery(val query: String) : DeckListEvent
}
