package cz.cvut.fel.kindlma7.flashcards

import android.app.Application
import cz.cvut.fel.kindlma7.flashcards.notification.NotificationHelper
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.SupervisorJob
import kotlinx.coroutines.launch

class FlashcardsApplication : Application() {
    private val applicationScope = CoroutineScope(SupervisorJob() + Dispatchers.IO)

    lateinit var container: AppContainer
        private set

    override fun onCreate() {
        super.onCreate()
        container = AppContainer(this)
        NotificationHelper.createChannel(this)
        applicationScope.launch {
            val intervalHours = container.userPreferencesRepository.getIntervalOnce()
            container.notificationScheduler.schedule(intervalHours.toLong())
        }
    }
}
