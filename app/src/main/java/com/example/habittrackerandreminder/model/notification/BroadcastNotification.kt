package com.example.habittrackerandreminder.model.notification

import android.app.AlarmManager
import android.app.PendingIntent
import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent

/**
 * BroadcastNotification is used to model what is needed to start a broadcast notification
 * @param context Context of application
 * @param broadcastClass BroadcastReceiver class that listens for and handles the notification
 * @param notificationId Id to set for the notification
 * @param pendingIntentFlags Flags used to determine how to handle overwriting pending intents
 */
class BroadcastNotification(val context: Context,
                            broadcastClass: Class<out BroadcastReceiver>,
                            notificationId: Int,
                            notifyIntent: Intent = Intent(context, broadcastClass),
                            pendingIntentFlags: Int = DEFAULT_PENDING_INTENT_FLAGS,
) {
    private var pendingIntent: PendingIntent

    init {
        pendingIntent = PendingIntent.getBroadcast(
            context,
            notificationId,//id for pending intent must be globally unique
            notifyIntent, pendingIntentFlags)
    }

    /**
     * Sets a notification to be received by a BroadcastReceiver at [startTime]
     * @param startTime Time in milliseconds for the notification to start
     */
    fun set(startTime: Long) {
        val alarmManager = context.getSystemService(Context.ALARM_SERVICE) as AlarmManager
        alarmManager.set(AlarmManager.RTC_WAKEUP, startTime, pendingIntent)
    }

    companion object {
        private const val DEFAULT_PENDING_INTENT_FLAGS = PendingIntent.FLAG_IMMUTABLE or PendingIntent.FLAG_UPDATE_CURRENT

        /**
         * Cancels BroadcastNotification based off id, intent, and flags
         * @param context Context of app
         * @param notificationId Id of notification
         * @param notifyIntent Intent of notification
         * @param flags Flags set for notification's pending intent
         */
        fun cancelBroadcastNotification(context: Context, notificationId: Int, notifyIntent: Intent,
                                        flags: Int = DEFAULT_PENDING_INTENT_FLAGS){
            val pendingIntent = PendingIntent.getBroadcast(context,
                notificationId, notifyIntent, flags)

            val alarmManager = context.getSystemService(Context.ALARM_SERVICE) as AlarmManager
            alarmManager.cancel(pendingIntent)
        }
    }
}