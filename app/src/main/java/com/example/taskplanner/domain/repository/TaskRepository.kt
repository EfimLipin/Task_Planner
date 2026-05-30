package com.example.taskplanner.domain.repository

import com.example.taskplanner.domain.model.Task

interface TaskRepository {
    suspend fun list(query: String? = null): List<Task>
    suspend fun create(task: Task): Task
    suspend fun update(task: Task): Task
    suspend fun delete(id: Int)
}