package com.example.taskplanner.presentation.auth

import androidx.compose.foundation.layout.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle

@Composable
fun LoginScreen(
    onLoggedIn: () -> Unit,
    onGoRegister: () -> Unit,
    vm: AuthViewModel = hiltViewModel()
) {
    val state by vm.state.collectAsStateWithLifecycle()
    var email by rememberSaveable { mutableStateOf("") }
    var password by rememberSaveable { mutableStateOf("") }

    LaunchedEffect(state.success) { if (state.success) onLoggedIn() }

    Column(
        modifier = Modifier.fillMaxSize().padding(24.dp),
        verticalArrangement = Arrangement.Center,
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Text("Вход", style = MaterialTheme.typography.headlineMedium)
        Spacer(Modifier.height(24.dp))
        OutlinedTextField(
            value = email, onValueChange = { email = it },
            label = { Text("E-mail") }, singleLine = true,
            modifier = Modifier.fillMaxWidth()
        )
        Spacer(Modifier.height(12.dp))
        OutlinedTextField(
            value = password, onValueChange = { password = it },
            label = { Text("Пароль") }, singleLine = true,
            visualTransformation = PasswordVisualTransformation(),
            modifier = Modifier.fillMaxWidth()
        )
        state.error?.let {
            Spacer(Modifier.height(8.dp))
            Text(it, color = MaterialTheme.colorScheme.error)
        }
        Spacer(Modifier.height(20.dp))
        Button(
            onClick = { vm.login(email, password) },
            enabled = !state.loading,
            modifier = Modifier.fillMaxWidth()
        ) {
            if (state.loading) CircularProgressIndicator(Modifier.size(20.dp))
            else Text("Войти")
        }
        Spacer(Modifier.height(8.dp))
        TextButton(onClick = onGoRegister) { Text("Нет аккаунта? Зарегистрироваться") }
    }
}