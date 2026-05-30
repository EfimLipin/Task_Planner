package com.example.taskplanner.domain.model

data class Task(
    val id: Int = 0,
    val title: String,
    val description: String = "",
    val dueDate: Long = 0L,
    val isDone: Boolean = false,
    val createdAt: Long = 0L
)