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
import com.example.habittrackerandreminder.model.handler.AppManager

/**
 * HabitNotificationReceiver listens to system-wide broadcast intents
 */
class HabitNotificationReceiver: BroadcastReceiver() {
    /**
     * Triggered by each notification interval and creates and shows notification
     */
    override fun onReceive(context: Context?, intent: Intent?) {
        //Sets values from Habit values passed to Intent
        val title = intent?.getStringExtra("title") ?: "Empty"
        val description = intent?.getStringExtra("description") ?: "Empty"

        //Creates notification and sets values
        val builder = NotificationCompat.Builder(context!!, AppManager.CHANNEL_ID)
            .setSmallIcon(R.drawable.baseline_alarm_24)
            .setContentTitle(title)
            .setContentText(description)
            .setPriority(NotificationCompat.PRIORITY_DEFAULT)

        val notificationId = intent?.getIntExtra("notificationId", 0) ?: 0

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
    }
}