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
fun RegisterScreen(
    onRegistered: () -> Unit,
    onGoLogin: () -> Unit,
    vm: AuthViewModel = hiltViewModel()
) {
    val state by vm.state.collectAsStateWithLifecycle()
    var email by rememberSaveable { mutableStateOf("") }
    var password by rememberSaveable { mutableStateOf("") }
    var password2 by rememberSaveable { mutableStateOf("") }
    var localError by rememberSaveable { mutableStateOf<String?>(null) }

    LaunchedEffect(state.success) { if (state.success) onRegistered() }

    Column(
        modifier = Modifier.fillMaxSize().padding(24.dp),
        verticalArrangement = Arrangement.Center,
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Text("Регистрация", style = MaterialTheme.typography.headlineMedium)
        Spacer(Modifier.height(24.dp))
        OutlinedTextField(email, { email = it }, label = { Text("E-mail") },
            singleLine = true, modifier = Modifier.fillMaxWidth())
        Spacer(Modifier.height(12.dp))
        OutlinedTextField(password, { password = it }, label = { Text("Пароль") },
            singleLine = true, visualTransformation = PasswordVisualTransformation(),
            modifier = Modifier.fillMaxWidth())
        Spacer(Modifier.height(12.dp))
        OutlinedTextField(password2, { password2 = it }, label = { Text("Повторите пароль") },
            singleLine = true, visualTransformation = PasswordVisualTransformation(),
            modifier = Modifier.fillMaxWidth())
        (localError ?: state.error)?.let {
            Spacer(Modifier.height(8.dp))
            Text(it, color = MaterialTheme.colorScheme.error)
        }
        Spacer(Modifier.height(20.dp))
        Button(
            onClick = {
                if (password != password2) { localError = "Пароли не совпадают"; return@Button }
                localError = null
                vm.register(email, password)
            },
            enabled = !state.loading,
            modifier = Modifier.fillMaxWidth()
        ) {
            if (state.loading) CircularProgressIndicator(Modifier.size(20.dp))
            else Text("Создать аккаунт")
        }
        Spacer(Modifier.height(8.dp))
        TextButton(onClick = onGoLogin) { Text("Уже есть аккаунт? Войти") }
    }
}