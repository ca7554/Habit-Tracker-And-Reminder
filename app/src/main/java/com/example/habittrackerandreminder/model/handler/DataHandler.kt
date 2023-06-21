package com.example.habittrackerandreminder.model.handler

import android.content.Context
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
class  DataHandler(context: Context){
    private var db : AppDatabase

    init {
        if(appDataBase == null)
            appDataBase = Room.databaseBuilder(
                context,
                AppDatabase::class.java, "app-database"
            ).allowMainThreadQueries().build()

        db = appDataBase!!
    }


    /**
     * Loads all habits from database in LiveData<> but does not fully set the entire object and just sets
     * id, title and description. (Shallow)
     * @return Habits in LiveData to be observed for changes
     */
    fun loadAllHabitsShallowInLiveData(): LiveData<List<Habit>> {
        return db.habitDao().getAllInLiveData()
    }

    /**
     * Loads all habits by in [habitsIds] and fully initializes all habits and there relations
     * @param habitsIds ids of the habits to be loaded fully
     * @return All habits loaded from given ids
     */
    fun loadHabitsFullByIds(habitsIds: List<Long>): List<Habit> {
        val habits = db.habitDao().loadHabitsByIds(habitsIds)
        val days = db.dayDao().loadDaysByWeekIds(List(habits.size){i -> habits[i].weekId!!})

        var i = 0
        while(i < habits.size){
            val habitWeek = Array(7) {
                val daysIndex = i * 7 + it
                val times = db.militaryTimeDao().loadMilitaryTimesByDaysIds(listOf(days[daysIndex].id!!))
                days[daysIndex].timesInDay = times.toTypedArray()
                days[daysIndex]
            }

            habits[i].week = Week(habitWeek)
            habits[i].week.id = habits[i].weekId
            i++
        }

        return habits
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
     * Adds habits to database (Shallow)
     * @param habits Habits to save to database
     */
    fun addHabitsShallow(habits: Array<Habit>){
        db.habitDao().insertAll(*habits)
    }

    /**
     * Loads all ids of the habits in the database
     * @return All ids of Habits in the database
     */
    fun loadAllHabitsIds(): List<Long> {
        return db.habitDao().getAllIds()
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

    companion object{
        //App Database
        private var appDataBase : AppDatabase? = null
    }
}