package com.example.habittrackerandreminder.model.database.dao

import androidx.room.Dao
import androidx.room.Delete
import androidx.room.Insert
import androidx.room.Query
import com.example.habittrackerandreminder.model.database.entity.Day

/**
 * DayDao used to provide interface methods to access and modify Day table in local database
 */
@Dao
interface DayDao {
    @Insert
    fun insertAll(vararg days: Day): List<Long>
    @Query("SELECT * FROM days WHERE week_id IN (:weekIds)")
    fun loadDaysByWeekIds(weekIds: List<Long>): List<Day>
    @Delete
    fun deleteDays(vararg days: Day)
}