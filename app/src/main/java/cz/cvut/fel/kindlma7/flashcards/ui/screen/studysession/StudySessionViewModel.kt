package cz.cvut.fel.kindlma7.flashcards.ui.screen.studysession

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.viewModelScope
import cz.cvut.fel.kindlma7.flashcards.data.repository.DeckRepository
import cz.cvut.fel.kindlma7.flashcards.data.repository.FlashcardRepository
import cz.cvut.fel.kindlma7.flashcards.data.repository.ReviewRecordRepository
import cz.cvut.fel.kindlma7.flashcards.domain.ReviewRecord
import cz.cvut.fel.kindlma7.flashcards.domain.applySmReview
import cz.cvut.fel.kindlma7.flashcards.ui.component.ReviewRating
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharedFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asSharedFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.launch

sealed interface StudySessionEffect {
    data object NavigateBack : StudySessionEffect
    data class ShowError(val message: String) : StudySessionEffect
}

class StudySessionViewModel(
    private val deckId: Long,
    private val flashcardRepository: FlashcardRepository,
    private val reviewRecordRepository: ReviewRecordRepository,
    private val deckRepository: DeckRepository,
) : ViewModel() {

    private val _uiState = MutableStateFlow<StudySessionUiState>(StudySessionUiState.Loading)
    val uiState: StateFlow<StudySessionUiState> = _uiState.asStateFlow()

    private val _effects = MutableSharedFlow<StudySessionEffect>()
    val effects: SharedFlow<StudySessionEffect> = _effects.asSharedFlow()

    private var dueCards = emptyList<cz.cvut.fel.kindlma7.flashcards.domain.Flashcard>()
    private var currentIndex = 0
    private var deckName = ""

    init {
        viewModelScope.launch {
            val deck = deckRepository.getById(deckId)
            if (deck == null) {
                _uiState.value = StudySessionUiState.Error("Deck not found")
                return@launch
            }
            deckName = deck.name
            runCatching {
                flashcardRepository.getDueCards(deckId).first()
            }.onSuccess { cards ->
                dueCards = cards
                showCurrentCard()
            }.onFailure { e ->
                _uiState.value = StudySessionUiState.Error(e.message ?: "Failed to load cards")
            }
        }
    }

    fun onEvent(event: StudySessionEvent) {
        when (event) {
            is StudySessionEvent.SubmitRating -> submitRating(event.rating)
            is StudySessionEvent.NavigateBack -> emit(StudySessionEffect.NavigateBack)
        }
    }

    private fun showCurrentCard() {
        if (dueCards.isEmpty()) {
            _uiState.value = StudySessionUiState.Empty(deckName)
            return
        }
        if (currentIndex >= dueCards.size) {
            _uiState.value = StudySessionUiState.Complete(deckName, dueCards.size)
            return
        }
        _uiState.value = StudySessionUiState.Active(
            deckName = deckName,
            currentCard = dueCards[currentIndex],
            currentIndex = currentIndex,
            totalCards = dueCards.size,
        )
    }

    private fun submitRating(rating: ReviewRating) {
        val current = _uiState.value as? StudySessionUiState.Active ?: return
        val card = current.currentCard
        viewModelScope.launch {
            val updatedCard = applySmReview(card, rating.quality)
            runCatching {
                flashcardRepository.update(updatedCard)
                reviewRecordRepository.insert(ReviewRecord(flashcardId = card.id, quality = rating.quality))
            }.onFailure { e ->
                _effects.emit(StudySessionEffect.ShowError(e.message ?: "Failed to save review"))
            }
            currentIndex++
            showCurrentCard()
        }
    }

    private fun emit(effect: StudySessionEffect) {
        viewModelScope.launch { _effects.emit(effect) }
    }

    companion object {
        fun factory(
            flashcardRepository: FlashcardRepository,
            reviewRecordRepository: ReviewRecordRepository,
            deckRepository: DeckRepository,
            deckId: Long,
        ): ViewModelProvider.Factory = object : ViewModelProvider.Factory {
            @Suppress("UNCHECKED_CAST")
            override fun <T : ViewModel> create(modelClass: Class<T>): T =
                StudySessionViewModel(deckId, flashcardRepository, reviewRecordRepository, deckRepository) as T
        }
    }
}
