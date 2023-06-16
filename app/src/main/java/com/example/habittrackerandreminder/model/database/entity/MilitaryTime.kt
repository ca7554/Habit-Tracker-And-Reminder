package com.example.habittrackerandreminder.model.database.entity

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.PrimaryKey

/**
 * MilitaryTime used for entity in Military Time table in local database
 * @param id Id used in Military Time table in local database
 * @param dayId Id used to determine what day this MilitaryTime belongs to
 * @param hour Hour in military time
 * @param minute Minute of hour
 */
@Entity(tableName = "military_times")
class MilitaryTime(@PrimaryKey(autoGenerate = true) val id: Long? = null,
                   @ColumnInfo(name = "day_id") var dayId: Long? = null, @ColumnInfo var hour: Int,
                   @ColumnInfo var minute: Int) {

    /**
     * Used to create a MilitaryTime without having to provide database needed values
     * @param hour Hour in military time
     * @param minute Minute of hour
     */
    constructor(hour: Int, minute: Int): this(id = null, hour = hour, minute = minute){
        //Must be valid military time 00:00 to 23:59
        require(hour in 0..23 && minute in 0 .. 59) {
            "Invalid military time $hour:$minute passed to MilitaryTime constructor."
        }
    }

    /**
     * Determines if MilitaryTime is equal to another MilitaryTime based on hour and minute
     * @return Boolean represents equality
     */
    override fun equals(other: Any?): Boolean {
        if(this === other)
            return true
        if(other !is MilitaryTime)
            return false

        return this.hour == other.hour && this.minute == other.minute
    }

    override fun toString(): String {
        return "MilitaryTime(id=$id, hour=$hour, minute=$minute)"
    }
}