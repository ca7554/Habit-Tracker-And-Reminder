package com.example.habittrackerandreminder.controller

import android.os.Bundle
import android.widget.ArrayAdapter
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.example.habittrackerandreminder.R
import com.example.habittrackerandreminder.databinding.ActivityAddHabitBinding
import com.example.habittrackerandreminder.model.database.entity.Habit
import com.example.habittrackerandreminder.model.handler.AppManager

/**
 * AddHabitActivity allows user to add new habit to local database and starts repeating notifications
 */
class AddHabitActivity : AppCompatActivity() {
    private lateinit var binding: ActivityAddHabitBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        //Sets content view and stores xml ui components with ids in binding object
        binding = ActivityAddHabitBinding.inflate(layoutInflater)
        val view = binding.root
        setContentView(view)

        //Loads frequency spinner
        val frequencies = resources.getStringArray(R.array.Frequency)
        val spinnerAdapter = ArrayAdapter(this, R.layout.spinner_frequency_item, frequencies)
        binding.frequencySpinner.adapter = spinnerAdapter

        //Set buttons
        binding.backButton.setOnClickListener {
            finish()
        }

        binding.checkButton.setOnClickListener {
            addHabit()
        }
    }

    /**
     * Dismisses activity with transition animation
     */
    override fun finish() {
        super.finish()
        overridePendingTransition(R.anim.stay, R.anim.slide_out_right)
    }

    /**
     * Gets entered data from UI and creates a new habit with data and then saves the new habit
     */
    private fun addHabit(){
        //Gets data to set for habit
        val title = binding.enterTitleEditText.text.toString()
        val description = binding.enterDescriptionEditText.text.toString()
        val frequency = (binding.frequencySpinner.getChildAt(0) as (TextView)).text.toString()

        if(title.isBlank()) {//Title must not be blank
            Toast.makeText(this, "Title cannot be empty", Toast.LENGTH_SHORT).show()
            return
        }

        //Creates habit and saves habit if week is non null
        Habit.frequencyStringToWeek(frequency)?.let {
            val habit = Habit(title, description, it)

            //Resets inputs
            binding.enterTitleEditText.setText("")
            binding.enterDescriptionEditText.setText("")

            //Gets auto generated id after habit saved to database
            val habitId = AppManager.dataHandler.addHabit(habit)
            habit.id = habitId //Set id to habit

            AppManager.notificationHandler.startHabitNotification(habit, this) //Starts new notification

            Toast.makeText(this, "Habit saved!", Toast.LENGTH_SHORT).show()
        } ?: Toast.makeText(this, "Error adding habit", Toast.LENGTH_SHORT).show()
    }
}