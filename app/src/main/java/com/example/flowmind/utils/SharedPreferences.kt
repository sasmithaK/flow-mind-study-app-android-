package com.example.flowmind.utils

import android.content.Context
import android.content.SharedPreferences
import com.example.flowmind.model.TodoItem
import com.google.firebase.crashlytics.buildtools.reloc.com.google.common.reflect.TypeToken
import com.google.gson.Gson

object SharedPrefUtil {

    private const val PREFERENCES_NAME = "tasks"
    private const val TASK_LIST_KEY = "task_list"

    // Save task list to SharedPreferences
    fun saveTasks(context: Context, tasks: List<TodoItem>) {
        val sharedPreferences: SharedPreferences = context.getSharedPreferences(PREFERENCES_NAME, Context.MODE_PRIVATE)
        val editor = sharedPreferences.edit()

        // Convert list to JSON
        val gson = Gson()
        val json = gson.toJson(tasks)

        // Save JSON to SharedPreferences
        editor.putString(TASK_LIST_KEY, json)
        editor.apply()
    }

    // Load task list from SharedPreferences
    fun loadTasks(context: Context): MutableList<TodoItem> {
        val sharedPreferences: SharedPreferences = context.getSharedPreferences(PREFERENCES_NAME, Context.MODE_PRIVATE)
        val gson = Gson()
        val json = sharedPreferences.getString(TASK_LIST_KEY, null)

        return if (json == null) {
            mutableListOf()  // Return empty list if no data is found
        } else {
            // Convert JSON back to list of tasks
            val type = object : TypeToken<MutableList<TodoItem>>() {}.type
            gson.fromJson(json, type)
        }
    }

    // Clear all tasks from SharedPreferences
    fun clearTasks(context: Context) {
        val sharedPreferences: SharedPreferences = context.getSharedPreferences(PREFERENCES_NAME, Context.MODE_PRIVATE)
        val editor = sharedPreferences.edit()
        editor.clear()
        editor.apply()
    }


    fun saveCurrentDate(context: Context) {
        val sharedPreferences = context.getSharedPreferences("tasks", Context.MODE_PRIVATE)
        val editor = sharedPreferences.edit()
        val currentDate = System.currentTimeMillis() // Get current timestamp
        editor.putLong("last_saved_date", currentDate)
        editor.apply()
    }

    fun isNewDay(context: Context): Boolean {
        val sharedPreferences = context.getSharedPreferences("tasks", Context.MODE_PRIVATE)
        val lastSavedDate = sharedPreferences.getLong("last_saved_date", 0L)

        val currentDate = System.currentTimeMillis()
        val oneDayInMillis = 24 * 60 * 60 * 1000 // 24 hours in milliseconds

        // Check if more than 24 hours have passed
        return (currentDate - lastSavedDate) >= oneDayInMillis
    }


}