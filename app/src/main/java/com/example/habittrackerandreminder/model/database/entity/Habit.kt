package com.example.habittrackerandreminder.model.database.entity

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.Ignore
import androidx.room.PrimaryKey
import java.util.*

/**
 * Habit used for entity in Day table in local database
 * @param id Id used in Habit table in local database
 * @param weekId Id used to determine what week this Habit is using for times
 * @param title Title of Habit
 * @param description Description of Habit
 */
@Entity(tableName = "habits")
class Habit(@PrimaryKey(autoGenerate = true) var id: Long? = null, @ColumnInfo(name = "week_id") var weekId: Long? = null,
            @ColumnInfo(name = "day_index") var dayIndex: Int = 0,
            @ColumnInfo(name = "time_index") var timeIndex: Int = -1,
            @ColumnInfo var title: String, @ColumnInfo var description: String) {
    @Ignore
    var week = Week() //Week is used for holding the times a Habit should be done/notified

    /**
     * Used to create a week without having to provide database needed values
     * @param title Title of Habit to be set
     * @param description Description of Habit to be set
     * @param week Week of Habit to be set
     */
    constructor(title: String, description: String, week: Week) : this(title = title, description= description){
        this.week = week
        setFutureWeekIndexes()
    }

    /**
     * Gets next military time based on current time index saved for habit
     * @return Time a reminder should be triggered next
     */
    fun getNextTimeReminder(): MilitaryTime {
        return week.days[dayIndex].timesInDay[timeIndex]
    }

    /**
     * Sets future indexes for Habit's week based off of current system milliseconds to determine
     * when the next time a habit should be notified
     */
    fun setFutureWeekIndexes() {
        val calendar: Calendar = Calendar.getInstance()
        calendar.timeInMillis = System.currentTimeMillis()

        dayIndex = calendar.get(Calendar.DAY_OF_WEEK) - 1

        val currentTime = MilitaryTime(calendar.get(Calendar.HOUR_OF_DAY), calendar.get(Calendar.MINUTE))
        val timesInDay = week.days[dayIndex].timesInDay
        var newTimeIndex = -1
        var right = timesInDay.size - 1
        var left = 0

        while (left <= right) { //Searches using binary search for current military time in timesInDay
            newTimeIndex = left + (right - left) / 2 //newTimeIndex = mid

            if (timesInDay[newTimeIndex] == currentTime)
                break
            else if (timesInDay[newTimeIndex].compareTo(currentTime) < 0)
                left = newTimeIndex + 1
            else
                right = newTimeIndex - 1
        }

        //Checks if last known index is bigger than current time
        if (newTimeIndex != -1 && timesInDay[newTimeIndex].compareTo(currentTime) > 0) {
            timeIndex = newTimeIndex
            return
        }
        //Gets next bigger time if possible
        else if (newTimeIndex != -1 && newTimeIndex + 1 < timesInDay.size) {
            timeIndex = newTimeIndex + 1
            return
        }

        //Loops through all days looking for a non-empty day
        for(i in 0 until Week.DAYS_IN_WEEK) {
            dayIndex = (dayIndex + 1) % 7
            if(week.days[dayIndex].timesInDay.isNotEmpty())
                break
        }

        timeIndex = 0 //Time index must be zero
    }

    companion object{
        /**
         * Converts [frequency] String to a Week with times for intervals
         * @param frequency String name of frequency
         * @return Week with times for intervals based on frequency
         */
        @Ignore
        fun frequencyStringToWeek(frequency: String): Week? {
            return if(frequency.equals("daily", ignoreCase = true)){
                val timesInDays = Array(Week.DAYS_IN_WEEK){ arrayOf(MilitaryTime(12, 0)) }
                Week(timesInDays)
            }else if(frequency.equals("weekly", ignoreCase = true)){
                val timesInDays = Array(Week.DAYS_IN_WEEK){ i -> if(i == 0) arrayOf(MilitaryTime(12, 0)) else arrayOf() }
                Week(timesInDays)
            }else if(frequency.equals("minute", ignoreCase = true)){
                Week(Array(7) {
                    Array(1440){ i -> MilitaryTime(i / 60, i % 60) }
                })
            }else
                null
        }

        /**
         * Converts [week] with times for intervals to a String frequency and sets [militaryTimeToSet]
         * with the time found if not a Custom frequency
         * @param week Week to be evaluated for frequency
         * @param militaryTimeToSet Military Time found if frequency is not Custom
         * @return String name of frequency
         */
        fun weekToFrequencyString(week: Week, militaryTimeToSet: MilitaryTime? = null): String {
            var timesFound = 0
            var militaryTime: MilitaryTime? = null

            var i = 0
            while(i < week.days.size){ //Loop completes if either Weekly, Daily or None
                val day = week.days[i]
                if(day.timesInDay.isNotEmpty()){
                    if(day.timesInDay.size == 1 && militaryTime == day.timesInDay[0] && timesFound == i)
                        timesFound++
                    else if(day.timesInDay.size == 1 && militaryTime == null) {
                        militaryTime = day.timesInDay[0]
                        timesFound++
                    }else
                        return "Custom"
                }
                i++
            }

            //Sets military time found if not None frequency
            if(militaryTime != null && militaryTimeToSet != null) {
                militaryTimeToSet.hour = militaryTime.hour
                militaryTimeToSet.minute = militaryTime.minute
            }

            if(timesFound == 1)
                return "Weekly"
            else if(timesFound == 0)
                return "None"

            return "Daily"
        }
    }

    override fun toString(): String {
        return "Habit(id=$id, weekId=$weekId, title='$title', description='$description', week=$week)"
    }
}