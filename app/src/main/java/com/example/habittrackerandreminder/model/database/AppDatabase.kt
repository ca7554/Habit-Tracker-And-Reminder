package com.example.habittrackerandreminder.model.database

import androidx.room.Database
import androidx.room.RoomDatabase
import com.example.habittrackerandreminder.model.database.dao.DayDao
import com.example.habittrackerandreminder.model.database.dao.HabitDao
import com.example.habittrackerandreminder.model.database.dao.MilitaryTimeDao
import com.example.habittrackerandreminder.model.database.dao.WeekDao
import com.example.habittrackerandreminder.model.database.entity.Day
import com.example.habittrackerandreminder.model.database.entity.Habit
import com.example.habittrackerandreminder.model.database.entity.MilitaryTime
import com.example.habittrackerandreminder.model.database.entity.Week

/**
 * Used to access all Daos
 */
@Database(entities = [Habit::class, Week::class, Day::class, MilitaryTime::class], version = 1)
abstract class AppDatabase: RoomDatabase() {
    abstract fun habitDao(): HabitDao
    abstract fun weekDao(): WeekDao
    abstract fun dayDao(): DayDao
    abstract fun militaryTimeDao(): MilitaryTimeDao
}