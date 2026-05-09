package cz.cvut.fel.kindlma7.flashcards.notification

import android.content.Context
import androidx.work.ExistingPeriodicWorkPolicy
import androidx.work.PeriodicWorkRequestBuilder
import androidx.work.WorkManager
import java.util.concurrent.TimeUnit

class NotificationScheduler(private val context: Context) {

    companion object {
        const val WORK_TAG = "due_cards_notification_work"
        const val DEFAULT_INTERVAL_HOURS = 24L
    }

    /**
     * Schedules (or replaces) the periodic due-cards notification.
     * Phase 2 settings can call this with a user-selected interval.
     */
    fun schedule(intervalHours: Long = DEFAULT_INTERVAL_HOURS) {
        val request = PeriodicWorkRequestBuilder<DueCardsNotificationWorker>(
            intervalHours, TimeUnit.HOURS,
        ).build()

        WorkManager.getInstance(context).enqueueUniquePeriodicWork(
            WORK_TAG,
            ExistingPeriodicWorkPolicy.CANCEL_AND_REENQUEUE,
            request,
        )
    }

    fun cancel() {
        WorkManager.getInstance(context).cancelUniqueWork(WORK_TAG)
    }
}
