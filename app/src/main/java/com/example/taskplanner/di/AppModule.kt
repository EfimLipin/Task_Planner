package com.example.taskplanner.di

import android.content.Context
import com.jakewharton.retrofit2.converter.kotlinx.serialization.asConverterFactory
import com.example.taskplanner.data.local.UserPreferences
import com.example.taskplanner.data.remote.PlannerApi
import com.example.taskplanner.data.repository.AuthRepositoryImpl
import com.example.taskplanner.data.repository.TaskRepositoryImpl
import com.example.taskplanner.domain.repository.AuthRepository
import com.example.taskplanner.domain.repository.TaskRepository
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.qualifiers.ApplicationContext
import dagger.hilt.components.SingletonComponent
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.runBlocking
import kotlinx.serialization.json.Json
import okhttp3.MediaType.Companion.toMediaType
import okhttp3.OkHttpClient
import okhttp3.logging.HttpLoggingInterceptor
import retrofit2.Retrofit
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
object AppModule {

    private const val BASE_URL = "http://10.0.2.2:8080/" // эмулятор -> localhost ПК

    @Provides @Singleton
    fun providePrefs(@ApplicationContext ctx: Context) = UserPreferences(ctx)

    @Provides @Singleton
    fun provideApi(prefs: UserPreferences): PlannerApi {
        val logging = HttpLoggingInterceptor().apply { level = HttpLoggingInterceptor.Level.BASIC }
        val client = OkHttpClient.Builder()
            .addInterceptor { chain ->
                val token = runBlocking { prefs.token.first() }
                val req = chain.request().newBuilder().apply {
                    token?.let { addHeader("Authorization", "Bearer $it") }
                }.build()
                chain.proceed(req)
            }
            .addInterceptor(logging)
            .build()

        val json = Json { ignoreUnknownKeys = true }
        return Retrofit.Builder()
            .baseUrl(BASE_URL)
            .client(client)
            .addConverterFactory(json.asConverterFactory("application/json".toMediaType()))
            .build()
            .create(PlannerApi::class.java)
    }

    @Provides @Singleton
    fun provideAuthRepo(api: PlannerApi, prefs: UserPreferences): AuthRepository =
        AuthRepositoryImpl(api, prefs)

    @Provides @Singleton
    fun provideTaskRepo(api: PlannerApi): TaskRepository = TaskRepositoryImpl(api)
}