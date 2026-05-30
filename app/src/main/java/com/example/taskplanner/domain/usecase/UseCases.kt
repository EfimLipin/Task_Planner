package com.example.taskplanner.domain.usecase

import com.example.taskplanner.domain.model.Task
import com.example.taskplanner.domain.repository.AuthRepository
import com.example.taskplanner.domain.repository.TaskRepository
import javax.inject.Inject

class LoginUseCase @Inject constructor(private val repo: AuthRepository) {
    suspend operator fun invoke(email: String, password: String) = repo.login(email, password)
}
class RegisterUseCase @Inject constructor(private val repo: AuthRepository) {
    suspend operator fun invoke(email: String, password: String) = repo.register(email, password)
}
class LogoutUseCase @Inject constructor(private val repo: AuthRepository) {
    suspend operator fun invoke() = repo.logout()
}
class GetTasksUseCase @Inject constructor(private val repo: TaskRepository) {
    suspend operator fun invoke(q: String? = null) = repo.list(q)
}
class CreateTaskUseCase @Inject constructor(private val repo: TaskRepository) {
    suspend operator fun invoke(t: Task) = repo.create(t)
}
class UpdateTaskUseCase @Inject constructor(private val repo: TaskRepository) {
    suspend operator fun invoke(t: Task) = repo.update(t)
}
class DeleteTaskUseCase @Inject constructor(private val repo: TaskRepository) {
    suspend operator fun invoke(id: Int) = repo.delete(id)
}