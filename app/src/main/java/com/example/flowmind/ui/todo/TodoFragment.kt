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
    private var selectedTimeInMillis: Long = 0 // Variable to store selected time

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

        if (SharedPrefUtil.isNewDay(requireContext())) {
            tasks.clear()
            SharedPrefUtil.clearTasks(requireContext())
        } else {
            tasks.addAll(SharedPrefUtil.loadTasks(requireContext()))
        }

        recyclerViewTasks.layoutManager = LinearLayoutManager(context)
        taskAdapter = TaskAdapter(tasks) { position ->
            tasks.removeAt(position)
            taskAdapter.notifyItemRemoved(position)
            SharedPrefUtil.saveTasks(requireContext(), tasks)
        }
        recyclerViewTasks.adapter = taskAdapter

        buttonAddTask.setOnClickListener {
            val taskName = editTextTaskName.text.toString()
            val taskDescription = editTextTaskDescription.text.toString()

            if (taskName.isNotEmpty()) {
                if (selectedTimeInMillis == 0L) {
                    Toast.makeText(requireContext(), "Please set a time for the task.", Toast.LENGTH_SHORT).show()
                    return@setOnClickListener
                }

                val task = TodoItem(taskName, taskDescription, selectedTimeInMillis)
                tasks.add(task)
                taskAdapter.notifyItemInserted(tasks.size - 1)

                // Save the updated task list to SharedPreferences
                SharedPrefUtil.saveTasks(requireContext(), tasks)

                // Schedule the notification
                scheduleNotification(task)

                // Clear input fields
                editTextTaskName.text.clear()
                editTextTaskDescription.text.clear()
                selectedTimeInMillis = 0L // Reset time after the task is added
            }
        }

        buttonClearTasks.setOnClickListener {
            val taskCount = tasks.size
            if (taskCount > 0) {
                tasks.clear()
                taskAdapter.notifyItemRangeRemoved(0, taskCount)
                SharedPrefUtil.clearTasks(requireContext())
            } else {
                Toast.makeText(requireContext(), "No tasks to clear", Toast.LENGTH_SHORT).show()
            }
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
            task.timeInMillis.toInt(), // Use a unique request code for each task
            intent,
            PendingIntent.FLAG_UPDATE_CURRENT or PendingIntent.FLAG_IMMUTABLE
        )

        val alarmManager = requireContext().getSystemService(Context.ALARM_SERVICE) as AlarmManager

        if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.S) {
            if (alarmManager.canScheduleExactAlarms()) {
                try {
                    alarmManager.setExact(AlarmManager.RTC_WAKEUP, task.timeInMillis, pendingIntent)
                } catch (e: SecurityException) {
                    Toast.makeText(requireContext(), "Permission to schedule alarms is denied.", Toast.LENGTH_SHORT).show()
                }
            } else {
                Toast.makeText(requireContext(), "This app cannot schedule exact alarms.", Toast.LENGTH_SHORT).show()
            }
        } else {
            // For older Android versions, just schedule the alarm
            alarmManager.setExact(AlarmManager.RTC_WAKEUP, task.timeInMillis, pendingIntent)
        }
    }

    // Save state when the screen rotates
    override fun onSaveInstanceState(outState: Bundle) {
        super.onSaveInstanceState(outState)
        outState.putLong("selectedTimeInMillis", selectedTimeInMillis)
    }

    // Restore state after configuration changes (like screen rotations)
    override fun onViewStateRestored(savedInstanceState: Bundle?) {
        super.onViewStateRestored(savedInstanceState)
        selectedTimeInMillis = savedInstanceState?.getLong("selectedTimeInMillis") ?: 0L
    }
}
