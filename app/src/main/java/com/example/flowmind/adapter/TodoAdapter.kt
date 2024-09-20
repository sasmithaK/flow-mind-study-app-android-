package com.example.flowmind.adapter

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.CheckBox
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.example.flowmind.R
import com.example.flowmind.model.TodoItem

class TodoAdapter(private val todoList: List<TodoItem>) :
    RecyclerView.Adapter<TodoAdapter.TodoViewHolder>() {

    inner class TodoViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        val titleTextView: TextView = itemView.findViewById(R.id.text_view_title)
        val descriptionTextView: TextView = itemView.findViewById(R.id.text_view_description)
        val completedCheckBox: CheckBox = itemView.findViewById(R.id.check_box_completed)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): TodoViewHolder {
        val itemView = LayoutInflater.from(parent.context)
            .inflate(R.layout.item_task, parent, false)
        return TodoViewHolder(itemView)
    }

    override fun onBindViewHolder(holder: TodoViewHolder, position: Int) {
        val todoItem = todoList[position]
        holder.titleTextView.text = todoItem.title
        holder.descriptionTextView.text = todoItem.description
        holder.completedCheckBox.isChecked = todoItem.isCompleted
    }

    override fun getItemCount() = todoList.size
}
