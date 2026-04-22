package cz.cvut.fel.kindlma7.flashcards

import android.app.Application

class FlashcardsApplication : Application() {
    lateinit var container: AppContainer
        private set

    override fun onCreate() {
        super.onCreate()
        container = AppContainer(this)
    }
}
