package com.example.habittrackerandreminder.model.handler

import androidx.lifecycle.LiveData
import androidx.room.Room
import com.example.habittrackerandreminder.model.database.AppDatabase
import com.example.habittrackerandreminder.model.database.entity.Day
import com.example.habittrackerandreminder.model.database.entity.Habit
import com.example.habittrackerandreminder.model.database.entity.MilitaryTime
import com.example.habittrackerandreminder.model.database.entity.Week

/**
 * DataHandler used to provide methods for complex database changes and handle data related logic
 */
class  DataHandler(){
    //App Database
    private val db = Room.databaseBuilder(
        AppManager.mainActivity,
        AppDatabase::class.java, "app-database"
    ).allowMainThreadQueries().build()

    /**
     * Loads all habits from database but does not fully set the entire object and just sets
     * id, title and description. (Shallow)
     * @return Habits in LiveData to be observed for changes
     */
    fun loadAllHabitsShallow(): LiveData<List<Habit>> {
        return db.habitDao().getAll()
    }

    /**
     * Loads Habit from database and fully sets the entire Habit object
     * @return Habits completely set with all needed objects initialized
     */
    fun loadHabitFullById(habitId: Long): Habit {
        val habit = db.habitDao().loadHabitsByIds(listOf(habitId))[0]
        val days = db.dayDao().loadDaysByWeekIds(arrayOf(habit.weekId!!))

        for (day in days){
            val times = db.militaryTimeDao().loadMilitaryTimesByDayIds(listOf(day.id!!))
            day.timesInDay = times.toTypedArray()
        }

        habit.week = Week(days.toTypedArray())
        habit.week.id = habit.weekId
        return habit
    }

    /**
     * Deletes Habit from database and all relational entities belonging to habit
     * @param habit Habit to be deleted
     */
    fun deleteHabitFromDatabase(habit: Habit){
        for(i in 0 until habit.week.days.size)
            db.militaryTimeDao().deleteMilitaryTimes(*habit.week.days[i].timesInDay)
        db.dayDao().deleteDays(*habit.week.days)
        db.weekDao().deleteWeeks(habit.week)
        db.habitDao().deleteHabits(habit)
    }

    /**
     * Adds Habit to database and all relational entities belonging to habit
     * @return Id that was auto generated for Habit that was saved to database
     */
    fun addHabit(habit: Habit): Long {
        val weekId = addWeek(habit.week)[0]

        habit.weekId = weekId

        val days = habit.week.days
        for(day in days)
            day.weekId = weekId

        val daysIds = addDays(days)
        for(i in 0 until 7) { //Sets day ids for all times in days then add times to database
            for (time in days[i].timesInDay)
                time.dayId = daysIds[i]
            addMilitaryTimes(days[i].timesInDay)
        }

        return db.habitDao().insertAll(habit)[0]
    }

    /**
     * Adds [week] to database (Shallow)
     * @param week to be added to database
     */
    private fun addWeek(week: Week): List<Long> {
        return db.weekDao().insertAll(week)
    }

    /**
     * Adds [days] to database (Shallow)
     * @param days to be added to database
     */
    private fun addDays(days: Array<Day>): List<Long> {
        return db.dayDao().insertAll(*days)
    }

    /**
     * Adds [militaryTimes] to database (Shallow)
     * @param militaryTimes to be added to database
     */
    private fun addMilitaryTimes(militaryTimes: Array<MilitaryTime>): List<Long> {
        return db.militaryTimeDao().insertAll(*militaryTimes)
    }
}