package cz.cvut.fel.kindlma7.flashcards.data.preferences

import android.content.Context
import androidx.datastore.core.DataStore
import androidx.datastore.preferences.core.Preferences
import androidx.datastore.preferences.core.edit
import androidx.datastore.preferences.core.intPreferencesKey
import androidx.datastore.preferences.core.stringPreferencesKey
import androidx.datastore.preferences.preferencesDataStore
import cz.cvut.fel.kindlma7.flashcards.notification.NotificationScheduler
import cz.cvut.fel.kindlma7.flashcards.ui.theme.AppTheme
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.flow.map

private val Context.dataStore: DataStore<Preferences> by preferencesDataStore(name = "user_prefs")

class UserPreferencesRepository(
    private val context: Context,
    private val notificationScheduler: NotificationScheduler,
) {
    companion object {
        val KEY_NOTIFICATION_INTERVAL = intPreferencesKey("notification_interval_minutes")
        val KEY_APP_THEME = stringPreferencesKey("app_theme")
        const val DEFAULT_INTERVAL_MINUTES = 1440 // 24 h
    }

    val notificationIntervalMinutes: Flow<Int> = context.dataStore.data
        .map { prefs -> prefs[KEY_NOTIFICATION_INTERVAL] ?: DEFAULT_INTERVAL_MINUTES }

    val appTheme: Flow<AppTheme> = context.dataStore.data
        .map { prefs ->
            prefs[KEY_APP_THEME]?.let { runCatching { AppTheme.valueOf(it) }.getOrNull() }
                ?: AppTheme.SYSTEM
        }

    suspend fun getIntervalOnce(): Int =
        notificationIntervalMinutes.first()

    suspend fun setNotificationIntervalMinutes(minutes: Int) {
        context.dataStore.edit { prefs ->
            prefs[KEY_NOTIFICATION_INTERVAL] = minutes
        }
        notificationScheduler.schedule(minutes.toLong())
    }

    suspend fun setAppTheme(theme: AppTheme) {
        context.dataStore.edit { prefs ->
            prefs[KEY_APP_THEME] = theme.name
        }
    }
}
