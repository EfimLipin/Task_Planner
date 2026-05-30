package com.example.taskplanner.domain.repository

import kotlinx.coroutines.flow.Flow

interface AuthRepository {
    suspend fun login(email: String, password: String)
    suspend fun register(email: String, password: String)
    suspend fun logout()
    fun token(): Flow<String?>
    fun email(): Flow<String?>
}