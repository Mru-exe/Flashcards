package cz.cvut.fel.kindlma7.flashcards.ui.screen.decklist

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.viewModelScope
import cz.cvut.fel.kindlma7.flashcards.data.repository.DeckRepository
import cz.cvut.fel.kindlma7.flashcards.domain.Deck
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharedFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asSharedFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.catch
import kotlinx.coroutines.launch

sealed interface DeckListEffect {
    data class NavigateToFlashcards(val deckId: Long) : DeckListEffect
    data class NavigateToStudySession(val deckId: Long) : DeckListEffect
    data class ShowError(val message: String) : DeckListEffect
}

class DeckListViewModel(
    private val deckRepository: DeckRepository,
) : ViewModel() {

    private val _uiState = MutableStateFlow<DeckListUiState>(DeckListUiState.Loading)
    val uiState: StateFlow<DeckListUiState> = _uiState.asStateFlow()

    private val _effects = MutableSharedFlow<DeckListEffect>()
    val effects: SharedFlow<DeckListEffect> = _effects.asSharedFlow()

    init {
        viewModelScope.launch {
            deckRepository.getAll()
                .catch { e -> _uiState.value = DeckListUiState.Error(e.message ?: "Unknown error") }
                .collect { decks ->
                    val dialog = (_uiState.value as? DeckListUiState.Content)?.dialog
                    _uiState.value = DeckListUiState.Content(decks, dialog)
                }
        }
    }

    fun onEvent(event: DeckListEvent) {
        when (event) {
            is DeckListEvent.ShowCreateDeckDialog -> setDialog(DeckListUiState.DialogState.CreateDeck)
            is DeckListEvent.ShowEditDeckDialog -> setDialog(DeckListUiState.DialogState.EditDeck(event.deck))
            is DeckListEvent.ShowDeleteConfirmation -> setDialog(DeckListUiState.DialogState.ConfirmDelete(event.deck))
            is DeckListEvent.DismissDialog -> setDialog(null)
            is DeckListEvent.SubmitCreateDeck -> createDeck(event.name)
            is DeckListEvent.SubmitRenameDeck -> renameDeck(event.deck, event.newName)
            is DeckListEvent.ConfirmDeleteDeck -> deleteDeck(event.deck)
            is DeckListEvent.OpenFlashcards -> emit(DeckListEffect.NavigateToFlashcards(event.deck.id))
            is DeckListEvent.OpenStudySession -> emit(DeckListEffect.NavigateToStudySession(event.deck.id))
        }
    }

    private fun setDialog(dialog: DeckListUiState.DialogState?) {
        val current = _uiState.value as? DeckListUiState.Content ?: return
        _uiState.value = current.copy(dialog = dialog)
    }

    private fun createDeck(name: String) {
        setDialog(null)
        viewModelScope.launch {
            runCatching { deckRepository.insert(Deck(name = name, cardCount = 0, dueCount = 0)) }
                .onFailure { e -> _effects.emit(DeckListEffect.ShowError(e.message ?: "Failed to create deck")) }
        }
    }

    private fun renameDeck(deck: Deck, newName: String) {
        setDialog(null)
        viewModelScope.launch {
            runCatching { deckRepository.update(deck.copy(name = newName)) }
                .onFailure { e -> _effects.emit(DeckListEffect.ShowError(e.message ?: "Failed to rename deck")) }
        }
    }

    private fun deleteDeck(deck: Deck) {
        setDialog(null)
        viewModelScope.launch {
            runCatching { deckRepository.delete(deck) }
                .onFailure { e -> _effects.emit(DeckListEffect.ShowError(e.message ?: "Failed to delete deck")) }
        }
    }

    private fun emit(effect: DeckListEffect) {
        viewModelScope.launch { _effects.emit(effect) }
    }

    companion object {
        fun factory(deckRepository: DeckRepository): ViewModelProvider.Factory =
            object : ViewModelProvider.Factory {
                @Suppress("UNCHECKED_CAST")
                override fun <T : ViewModel> create(modelClass: Class<T>): T =
                    DeckListViewModel(deckRepository) as T
            }
    }
}
