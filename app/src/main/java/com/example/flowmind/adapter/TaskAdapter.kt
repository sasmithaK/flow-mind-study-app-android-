package com.example.flowmind.adapter

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.CheckBox
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.example.flowmind.R
import com.example.flowmind.model.TodoItem

class TaskAdapter(
    private val tasks: MutableList<TodoItem>,
    private val onTaskChecked: (Int) -> Unit
) : RecyclerView.Adapter<TaskAdapter.TaskViewHolder>() {

    inner class TaskViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        val taskName: TextView = itemView.findViewById(R.id.taskName)
        val taskDescription: TextView = itemView.findViewById(R.id.taskDescription)
        val taskCheckbox: CheckBox = itemView.findViewById(R.id.taskCheckbox)

        fun bind(task: TodoItem, position: Int) {
            taskName.text = task.name
            taskDescription.text = task.description
            taskCheckbox.isChecked = task.isCompleted
            taskCheckbox.setOnCheckedChangeListener { _, isChecked ->
                task.isCompleted = isChecked
                if (isChecked) {
                    onTaskChecked(position)
                }
            }
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): TaskViewHolder {
        val view = LayoutInflater.from(parent.context).inflate(R.layout.item_task, parent, false)
        return TaskViewHolder(view)
    }

    override fun onBindViewHolder(holder: TaskViewHolder, position: Int) {
        holder.bind(tasks[position], position)
    }

    override fun getItemCount(): Int = tasks.size

    fun removeTask(position: Int) {
        tasks.removeAt(position)
        notifyItemRemoved(position)
    }
}
