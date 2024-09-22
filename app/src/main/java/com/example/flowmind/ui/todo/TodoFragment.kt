package com.example.flowmind.ui.todo

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.EditText
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.flowmind.R
import com.example.flowmind.adapter.TaskAdapter
import com.example.flowmind.model.TodoItem
import com.example.flowmind.utils.SharedPrefUtil
import android.app.TimePickerDialog
import android.app.AlarmManager
import android.app.PendingIntent
import android.content.Context
import android.content.Intent
import android.widget.Toast
import com.example.flowmind.utils.NotificationReceiver
import java.util.Calendar

class TodoFragment : Fragment() {

    private lateinit var taskAdapter: TaskAdapter
    private val tasks = mutableListOf<TodoItem>()

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        val view = inflater.inflate(R.layout.fragment_todo, container, false)

        val editTextTaskName = view.findViewById<EditText>(R.id.editTextTaskName)
        val editTextTaskDescription = view.findViewById<EditText>(R.id.editTextTaskDescription)
        val buttonAddTask = view.findViewById<Button>(R.id.buttonAddTask)
        val buttonClearTasks = view.findViewById<Button>(R.id.buttonClearTasks)
        val recyclerViewTasks = view.findViewById<RecyclerView>(R.id.recyclerViewTasks)
        val buttonSetTime = view.findViewById<Button>(R.id.buttonSetTime)

        var selectedTimeInMillis: Long = 0 // Variable to store selected time

        if (SharedPrefUtil.isNewDay(requireContext())) {
            tasks.clear()
            SharedPrefUtil.clearTasks(requireContext())
        } else {
            tasks.addAll(SharedPrefUtil.loadTasks(requireContext()))
        }

        recyclerViewTasks.layoutManager = LinearLayoutManager(context)
        taskAdapter = TaskAdapter(tasks) { position ->
            tasks.removeAt(position)
            taskAdapter.notifyItemRemoved(position) // Notify specific item removal
            SharedPrefUtil.saveTasks(requireContext(), tasks)
        }
        recyclerViewTasks.adapter = taskAdapter

        buttonAddTask.setOnClickListener {
            val taskName = editTextTaskName.text.toString()
            val taskDescription = editTextTaskDescription.text.toString()

            if (taskName.isNotEmpty()) {
                val task = TodoItem(taskName, taskDescription, selectedTimeInMillis) // Include time
                tasks.add(task)
                taskAdapter.notifyItemInserted(tasks.size - 1) // Notify specific item insertion

                // Save the updated task list to SharedPreferences
                SharedPrefUtil.saveTasks(requireContext(), tasks)

                // Schedule the notification
                scheduleNotification(task)

                // Clear input fields
                editTextTaskName.text.clear()
                editTextTaskDescription.text.clear()
            }
        }

        buttonClearTasks.setOnClickListener {
            val taskCount = tasks.size
            tasks.clear()
            taskAdapter.notifyItemRangeRemoved(0, taskCount) // Notify range removal
            SharedPrefUtil.clearTasks(requireContext())
        }

        buttonSetTime.setOnClickListener {
            val calendar = Calendar.getInstance()
            val hour = calendar.get(Calendar.HOUR_OF_DAY)
            val minute = calendar.get(Calendar.MINUTE)

            val timePicker = TimePickerDialog(requireContext(), { _, selectedHour, selectedMinute ->
                val selectedCalendar = Calendar.getInstance()
                selectedCalendar.set(Calendar.HOUR_OF_DAY, selectedHour)
                selectedCalendar.set(Calendar.MINUTE, selectedMinute)
                selectedCalendar.set(Calendar.SECOND, 0)

                selectedTimeInMillis = selectedCalendar.timeInMillis // Store selected time
            }, hour, minute, true)

            timePicker.show()
        }

        return view
    }

    private fun scheduleNotification(task: TodoItem) {
        val intent = Intent(requireContext(), NotificationReceiver::class.java)
        intent.putExtra("taskName", task.name)

        val pendingIntent = PendingIntent.getBroadcast(
            requireContext(),
            0,
            intent,
            PendingIntent.FLAG_UPDATE_CURRENT or PendingIntent.FLAG_IMMUTABLE
        )

        val alarmManager = requireContext().getSystemService(Context.ALARM_SERVICE) as AlarmManager

        // Check if we can schedule exact alarms
        if (alarmManager.canScheduleExactAlarms()) {
            try {
                alarmManager.setExact(AlarmManager.RTC_WAKEUP, task.timeInMillis, pendingIntent)
            } catch (e: SecurityException) {
                // Handle the exception, e.g., show a message to the user
                Toast.makeText(requireContext(), "Permission to schedule alarms is denied.", Toast.LENGTH_SHORT).show()
            }
        } else {
            // Handle case where exact alarms cannot be scheduled
            Toast.makeText(requireContext(), "This app cannot schedule exact alarms.", Toast.LENGTH_SHORT).show()
        }
    }

}
