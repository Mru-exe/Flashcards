package cz.cvut.fel.kindlma7.flashcards.ui.screen.settings

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.viewModelScope
import cz.cvut.fel.kindlma7.flashcards.data.preferences.UserPreferencesRepository
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch

class SettingsViewModel(
    private val userPreferencesRepository: UserPreferencesRepository,
) : ViewModel() {

    val uiState: StateFlow<SettingsUiState> = userPreferencesRepository.notificationIntervalMinutes
        .map { minutes -> SettingsUiState(notificationIntervalMinutes = minutes) }
        .stateIn(
            scope = viewModelScope,
            started = SharingStarted.WhileSubscribed(5_000),
            initialValue = SettingsUiState(),
        )

    fun setNotificationInterval(minutes: Int) {
        viewModelScope.launch {
            userPreferencesRepository.setNotificationIntervalMinutes(minutes)
        }
    }

    companion object {
        fun factory(userPreferencesRepository: UserPreferencesRepository): ViewModelProvider.Factory =
            object : ViewModelProvider.Factory {
                @Suppress("UNCHECKED_CAST")
                override fun <T : ViewModel> create(modelClass: Class<T>): T =
                    SettingsViewModel(userPreferencesRepository) as T
            }
    }
}
