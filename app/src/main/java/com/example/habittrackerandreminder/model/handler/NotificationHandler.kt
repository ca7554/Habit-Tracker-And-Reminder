package com.example.habittrackerandreminder.model.handler

import android.app.NotificationChannel
import android.app.NotificationManager
import android.content.Context
import android.content.Intent
import com.example.habittrackerandreminder.model.database.entity.Habit
import com.example.habittrackerandreminder.model.database.entity.MilitaryTime
import com.example.habittrackerandreminder.model.notification.BroadcastNotification
import com.example.habittrackerandreminder.model.receiver.HabitNotificationReceiver
import java.util.*

/**
 * NotificationHandler used to provide methods for creating and modifying app notifications
 */
class NotificationHandler(context: Context) {
    init {
        createNotificationChannel(context)
    }

    /**
     * Registers app's notification channel with the system needed for Android 8.0 and higher
     */
    private fun createNotificationChannel(context: Context) {
        // Create the NotificationChannel
        val importance = NotificationManager.IMPORTANCE_DEFAULT
        val channel = NotificationChannel(CHANNEL_ID, CHANNEL_NAME, importance).apply {
            description = CHANNEL_DESCRIPTION
        }
        // Register the channel with the system
        val notificationManager: NotificationManager =
            context.getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager
        notificationManager.createNotificationChannel(channel)
    }

    /**
     * Creates a Habit notification to let the user be reminded of a [habit] based on habit
     * frequency and [habit] time
     * @param habit Habit to provide data for starting notification
     */
    fun startHabitNotification(habit: Habit, context: Context) {
        val habitNotification = BroadcastNotification(context,
            HabitNotificationReceiver::class.java, habit.id!!.toInt(), createHabitNotifyIntent(habit, context)
        )

        val day = habit.dayIndex
        val nextTimeReminder = habit.getNextTimeReminder()

        //Sets calendar time to be reminded in current day
        val calendar: Calendar = Calendar.getInstance()
        calendar.timeInMillis = System.currentTimeMillis()

        val currentTime = MilitaryTime(calendar.get(Calendar.HOUR_OF_DAY), calendar.get(Calendar.MINUTE))

        calendar.set(Calendar.HOUR_OF_DAY, nextTimeReminder.hour)
        calendar.set(Calendar.MINUTE, nextTimeReminder.minute)
        calendar.set(Calendar.SECOND, 0)

        //Prevents habit from starting immediately and adds week interval if needed
        if(calendar.get(Calendar.DAY_OF_WEEK) > day + 1 ||
            (calendar.get(Calendar.DAY_OF_WEEK) == day + 1 && currentTime.compareTo(nextTimeReminder) >= 0)) {
            calendar.set(Calendar.DAY_OF_WEEK, day + 1)
            calendar.timeInMillis += 604800000L //Week in milliseconds
        }

        habitNotification.set(calendar.timeInMillis)
    }

    /**
     * Creates an intent based off the [habit]
     * @param habit to evaluate to create Intent
     */
    private fun createHabitNotifyIntent(habit: Habit, context: Context): Intent {
        val intent = Intent(context, HabitNotificationReceiver::class.java)
        intent.putExtra("notificationId", habit.id!!.toInt())
        intent.putExtra("title", habit.title)
        intent.putExtra("description", habit.description)

        return intent
    }

    /**
     * Cancels Habit notification based on pendingIntent which must be exactly set when created
     */
    fun cancelHabitNotification(habitId: Int, context: Context){
        val notifyIntent = Intent(context, HabitNotificationReceiver::class.java)

        BroadcastNotification.cancelBroadcastNotification(context, habitId, notifyIntent)
    }

    companion object{
        const val CHANNEL_ID = "HTR_CHANNEL"
        private const val CHANNEL_NAME = "HTR_CHANNEL_NAME"
        private const val CHANNEL_DESCRIPTION = "HTR_CHANNEL_DESCRIPTION"
    }
}