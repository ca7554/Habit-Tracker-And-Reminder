package com.example.habittrackerandreminder.model.database.entity

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.Ignore
import androidx.room.PrimaryKey

/**
 * Habit used for entity in Day table in local database
 * @param id Id used in Habit table in local database
 * @param weekId Id used to determine what week this Habit is using for times
 * @param title Title of Habit
 * @param description Description of Habit
 */
@Entity(tableName = "habits")
class Habit(@PrimaryKey(autoGenerate = true) var id: Long? = null, @ColumnInfo var weekId: Long? = null,
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
            } else
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
                    if(militaryTime == day.timesInDay[0] && timesFound == i)
                        timesFound++
                    else if(militaryTime == null) {
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