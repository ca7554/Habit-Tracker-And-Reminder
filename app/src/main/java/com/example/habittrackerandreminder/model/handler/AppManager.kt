package com.example.habittrackerandreminder.model.handler

import android.app.NotificationChannel
import android.app.NotificationManager
import android.content.Context
import com.example.habittrackerandreminder.controller.MainActivity

/**
 * AppManager used as a global state for the entire app
 */
class AppManager {
    companion object{
        const val LOG_TAG = "HTR"
        const val CHANNEL_ID = "HTR_CHANNEL"
        private const val CHANNEL_NAME = "HTR_CHANNEL_NAME"
        private const val CHANNEL_DESCRIPTION = "HTR_CHANNEL_DESCRIPTION"

        lateinit var mainActivity: MainActivity //Used to provide easy access to MainActivity
        lateinit var dataHandler: DataHandler //Used to provide easy access to data methods

        /**
         * Initializes all needed handlers for basic functionality of app
         * @param mainActivity Activity to be set for AppManager
         */
        fun initializeBaseFunctionality(mainActivity: MainActivity){ //Todo: Make asynchronous
            this.mainActivity = mainActivity
            dataHandler = DataHandler()
            createNotificationChannel()
        }

        /**
         * Registers app's notification channel with the system needed for Android 8.0 and higher
         */
        private fun createNotificationChannel() {
            // Create the NotificationChannel
            val importance = NotificationManager.IMPORTANCE_DEFAULT
            val channel = NotificationChannel(CHANNEL_ID, CHANNEL_NAME, importance).apply {
                description = CHANNEL_DESCRIPTION
            }
            // Register the channel with the system
            val notificationManager: NotificationManager =
                mainActivity.getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager
            notificationManager.createNotificationChannel(channel)
        }
    }
}