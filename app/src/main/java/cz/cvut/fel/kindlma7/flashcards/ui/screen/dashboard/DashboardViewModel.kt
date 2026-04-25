package cz.cvut.fel.kindlma7.flashcards.ui.screen.dashboard

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.viewModelScope
import cz.cvut.fel.kindlma7.flashcards.data.repository.FlashcardRepository
import cz.cvut.fel.kindlma7.flashcards.data.repository.ReviewRecordRepository
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharedFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asSharedFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.launch
import java.util.Calendar

sealed interface DashboardEffect {
    data object NavigateToReviewAll : DashboardEffect
}
class DashboardViewModel(
    private val flashcardRepository: FlashcardRepository,
    private val reviewRecordRepository: ReviewRecordRepository,
) : ViewModel() {

    private val _uiState = MutableStateFlow(DashboardUiState())
    val uiState: StateFlow<DashboardUiState> = _uiState.asStateFlow()

    private val _effects = MutableSharedFlow<DashboardEffect>()
    val effects: SharedFlow<DashboardEffect> = _effects.asSharedFlow()

    init {
        val startOfMonth = startOfCurrentMonth()
        viewModelScope.launch {
            combine(
                flashcardRepository.observeAllDueCount(),
                reviewRecordRepository.observeCountSince(startOfMonth),
                reviewRecordRepository.observeCount(),
                reviewRecordRepository.observeTop3DecksByRetention(),
            ) { dueCount, thisMonth, lifetime, top3 ->
                DashboardUiState(
                    dueCount = dueCount,
                    reviewedThisMonth = thisMonth,
                    reviewedLifetime = lifetime,
                    top3Decks = top3,
                )
            }.collect { _uiState.value = it }
        }
    }

    fun onReviewAll() {
        viewModelScope.launch { _effects.emit(DashboardEffect.NavigateToReviewAll) }
    }

    private fun startOfCurrentMonth(): Long {
        val cal = Calendar.getInstance()
        cal.set(Calendar.DAY_OF_MONTH, 1)
        cal.set(Calendar.HOUR_OF_DAY, 0)
        cal.set(Calendar.MINUTE, 0)
        cal.set(Calendar.SECOND, 0)
        cal.set(Calendar.MILLISECOND, 0)
        return cal.timeInMillis
    }

    companion object {
        fun factory(
            flashcardRepository: FlashcardRepository,
            reviewRecordRepository: ReviewRecordRepository,
        ): ViewModelProvider.Factory = object : ViewModelProvider.Factory {
            @Suppress("UNCHECKED_CAST")
            override fun <T : ViewModel> create(modelClass: Class<T>): T =
                DashboardViewModel(flashcardRepository, reviewRecordRepository) as T
        }
    }
}
