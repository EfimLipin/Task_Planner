package com.example.taskplanner.data.remote

import kotlinx.serialization.Serializable

@Serializable data class AuthRequest(val email: String, val password: String)
@Serializable data class AuthResponse(val token: String, val userId: Int, val email: String)
@Serializable data class TaskDto(
    val id: Int = 0,
    val title: String,
    val description: String = "",
    val dueDate: Long = 0,
    val isDone: Boolean = false,
    val createdAt: Long = 0L
)