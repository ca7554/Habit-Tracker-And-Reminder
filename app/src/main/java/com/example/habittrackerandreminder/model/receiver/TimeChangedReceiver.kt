package com.example.habittrackerandreminder.model.receiver

import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import com.example.habittrackerandreminder.model.handler.DataHandler
import com.example.habittrackerandreminder.model.handler.NotificationHandler

/**
 * Listens for date changes to set all habits alarms again correctly
 */
class TimeChangedReceiver: BroadcastReceiver() {
    override fun onReceive(context: Context?, intent: Intent) {
        if ("android.intent.action.TIME_SET" == intent.action ||
            "android.intent.action.TIMEZONE_CHANGED" == intent.action) {
            val dataHandler = DataHandler(context!!)
            val notificationHandler = NotificationHandler(context)

            val habitsIds = dataHandler.loadAllHabitsIds()
            val habitsFull = dataHandler.loadHabitsFullByIds(habitsIds)

            for(habit in habitsFull) {
                habit.setFutureWeekIndexes()
                notificationHandler.startHabitNotification(habit, context)
            }

            dataHandler.addHabitsShallow(habitsFull.toTypedArray())
        }
    }
}