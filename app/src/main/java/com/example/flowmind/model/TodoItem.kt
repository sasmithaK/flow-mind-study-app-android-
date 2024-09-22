package com.example.flowmind.model

data class TodoItem(
    val name: String,
    val description: String,
    var isCompleted: Boolean = false
)

