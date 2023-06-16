package com.example.habittrackerandreminder.model.handler

import android.app.AlarmManager
import android.app.PendingIntent
import android.content.Context
import android.content.Intent
import com.example.habittrackerandreminder.model.database.entity.Habit
import com.example.habittrackerandreminder.model.database.entity.MilitaryTime
import com.example.habittrackerandreminder.model.receiver.HabitNotificationReceiver
import java.util.*

/**
 * NotificationHandler used to provide methods for creating and modifying app notifications
 */
class NotificationHandler {
    companion object{
        /**
         * Creates a Habit notification to let the user be reminded of a [habit] based on habit
         * frequency and [habit] time
         * @param habit Habit to provide data for starting notification
         */
        fun startHabitNotification(habit: Habit) {
            val notifyIntent = createHabitNotifyIntent(habit)

            //Creates pending intent and must be correctly set exactly in order to cancel
            val pendingIntent = PendingIntent.getBroadcast(AppManager.mainActivity,
                habit.id!!.toInt(),//id for pending intent must be globally unique
                notifyIntent, PendingIntent.FLAG_UPDATE_CURRENT)

            //Passed to Habit.weekToFrequencyString to get time found
            val militaryTime = MilitaryTime(0, 0)
            val frequency = Habit.weekToFrequencyString(habit.week, militaryTime) //Frequency name as String

            //Sets calendar time to be reminded in current day
            val calendar: Calendar = Calendar.getInstance()
            calendar.timeInMillis = System.currentTimeMillis()
            calendar.set(Calendar.HOUR_OF_DAY, militaryTime.hour)
            calendar.set(Calendar.MINUTE, militaryTime.minute)
            calendar.set(Calendar.SECOND, 0)

            val interval: Long
            if(frequency.equals("Daily", ignoreCase = true)) { //Determines interval to be reminded
                interval = AlarmManager.INTERVAL_DAY
            }else if(frequency.equals("Weekly", ignoreCase = true)) {
                interval = 604800000L //Week in milliseconds
                calendar.firstDayOfWeek = 0
            }else
                return

            //Checks if interval should be added to calendar time to avoid a past reminder to immediately start
            if(calendar.timeInMillis < System.currentTimeMillis())
                calendar.timeInMillis += interval

            val alarmManager = AppManager.mainActivity.getSystemService(Context.ALARM_SERVICE) as AlarmManager


            //Todo: Test if notifications happen for all added habits and check correctness during device reboot
            //Uses alarm manager to set a notification for given interval
            alarmManager.setWindow(AlarmManager.RTC_WAKEUP, calendar.timeInMillis, interval, pendingIntent)

            //Used to test repeating faster. Comment top code and uncomment bottom code
//            alarmManager.setInexactRepeating(AlarmManager.RTC_WAKEUP, System.currentTimeMillis(), 1000 * 30, pendingIntent)
        }

        /**
         * Cancels [habit] notification based on pendingIntent which must be exactly set when created
         */
        fun cancelHabitNotification(habit: Habit){
            val notifyIntent = createHabitNotifyIntent(habit)

            //Must be exact to cancel notification
            val pendingIntent = PendingIntent.getBroadcast(AppManager.mainActivity,
                habit.id!!.toInt(), notifyIntent, PendingIntent.FLAG_UPDATE_CURRENT)

            val alarmManager = AppManager.mainActivity.getSystemService(Context.ALARM_SERVICE) as AlarmManager
            alarmManager.cancel(pendingIntent)
        }

        /**
         * Creates an intent based off the [habit]
         * @param habit to evaluate to create Intent
         */
        private fun createHabitNotifyIntent(habit: Habit): Intent {
            val intent = Intent(AppManager.mainActivity, HabitNotificationReceiver::class.java)
            intent.putExtra("notificationId", habit.id!!.toInt())
            intent.putExtra("title", habit.title)
            intent.putExtra("description", habit.description)

            return intent
        }
    }
}