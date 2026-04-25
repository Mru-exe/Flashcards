package cz.cvut.fel.kindlma7.flashcards.ui.screen.flashcardlist

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.viewModelScope
import cz.cvut.fel.kindlma7.flashcards.data.repository.DeckRepository
import cz.cvut.fel.kindlma7.flashcards.data.repository.FlashcardRepository
import cz.cvut.fel.kindlma7.flashcards.domain.Flashcard
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharedFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asSharedFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.catch
import kotlinx.coroutines.flow.flatMapLatest
import kotlinx.coroutines.launch

sealed interface FlashcardListEffect {
    data object NavigateBack : FlashcardListEffect
    data object NavigateToStudySession : FlashcardListEffect
    data class ShowError(val message: String) : FlashcardListEffect
}

@OptIn(ExperimentalCoroutinesApi::class)
class FlashcardListViewModel(
    private val deckId: Long,
    private val flashcardRepository: FlashcardRepository,
    private val deckRepository: DeckRepository,
) : ViewModel() {

    private val _uiState = MutableStateFlow<FlashcardListUiState>(FlashcardListUiState.Loading)
    val uiState: StateFlow<FlashcardListUiState> = _uiState.asStateFlow()

    private val _effects = MutableSharedFlow<FlashcardListEffect>()
    val effects: SharedFlow<FlashcardListEffect> = _effects.asSharedFlow()

    private val _searchQuery = MutableStateFlow("")

    init {
        viewModelScope.launch {
            val deck = deckRepository.getById(deckId)
            if (deck == null) {
                _uiState.value = FlashcardListUiState.Error("Deck not found")
                return@launch
            }
            _searchQuery
                .flatMapLatest { query ->
                    if (query.isBlank()) flashcardRepository.getByDeck(deckId)
                    else flashcardRepository.search(deckId, query)
                }
                .catch { e -> _uiState.value = FlashcardListUiState.Error(e.message ?: "Unknown error") }
                .collect { flashcards ->
                    val query = _searchQuery.value
                    val dialog = (_uiState.value as? FlashcardListUiState.Content)?.dialog
                    _uiState.value = FlashcardListUiState.Content(deck, flashcards, query, dialog)
                }
        }
    }

    fun onEvent(event: FlashcardListEvent) {
        when (event) {
            is FlashcardListEvent.ShowCreateFlashcardDialog -> setDialog(FlashcardListUiState.DialogState.CreateFlashcard)
            is FlashcardListEvent.ShowEditFlashcardDialog -> setDialog(FlashcardListUiState.DialogState.EditFlashcard(event.flashcard))
            is FlashcardListEvent.ShowDeleteConfirmation -> setDialog(FlashcardListUiState.DialogState.ConfirmDelete(event.flashcard))
            is FlashcardListEvent.DismissDialog -> setDialog(null)
            is FlashcardListEvent.SubmitCreateFlashcard -> createFlashcard(event.question, event.answer)
            is FlashcardListEvent.SubmitEditFlashcard -> editFlashcard(event.flashcard, event.newQuestion, event.newAnswer)
            is FlashcardListEvent.ConfirmDeleteFlashcard -> deleteFlashcard(event.flashcard)
            is FlashcardListEvent.NavigateBack -> emit(FlashcardListEffect.NavigateBack)
            is FlashcardListEvent.StartStudySession -> emit(FlashcardListEffect.NavigateToStudySession)
            is FlashcardListEvent.UpdateSearchQuery -> _searchQuery.value = event.query
        }
    }

    private fun setDialog(dialog: FlashcardListUiState.DialogState?) {
        val current = _uiState.value as? FlashcardListUiState.Content ?: return
        _uiState.value = current.copy(dialog = dialog)
    }

    private fun createFlashcard(question: String, answer: String) {
        setDialog(null)
        viewModelScope.launch {
            runCatching {
                flashcardRepository.insert(Flashcard(deckId = deckId, question = question, answer = answer))
            }.onFailure { e ->
                _effects.emit(FlashcardListEffect.ShowError(e.message ?: "Failed to create flashcard"))
            }
        }
    }

    private fun editFlashcard(flashcard: Flashcard, newQuestion: String, newAnswer: String) {
        setDialog(null)
        viewModelScope.launch {
            runCatching {
                flashcardRepository.update(flashcard.copy(question = newQuestion, answer = newAnswer))
            }.onFailure { e ->
                _effects.emit(FlashcardListEffect.ShowError(e.message ?: "Failed to update flashcard"))
            }
        }
    }

    private fun deleteFlashcard(flashcard: Flashcard) {
        setDialog(null)
        viewModelScope.launch {
            runCatching {
                flashcardRepository.delete(flashcard)
            }.onFailure { e ->
                _effects.emit(FlashcardListEffect.ShowError(e.message ?: "Failed to delete flashcard"))
            }
        }
    }

    private fun emit(effect: FlashcardListEffect) {
        viewModelScope.launch { _effects.emit(effect) }
    }

    companion object {
        fun factory(
            flashcardRepository: FlashcardRepository,
            deckRepository: DeckRepository,
            deckId: Long,
        ): ViewModelProvider.Factory = object : ViewModelProvider.Factory {
            @Suppress("UNCHECKED_CAST")
            override fun <T : ViewModel> create(modelClass: Class<T>): T =
                FlashcardListViewModel(deckId, flashcardRepository, deckRepository) as T
        }
    }
}
