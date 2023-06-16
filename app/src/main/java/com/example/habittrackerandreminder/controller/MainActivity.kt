package com.example.habittrackerandreminder.controller

import android.content.Intent
import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.habittrackerandreminder.R
import com.example.habittrackerandreminder.databinding.ActivityMainBinding
import com.example.habittrackerandreminder.model.adapter.HabitRecyclerViewAdapter
import com.example.habittrackerandreminder.model.handler.AppManager

//Todo: Evaluate overall design of code
//Todo: Extract Strings
/**
 * MainActivity launched at very start of app
 */
class MainActivity : AppCompatActivity() {
    private lateinit var binding: ActivityMainBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        //Initializes needed handlers for basic app functionality
        AppManager.initializeBaseFunctionality(this)

        //Sets content view and stores xml ui components with ids in binding object
        binding = ActivityMainBinding.inflate(layoutInflater)
        val view = binding.root
        setContentView(view)

        //Load habit recycler view
        val adapter = HabitRecyclerViewAdapter(this)

        binding.recyclerView.layoutManager = LinearLayoutManager(this)
        binding.recyclerView.adapter = adapter

        AppManager.dataHandler.loadAllHabitsShallow().observe(this) {
            adapter.setData(it)
        } //Allows recycler view to automatically update when changes are detected in local database

        //Set buttons
        binding.addButton.setOnClickListener {
            launchAddHabitActivity()
        }
    }

    /**
     * Launches AddHabitActivity for adding a new habit
     */
    private fun launchAddHabitActivity(){
        val intent = Intent(this@MainActivity, AddHabitActivity::class.java)
        startActivity(intent)
        overridePendingTransition(R.anim.slide_in_right, R.anim.stay) //Allows activity to slide in right
    }
}