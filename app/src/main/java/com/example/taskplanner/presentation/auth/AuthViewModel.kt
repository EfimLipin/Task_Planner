package com.example.taskplanner.presentation.auth

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.taskplanner.domain.usecase.LoginUseCase
import com.example.taskplanner.domain.usecase.RegisterUseCase
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.launch
import javax.inject.Inject

data class AuthUiState(
    val loading: Boolean = false,
    val error: String? = null,
    val success: Boolean = false
)

@HiltViewModel
class AuthViewModel @Inject constructor(
    private val loginUC: LoginUseCase,
    private val registerUC: RegisterUseCase
) : ViewModel() {
    val state = MutableStateFlow(AuthUiState())

    fun login(email: String, password: String) {
        if (!validate(email, password)) return
        state.value = AuthUiState(loading = true)
        viewModelScope.launch {
            runCatching { loginUC(email.trim(), password) }
                .onSuccess { state.value = AuthUiState(success = true) }
                .onFailure { state.value = AuthUiState(error = "Ошибка входа: ${it.localizedMessage}") }
        }
    }

    fun register(email: String, password: String) {
        if (!validate(email, password)) return
        state.value = AuthUiState(loading = true)
        viewModelScope.launch {
            runCatching { registerUC(email.trim(), password) }
                .onSuccess { state.value = AuthUiState(success = true) }
                .onFailure { state.value = AuthUiState(error = "Ошибка регистрации: ${it.localizedMessage}") }
        }
    }

    private fun validate(email: String, password: String): Boolean {
        if (!email.contains("@") || email.length < 5) {
            state.value = AuthUiState(error = "Введите корректный e-mail"); return false
        }
        if (password.length < 6) {
            state.value = AuthUiState(error = "Пароль должен быть не короче 6 символов"); return false
        }
        return true
    }
}