package com.example.habittrackerandreminder.model.adapter

import android.app.Activity
import android.content.Intent
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import androidx.recyclerview.widget.RecyclerView
import com.example.habittrackerandreminder.R
import com.example.habittrackerandreminder.controller.HabitViewerActivity
import com.example.habittrackerandreminder.model.database.entity.Habit

/**
 * HabitRecyclerViewAdapter used to provide the recycler view with the logic to display Habit items
 */
class HabitRecyclerViewAdapter (private val activity: Activity) :
    RecyclerView.Adapter<HabitRecyclerViewAdapter.ViewHolder>() {
    private var dataSet: List<Habit> = emptyList() //Data for list to be displayed in Recycler View

    /**
     * ViewHolder for custom item for Recycler view
     */
    class ViewHolder(view: View) : RecyclerView.ViewHolder(view) {
        val habitButton: Button

        init {
            // Define click listener for the ViewHolder's View
            habitButton = view.findViewById(R.id.habitButton)
        }
    }

    /**
     * Creates ViewHolder
     */
    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val view = LayoutInflater.from(parent.context)
            .inflate(R.layout.recycler_habit_item, parent, false)

        return ViewHolder(view)
    }

    /**
     * Binds view holder and sets UI component states for item at position
     */
    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        holder.habitButton.text = dataSet[position].title
        holder.habitButton.setOnClickListener {
            val intent = Intent(activity, HabitViewerActivity::class.java)
            intent.putExtra("habitId", dataSet[position].id)
            activity.startActivity(intent)
            activity.overridePendingTransition(R.anim.slide_in_right, R.anim.stay)
        }
    }

    override fun getItemCount() = dataSet.size

    /**
     * Used to update recycler view automatically with [data]
     * @param data New data to set for recycler view to display
     */
    fun setData(data: List<Habit>){
        this.dataSet = data
        notifyDataSetChanged() //Todo: Can be more efficient by using more specific change events
    }
}