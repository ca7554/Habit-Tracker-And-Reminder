package com.example.habittrackerandreminder.controller

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.rounded.ArrowBack
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.habittrackerandreminder.R
import com.example.habittrackerandreminder.controller.ui.theme.HabitTrackerAndReminderTheme
import com.example.habittrackerandreminder.model.database.entity.Habit
import com.example.habittrackerandreminder.model.database.entity.Week
import com.example.habittrackerandreminder.model.handler.AppManager
import com.example.habittrackerandreminder.model.handler.NotificationHandler

/**
 * HabitViewerActivity views Habit that was saved and allows user to make changes
 */
class HabitViewerActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        val habitId = intent.getLongExtra("habitId", -1) //Gets habit id
        val habit = AppManager.dataHandler.loadHabitFullById(habitId) //Loads full initialized habit from database

        setContent {
            HabitTrackerAndReminderTheme {
                // A surface container using the 'background' color from the theme
                Surface(
                    modifier = Modifier.fillMaxSize(),
                    color = MaterialTheme.colors.background
                ){
                    Root(habit,this)
                }
            }
        }
    }

    override fun finish() {
        super.finish()
        overridePendingTransition(R.anim.stay, R.anim.slide_out_right)
    }
}

/**
 * Main UI uses [habit] to set needed UI components and uses [activity] to call activity need methods
 * @param habit Habit to use to set components
 * @param activity used to call activity needed methods
 */
@Composable
fun Root(habit: Habit, activity: HabitViewerActivity = HabitViewerActivity()) {
    Column(
        modifier = Modifier
            .padding(top = 24.dp)
            .padding(horizontal = 12.dp)
            .fillMaxWidth()
            .fillMaxHeight()
    ) {
        //Back button
        IconButton(onClick = {
            activity.finish()
        }) {
            Icon(
                imageVector = Icons.Rounded.ArrowBack,
                contentDescription = "Back",
            )
        }
        Column(modifier = Modifier
            .fillMaxWidth()
            .fillMaxHeight()
            .verticalScroll(rememberScrollState())
        ) {
            //Habit title
            Text(text = habit.title, modifier = Modifier
                .padding(top = 24.dp)
                .width(IntrinsicSize.Max), fontSize = 24.sp, maxLines = 1)
            //Habit description
            Text(text = habit.description, modifier = Modifier
                .padding(top = 24.dp)
                .width(IntrinsicSize.Max), fontSize = 24.sp, maxLines = 3)
            //Habit frequency
            Text(text = Habit.weekToFrequencyString(habit.week), modifier = Modifier
                .padding(top = 24.dp)
                .width(IntrinsicSize.Max), fontSize = 24.sp, maxLines = 1)
            //Deletes Habit from database
            Button(onClick =
            {
                NotificationHandler.cancelHabitNotification(habit) //Cancel habit notification
                AppManager.dataHandler.deleteHabitFromDatabase(habit) //Deletes habit and all its relations from database
                activity.finish()
            },
                modifier = Modifier
                    .padding(top = 24.dp)
                    .align(Alignment.End)
                , colors = ButtonDefaults.buttonColors(backgroundColor = Color.Red)) {
                Text(text = "Delete")
            }
        }
    }
}

@Preview(showBackground = true)
@Composable
fun DefaultPreview() {
    HabitTrackerAndReminderTheme {
        Root(Habit("Test Title", "Test Description", Week()))
    }
}