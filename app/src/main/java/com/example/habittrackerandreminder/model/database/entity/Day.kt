package com.example.habittrackerandreminder.model.database.entity

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.Ignore
import androidx.room.PrimaryKey

/**
 * Day used for entity in Day table in local database
 * @param id Id used in Week table in local database
 * @param weekId Id used to determine what week this day belongs to
 * @param nameOfDay Name of day
 */
@Entity(tableName = "days")
class Day(@PrimaryKey(autoGenerate = true) val id: Long? = null,
          @ColumnInfo(name = "week_id") var weekId: Long? = null,
          @ColumnInfo(name = "name_of_day") var nameOfDay: String = "") {
    @Ignore
    var timesInDay = emptyArray<MilitaryTime>() //Times for this day in military time e.g. 23:59

    init {
        //Checks to make sure there are no more times then maximum minutes in a day
        require(timesInDay.size <= MAX_TIMES_IN_DAY) {
            "Invalid length of times ${timesInDay.size} passed to day, must be <= $MAX_TIMES_IN_DAY"
        }
    }

    /**
     * Used to create a day without having to provide database needed values
     * @param nameOfDay Name of day
     * @param timesInDay Array of Military Times for this day
     */
    constructor(nameOfDay: String, timesInDay: Array<MilitaryTime>): this(nameOfDay = nameOfDay) {
        this.timesInDay = timesInDay
    }

    companion object {
        @Ignore
        const val MAX_TIMES_IN_DAY = 1440
    }
}