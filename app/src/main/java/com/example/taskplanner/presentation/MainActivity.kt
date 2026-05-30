package com.example.taskplanner.presentation

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.runtime.*
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.navigation.compose.*
import com.example.taskplanner.data.local.UserPreferences
import com.example.taskplanner.presentation.auth.LoginScreen
import com.example.taskplanner.presentation.auth.RegisterScreen
import com.example.taskplanner.presentation.tasks.TaskListScreen
import com.example.taskplanner.presentation.theme.PlannerTheme
import dagger.hilt.android.AndroidEntryPoint
import javax.inject.Inject

@AndroidEntryPoint
class MainActivity : ComponentActivity() {
    @Inject lateinit var prefs: UserPreferences

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent {
            val dark by prefs.darkTheme.collectAsStateWithLifecycle(initialValue = false)
            val token by prefs.token.collectAsStateWithLifecycle(initialValue = null)
            PlannerTheme(darkTheme = dark) {
                val nav = rememberNavController()
                val start = if (token.isNullOrBlank()) "login" else "tasks"
                NavHost(navController = nav, startDestination = start) {
                    composable("login") {
                        LoginScreen(
                            onLoggedIn = { nav.navigate("tasks") { popUpTo(0) } },
                            onGoRegister = { nav.navigate("register") }
                        )
                    }
                    composable("register") {
                        RegisterScreen(
                            onRegistered = { nav.navigate("tasks") { popUpTo(0) } },
                            onGoLogin = { nav.popBackStack() }
                        )
                    }
                    composable("tasks") {
                        TaskListScreen(
                            isDark = dark,
                            onToggleTheme = { },
                            onLogout = { nav.navigate("login") { popUpTo(0) } }
                        )
                    }
                }
            }
        }
    }
}