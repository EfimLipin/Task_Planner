package com.example.taskplanner.data.repository

import com.example.taskplanner.data.remote.PlannerApi
import com.example.taskplanner.data.remote.TaskDto
import com.example.taskplanner.domain.model.Task
import com.example.taskplanner.domain.repository.TaskRepository
import javax.inject.Inject

class TaskRepositoryImpl @Inject constructor(private val api: PlannerApi) : TaskRepository {
    override suspend fun list(query: String?): List<Task> = api.getTasks(query).map { it.toDomain() }
    override suspend fun create(task: Task): Task = api.createTask(task.toDto()).toDomain()
    override suspend fun update(task: Task): Task = api.updateTask(task.toDto()).toDomain()
    override suspend fun delete(id: Int) = api.deleteTask(id)
}

private fun TaskDto.toDomain() = Task(id, title, description, dueDate, isDone, createdAt)
private fun Task.toDto() = TaskDto(id, title, description, dueDate, isDone, createdAt)