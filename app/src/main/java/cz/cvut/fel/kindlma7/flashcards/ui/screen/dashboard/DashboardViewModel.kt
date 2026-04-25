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
import kotlinx.coroutines.flow.catch
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.launch
import java.util.Calendar

class DashboardViewModel(
    private val flashcardRepository: FlashcardRepository,
    private val reviewRecordRepository: ReviewRecordRepository,
) : ViewModel() {

    private val _uiState = MutableStateFlow<DashboardUiState>(DashboardUiState.Loading)
    val uiState: StateFlow<DashboardUiState> = _uiState.asStateFlow()

    private val _effects = MutableSharedFlow<DashboardEffect>()
    val effects: SharedFlow<DashboardEffect> = _effects.asSharedFlow()

    private val monthStart = Calendar.getInstance().apply {
        set(Calendar.DAY_OF_MONTH, 1)
        set(Calendar.HOUR_OF_DAY, 0)
        set(Calendar.MINUTE, 0)
        set(Calendar.SECOND, 0)
        set(Calendar.MILLISECOND, 0)
    }.timeInMillis

    init {
        viewModelScope.launch {
            combine(
                flashcardRepository.getAllDueCount(),
                reviewRecordRepository.countAllSince(monthStart),
                reviewRecordRepository.countAll(),
                reviewRecordRepository.getTop3ByRetention(),
            ) { dueCount, thisMonth, lifetime, topDecks ->
                DashboardUiState.Content(dueCount, thisMonth, lifetime, topDecks) as DashboardUiState
            }
                .catch { e -> emit(DashboardUiState.Error(e.message ?: "Failed to load dashboard")) }
                .collect { _uiState.value = it }
        }
    }

    fun onEvent(event: DashboardEvent) {
        when (event) {
            DashboardEvent.ReviewAll -> viewModelScope.launch {
                _effects.emit(DashboardEffect.NavigateToReviewAll)
            }
        }
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
