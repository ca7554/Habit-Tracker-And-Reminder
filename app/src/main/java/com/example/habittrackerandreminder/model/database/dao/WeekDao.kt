package com.example.habittrackerandreminder.model.database.dao

import androidx.lifecycle.LiveData
import androidx.room.Dao
import androidx.room.Delete
import androidx.room.Insert
import androidx.room.Query
import com.example.habittrackerandreminder.model.database.entity.Week

/**
 * WeekDao used to provide interface methods to access and modify Week table in local database
 */
@Dao
interface WeekDao {
    @Insert
    fun insertAll(vararg weeks: Week): List<Long>
    @Query("SELECT * FROM weeks WHERE id IN (:ids)")
    fun loadWeeks(ids: List<Int>): LiveData<Array<Week>>
    @Delete
    fun deleteWeeks(vararg weeks: Week)
}