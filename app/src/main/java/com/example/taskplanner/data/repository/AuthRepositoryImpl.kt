package com.example.taskplanner.data.repository

import com.example.taskplanner.data.local.UserPreferences
import com.example.taskplanner.data.remote.AuthRequest
import com.example.taskplanner.data.remote.PlannerApi
import com.example.taskplanner.domain.repository.AuthRepository
import kotlinx.coroutines.flow.Flow
import javax.inject.Inject

class AuthRepositoryImpl @Inject constructor(
    private val api: PlannerApi,
    private val prefs: UserPreferences
) : AuthRepository {
    override suspend fun login(email: String, password: String) {
        val res = api.login(AuthRequest(email, password))
        prefs.saveAuth(res.token, res.email)
    }
    override suspend fun register(email: String, password: String) {
        val res = api.register(AuthRequest(email, password))
        prefs.saveAuth(res.token, res.email)
    }
    override suspend fun logout() = prefs.clearAuth()
    override fun token(): Flow<String?> = prefs.token
    override fun email(): Flow<String?> = prefs.email
}