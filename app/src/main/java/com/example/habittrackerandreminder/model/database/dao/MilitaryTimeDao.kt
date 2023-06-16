package com.example.habittrackerandreminder.model.database.dao
import androidx.room.Dao
import androidx.room.Delete
import androidx.room.Insert
import androidx.room.Query
import com.example.habittrackerandreminder.model.database.entity.MilitaryTime

/**
 * MilitaryTimeDao used to provide interface methods to access and modify MilitaryTime table in local database
 */
@Dao
interface MilitaryTimeDao {
    @Insert
    fun insertAll(vararg militaryTimes: MilitaryTime): List<Long>
    @Query("SELECT * FROM military_times WHERE day_id IN (:dayIds)")
    fun loadMilitaryTimesByDayIds(dayIds: List<Long>): List<MilitaryTime>
    @Delete
    fun deleteMilitaryTimes(vararg times: MilitaryTime)
}