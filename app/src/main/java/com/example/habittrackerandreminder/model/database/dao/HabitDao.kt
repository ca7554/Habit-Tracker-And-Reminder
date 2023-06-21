package com.example.habittrackerandreminder.model.database.dao

import androidx.lifecycle.LiveData
import androidx.room.*
import com.example.habittrackerandreminder.model.database.entity.Habit

/**
 * HabitDao used to provide interface methods to access and modify Habit table in local database
 */
@Dao
interface HabitDao {
    @Query("SELECT * FROM habits")
    fun getAllInLiveData(): LiveData<List<Habit>>
    @Query("SELECT id FROM habits")
    fun getAllIds(): List<Long>
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    fun insertAll(vararg habits: Habit): List<Long>
    @Query("SELECT * FROM habits WHERE id IN (:ids)")
    fun loadHabitsByIds(ids: List<Long>): List<Habit>
    @Delete
    fun deleteHabits(vararg habits: Habit)
}