package com.example.taskplanner.data.local

import android.content.Context
import androidx.datastore.preferences.core.*
import androidx.datastore.preferences.preferencesDataStore
import kotlinx.coroutines.flow.*

val Context.dataStore by preferencesDataStore("user_prefs")

class UserPreferences(private val context: Context) {
    companion object {
        private val TOKEN = stringPreferencesKey("token")
        private val EMAIL = stringPreferencesKey("email")
        private val HISTORY = stringPreferencesKey("search_history")
        private val DARK = booleanPreferencesKey("dark_theme")
    }

    val token: Flow<String?> = context.dataStore.data.map { it[TOKEN] }
    val email: Flow<String?> = context.dataStore.data.map { it[EMAIL] }
    val history: Flow<List<String>> = context.dataStore.data.map {
        it[HISTORY]?.split("|")?.filter { s -> s.isNotBlank() } ?: emptyList()
    }
    val darkTheme: Flow<Boolean> = context.dataStore.data.map { it[DARK] ?: false }

    suspend fun saveAuth(token: String, email: String) {
        context.dataStore.edit { it[TOKEN] = token; it[EMAIL] = email }
    }
    suspend fun clearAuth() {
        context.dataStore.edit { it.remove(TOKEN); it.remove(EMAIL) }
    }
    suspend fun saveHistory(items: List<String>) {
        context.dataStore.edit { it[HISTORY] = items.joinToString("|") }
    }
    suspend fun setDark(enabled: Boolean) {
        context.dataStore.edit { it[DARK] = enabled }
    }
}