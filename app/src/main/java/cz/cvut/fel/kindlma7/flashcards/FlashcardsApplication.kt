package cz.cvut.fel.kindlma7.flashcards

import android.app.Application
import cz.cvut.fel.kindlma7.flashcards.notification.NotificationHelper

class FlashcardsApplication : Application() {
    lateinit var container: AppContainer
        private set

    override fun onCreate() {
        super.onCreate()
        container = AppContainer(this)
        NotificationHelper.createChannel(this)
        container.notificationScheduler.schedule()
    }
}
