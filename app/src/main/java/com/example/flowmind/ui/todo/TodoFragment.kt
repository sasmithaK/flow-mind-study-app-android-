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
import com.example.flowmind.databinding.FragmentTodoBinding
import com.example.flowmind.model.TodoItem

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
        val recyclerViewTasks = view.findViewById<RecyclerView>(R.id.recyclerViewTasks)

        // Set up RecyclerView
        recyclerViewTasks.layoutManager = LinearLayoutManager(context)
        taskAdapter = TaskAdapter(tasks) { position ->
            taskAdapter.removeTask(position)
        }
        recyclerViewTasks.adapter = taskAdapter

        // Handle adding tasks
        buttonAddTask.setOnClickListener {
            val taskName = editTextTaskName.text.toString()
            val taskDescription = editTextTaskDescription.text.toString()

            if (taskName.isNotEmpty()) {
                val task = TodoItem(taskName, taskDescription)
                tasks.add(task)
                taskAdapter.notifyItemInserted(tasks.size - 1)

                // Clear input fields
                editTextTaskName.text.clear()
                editTextTaskDescription.text.clear()
            }
        }

        return view
    }
}

