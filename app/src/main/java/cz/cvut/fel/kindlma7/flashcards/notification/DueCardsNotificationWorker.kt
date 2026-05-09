package cz.cvut.fel.kindlma7.flashcards.notification

import android.content.Context
import androidx.work.CoroutineWorker
import androidx.work.WorkerParameters
import cz.cvut.fel.kindlma7.flashcards.FlashcardsApplication

class DueCardsNotificationWorker(
    appContext: Context,
    params: WorkerParameters,
) : CoroutineWorker(appContext, params) {

    override suspend fun doWork(): Result {
        val app = applicationContext as FlashcardsApplication
        val dueCount = app.container.flashcardRepository.getAllDueCountOnce()

        if (dueCount > 0) {
            NotificationHelper.showDueCardsNotification(applicationContext, dueCount)
        }

        return Result.success()
    }
}
