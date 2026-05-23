package cz.cvut.fel.kindlma7.flashcards.notification

import android.content.Context
import androidx.work.ExistingPeriodicWorkPolicy
import androidx.work.ExistingWorkPolicy
import androidx.work.OneTimeWorkRequestBuilder
import androidx.work.PeriodicWorkRequestBuilder
import androidx.work.WorkManager
import java.util.concurrent.TimeUnit

class NotificationScheduler(private val context: Context) {

    companion object {
        const val WORK_TAG = "due_cards_notification_work"
        const val DEFAULT_INTERVAL_MINUTES = 1440L // 24 h
        /** WorkManager minimum for PeriodicWorkRequest. */
        private const val MIN_PERIODIC_MINUTES = 15L
    }

    /**
     * Schedules the due-cards notification.
     * - intervalMinutes >= 15 → repeating PeriodicWorkRequest
     * - intervalMinutes < 15  → single OneTimeWorkRequest with that delay (for testing)
     */
    fun schedule(intervalMinutes: Long = DEFAULT_INTERVAL_MINUTES) {
        val wm = WorkManager.getInstance(context)
        wm.cancelUniqueWork(WORK_TAG)

        if (intervalMinutes < MIN_PERIODIC_MINUTES) {
            val request = OneTimeWorkRequestBuilder<DueCardsNotificationWorker>()
                .setInitialDelay(intervalMinutes, TimeUnit.MINUTES)
                .build()
            wm.enqueueUniqueWork(WORK_TAG, ExistingWorkPolicy.REPLACE, request)
        } else {
            val request = PeriodicWorkRequestBuilder<DueCardsNotificationWorker>(
                intervalMinutes, TimeUnit.MINUTES,
            ).build()
            wm.enqueueUniquePeriodicWork(
                WORK_TAG,
                ExistingPeriodicWorkPolicy.CANCEL_AND_REENQUEUE,
                request,
            )
        }
    }

    fun cancel() {
        WorkManager.getInstance(context).cancelUniqueWork(WORK_TAG)
    }
}
