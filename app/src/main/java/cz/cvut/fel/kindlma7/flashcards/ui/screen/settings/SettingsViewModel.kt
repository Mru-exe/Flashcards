package cz.cvut.fel.kindlma7.flashcards.ui.screen.settings

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.viewModelScope
import cz.cvut.fel.kindlma7.flashcards.data.preferences.UserPreferencesRepository
import cz.cvut.fel.kindlma7.flashcards.ui.theme.AppTheme
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch

class SettingsViewModel(
    private val userPreferencesRepository: UserPreferencesRepository,
) : ViewModel() {

    val uiState: StateFlow<SettingsUiState> = combine(
        userPreferencesRepository.notificationIntervalMinutes,
        userPreferencesRepository.appTheme,
    ) { minutes, theme ->
        SettingsUiState(notificationIntervalMinutes = minutes, appTheme = theme)
    }.stateIn(
        scope = viewModelScope,
        started = SharingStarted.WhileSubscribed(5_000),
        initialValue = SettingsUiState(),
    )

    fun setNotificationInterval(minutes: Int) {
        viewModelScope.launch {
            userPreferencesRepository.setNotificationIntervalMinutes(minutes)
        }
    }

    fun setAppTheme(theme: AppTheme) {
        viewModelScope.launch {
            userPreferencesRepository.setAppTheme(theme)
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
