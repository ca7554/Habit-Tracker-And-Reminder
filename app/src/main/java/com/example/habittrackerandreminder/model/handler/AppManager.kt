package com.example.habittrackerandreminder.model.handler

import android.content.Context

/**
 * AppManager used as a global state for the entire app
 */
class AppManager {
    companion object {
        const val LOG_TAG = "HTR"

        lateinit var dataHandler: DataHandler //Used to provide easy access to data methods
        lateinit var notificationHandler: NotificationHandler //Used to provide easy access to notification methods

        /**
         * Initializes all needed handlers for basic functionality of app
         * @param context Context of application
         */
        fun initializeBaseFunctionality(context: Context){ //Todo: Make asynchronous
            dataHandler = DataHandler(context)
            notificationHandler = NotificationHandler(context)
        }
    }
}