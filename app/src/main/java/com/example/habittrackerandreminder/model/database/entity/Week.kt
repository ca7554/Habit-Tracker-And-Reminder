package com.example.habittrackerandreminder.model.database.entity

import androidx.room.Entity
import androidx.room.Ignore
import androidx.room.PrimaryKey

/**
 * Week used for entity in Week table in local database
 */
@Entity(tableName = "weeks")
class Week() {
    @PrimaryKey
    var id: Long? = null //Id in Week table in local database
    @Ignore
    var days = Array(DAYS_IN_WEEK){ i -> Day(NAMES_OF_DAYS[i], arrayOf())} //Days in the week

    /**
     * Used to create a Week without having to provide database needed values
     * @param daysTimes Times to set for each day in the week
     */
    constructor(daysTimes: Array<Array<MilitaryTime>>): this() {
        //Array[] size must be equal to 7
        require(daysTimes.size == DAYS_IN_WEEK) {
            "Invalid array size of ${daysTimes.size} passed to Week constructor, must be equal to 7."
        }
        setTimesInDays(daysTimes)
    }

    /**
     * Used to create a Week without having to provide database needed values
     * @param days Days to set for the week
     */
    constructor(days: Array<Day>): this() {
        this.days = days
    }

    /**
     * Sets the times in each day without having to create a new Array
     * @param daysTimes MilitaryTime[][] Times in each day
     */
    private fun setTimesInDays(daysTimes: Array<Array<MilitaryTime>>){
        for(i in 0 until DAYS_IN_WEEK)
            days[i].timesInDay = daysTimes[i]
    }

    companion object{
        private val NAMES_OF_DAYS = arrayOf("Sunday", "Monday", "Tuesday", "Wednesday", "Thursday", "Friday", "Saturday")
        const val DAYS_IN_WEEK = 7
    }

    @Ignore
    override fun toString(): String {
        return "Week(days=${days.contentToString()})"
    }
}