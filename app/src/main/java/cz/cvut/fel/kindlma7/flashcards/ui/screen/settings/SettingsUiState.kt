package cz.cvut.fel.kindlma7.flashcards.ui.screen.settings

import cz.cvut.fel.kindlma7.flashcards.ui.theme.AppTheme

data class SettingsUiState(
    val notificationIntervalMinutes: Int = 1440,
    val appTheme: AppTheme = AppTheme.SYSTEM,
)
