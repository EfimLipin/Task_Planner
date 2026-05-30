package com.example.taskplanner.presentation.tasks

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.taskplanner.data.local.UserPreferences
import com.example.taskplanner.domain.model.Task
import com.example.taskplanner.domain.usecase.*
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.Job
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.*
import kotlinx.coroutines.launch
import javax.inject.Inject

enum class ListStatus { IDLE, LOADING, SUCCESS, EMPTY, ERROR }

data class TaskUiState(
    val query: String = "",
    val tasks: List<Task> = emptyList(),
    val history: List<String> = emptyList(),
    val status: ListStatus = ListStatus.IDLE,
    val showHistory: Boolean = false,
    val lastQuery: String? = null
)

@HiltViewModel
class TaskViewModel @Inject constructor(
    private val getTasks: GetTasksUseCase,
    private val createTask: CreateTaskUseCase,
    private val updateTask: UpdateTaskUseCase,
    private val deleteTask: DeleteTaskUseCase,
    private val logoutUC: LogoutUseCase,
    private val prefs: UserPreferences
) : ViewModel() {

    private val _state = MutableStateFlow(TaskUiState())
    val state: StateFlow<TaskUiState> = _state.asStateFlow()

    private var searchJob: Job? = null
    private var isSearchFocused = false

    init {
        viewModelScope.launch {
            prefs.history.collect { h -> _state.update { it.copy(history = h) } }
        }
        loadAll()
    }

    private fun updateShowHistory() {
        val currentState = _state.value
        val shouldShow = isSearchFocused && currentState.query.isEmpty() && currentState.history.isNotEmpty()
        if (currentState.showHistory != shouldShow) {
            _state.update { it.copy(showHistory = shouldShow) }
        }
    }

    fun onQueryChange(q: String) {
        _state.update { it.copy(query = q) }
        updateShowHistory()
        searchJob?.cancel()
        searchJob = viewModelScope.launch {
            delay(350)
            performSearch(q)
        }
    }

    fun onClearQuery() {
        _state.update { it.copy(query = "") }
        loadAll()
    }

    fun onFocusSearch(focused: Boolean) {
        _state.update { it.copy(showHistory = focused && it.query.isEmpty() && it.history.isNotEmpty()) }
    }

    fun onHistoryClick(item: String) {
        addToHistory(item)
        _state.update { it.copy(query = item, showHistory = false) }
        performSearch(item)
    }

    fun onClearHistory() {
        viewModelScope.launch {
            prefs.saveHistory(emptyList())
            _state.update { it.copy(showHistory = false) }
        }
    }

    fun retryLast() {
        performSearch(_state.value.lastQuery ?: _state.value.query)
    }

    fun onTaskClicked(task: Task) {
        addToHistory(task.title)
    }

    private fun addToHistory(item: String) {
        if (item.isBlank()) return
        viewModelScope.launch {
            val cur = prefs.history.first().toMutableList()
            cur.remove(item)
            cur.add(0, item)
            while (cur.size > 10) cur.removeAt(cur.lastIndex)
            prefs.saveHistory(cur)
            _state.update { it.copy(history = cur) }
            updateShowHistory()
        }
    }

    private fun loadAll() = performSearch(null)

    private fun performSearch(q: String?) {
        val query = q?.takeIf { it.isNotBlank() }
        if (query != null) {
            addToHistory(query)
        }
        viewModelScope.launch {
            _state.update { it.copy(status = ListStatus.LOADING, lastQuery = q) }
            runCatching { getTasks(q?.takeIf { it.isNotBlank() }) }
                .onSuccess { list ->
                    _state.update {
                        it.copy(
                            tasks = list,
                            status = if (list.isEmpty()) ListStatus.EMPTY else ListStatus.SUCCESS,
                            showHistory = false
                        )
                    }
                }
                .onFailure { _state.update { it.copy(status = ListStatus.ERROR) } }
        }
    }

    fun createOrUpdate(task: Task) {
        viewModelScope.launch {
            runCatching {
                if (task.id == 0) createTask(task) else updateTask(task)
            }.onSuccess { loadAll() }
        }
    }

    fun delete(id: Int) {
        viewModelScope.launch {
            runCatching { deleteTask(id) }.onSuccess { loadAll() }
        }
    }

    fun toggleDark(enabled: Boolean) {
        viewModelScope.launch { prefs.setDark(enabled) }
    }

    fun logout() {
        viewModelScope.launch { logoutUC() }
    }
}