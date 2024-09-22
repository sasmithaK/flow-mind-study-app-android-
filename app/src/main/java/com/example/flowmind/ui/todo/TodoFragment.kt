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
                val task = TodoItem(taskName, taskDescription)
                tasks.add(task)
                taskAdapter.notifyItemInserted(tasks.size - 1) // Notify specific item insertion
                SharedPrefUtil.saveTasks(requireContext(), tasks)
                SharedPrefUtil.saveCurrentDate(requireContext())
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

        return view
    }
}
