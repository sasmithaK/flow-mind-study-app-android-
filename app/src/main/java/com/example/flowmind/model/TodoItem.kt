package com.example.flowmind.model

data class TodoItem(
    val name: String,
    val description: String,
    val timeInMillis: Long,
    var isCompleted: Boolean = false
)

