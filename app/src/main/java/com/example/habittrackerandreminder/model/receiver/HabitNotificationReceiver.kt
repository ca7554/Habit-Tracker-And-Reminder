package com.example.habittrackerandreminder.model.receiver

import android.Manifest
import android.app.PendingIntent
import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.content.pm.PackageManager
import androidx.core.app.ActivityCompat
import androidx.core.app.NotificationCompat
import androidx.core.app.NotificationManagerCompat
import com.example.habittrackerandreminder.R
import com.example.habittrackerandreminder.controller.MainActivity
import com.example.habittrackerandreminder.model.handler.DataHandler
import com.example.habittrackerandreminder.model.handler.NotificationHandler

/**
 * HabitNotificationReceiver listens to system-wide broadcast intents
 */
class HabitNotificationReceiver: BroadcastReceiver() {
    /**
     * Triggered by each notification interval and creates and shows notification then sets next
     * notification based off next start time.
     */
    override fun onReceive(context: Context?, intent: Intent?) {
        val dataHandler = DataHandler(context!!)

        val notificationId = intent?.getIntExtra("notificationId", 0) ?: 0
        val habit = dataHandler.loadHabitsFullByIds(listOf(notificationId.toLong()))[0]
        val title = habit.title
        val description = habit.description

        //Creates notification and sets values
        val builder = NotificationCompat.Builder(context, NotificationHandler.CHANNEL_ID)
            .setSmallIcon(R.drawable.baseline_alarm_24)
            .setContentTitle(title)
            .setContentText(description)
            .setPriority(NotificationCompat.PRIORITY_DEFAULT)

        //Todo: Check Intent behavior when app is already open
        val notifyIntent = Intent(context, MainActivity::class.java) //Intent when notification pressed
        val pendingIntent = PendingIntent.getActivity(context, 0, notifyIntent,
            PendingIntent.FLAG_IMMUTABLE)
        builder.setContentIntent(pendingIntent) //Sets pending intent for notifyIntent

        //Builds notification
        val notificationCompat = builder.build()
        val managerCompat = NotificationManagerCompat.from(context)

        //Check permission
        if (ActivityCompat.checkSelfPermission(context, Manifest.permission.POST_NOTIFICATIONS)
            != PackageManager.PERMISSION_GRANTED) {
            return
        }

        managerCompat.notify(notificationId, notificationCompat) //Shows notification

        habit.setFutureWeekIndexes() //Sets next indexes for next interval to be triggered
        dataHandler.addHabitsShallow(arrayOf(habit)) //Saves habit to database

        val notificationHandler = NotificationHandler(context)
        notificationHandler.startHabitNotification(habit, context) //Set next notification based off of Habit's indexes
    }
}