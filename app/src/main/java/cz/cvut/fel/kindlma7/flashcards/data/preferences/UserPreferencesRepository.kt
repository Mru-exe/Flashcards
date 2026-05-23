package cz.cvut.fel.kindlma7.flashcards.data.preferences

import android.content.Context
import androidx.datastore.core.DataStore
import androidx.datastore.preferences.core.Preferences
import androidx.datastore.preferences.core.edit
import androidx.datastore.preferences.core.intPreferencesKey
import androidx.datastore.preferences.preferencesDataStore
import cz.cvut.fel.kindlma7.flashcards.notification.NotificationScheduler
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
        const val DEFAULT_INTERVAL_MINUTES = 1440 // 24 h
    }

    val notificationIntervalMinutes: Flow<Int> = context.dataStore.data
        .map { prefs -> prefs[KEY_NOTIFICATION_INTERVAL] ?: DEFAULT_INTERVAL_MINUTES }

    suspend fun getIntervalOnce(): Int =
        notificationIntervalMinutes.first()

    suspend fun setNotificationIntervalMinutes(minutes: Int) {
        context.dataStore.edit { prefs ->
            prefs[KEY_NOTIFICATION_INTERVAL] = minutes
        }
        notificationScheduler.schedule(minutes.toLong())
    }
}
