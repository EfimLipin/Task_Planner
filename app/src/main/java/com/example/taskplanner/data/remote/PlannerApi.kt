package com.example.taskplanner.data.remote

import retrofit2.http.*

interface PlannerApi {
    @POST("auth/register")
    suspend fun register(@Body req: AuthRequest): AuthResponse

    @POST("auth/login")
    suspend fun login(@Body req: AuthRequest): AuthResponse

    @GET("tasks")
    suspend fun getTasks(@Query("q") query: String? = null): List<TaskDto>

    @POST("tasks")
    suspend fun createTask(@Body dto: TaskDto): TaskDto

    @PUT("tasks")
    suspend fun updateTask(@Body dto: TaskDto): TaskDto

    @DELETE("tasks/{id}")
    suspend fun deleteTask(@Path("id") id: Int)
}