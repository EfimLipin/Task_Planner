package com.example.taskplanner.presentation.tasks

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.text.KeyboardActions
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.focus.onFocusChanged
import androidx.compose.ui.platform.LocalSoftwareKeyboardController
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.example.taskplanner.domain.model.Task

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun TaskListScreen(
    isDark: Boolean,
    onToggleTheme: (Boolean) -> Unit,
    onLogout: () -> Unit,
    vm: TaskViewModel = hiltViewModel()
) {
    val state by vm.state.collectAsStateWithLifecycle()
    val kb = LocalSoftwareKeyboardController.current
    var showDialog by remember { mutableStateOf(false) }
    var editing by remember { mutableStateOf<Task?>(null) }

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Планировщик задач") },
                actions = {
                    IconToggleButton(checked = isDark, onCheckedChange = {
                        vm.toggleDark(it); onToggleTheme(it)
                    }) {
                        Icon(if (isDark) Icons.Default.DarkMode else Icons.Default.LightMode, null)
                    }
                    IconButton(onClick = { vm.logout(); onLogout() }) {
                        Icon(Icons.Default.Logout, contentDescription = "Выход")
                    }
                }
            )
        },
        floatingActionButton = {
            FloatingActionButton(onClick = { editing = null; showDialog = true }) {
                Icon(Icons.Default.Add, null)
            }
        }
    ) { padding ->
        Column(Modifier.padding(padding).fillMaxSize()) {

            OutlinedTextField(
                value = state.query,
                onValueChange = vm::onQueryChange,
                placeholder = { Text("Поиск по задачам") },
                singleLine = true,
                leadingIcon = { Icon(Icons.Default.Search, null) },
                trailingIcon = {
                    if (state.query.isNotEmpty()) {
                        IconButton(onClick = {
                            vm.onClearQuery()
                            kb?.hide()
                        }) { Icon(Icons.Default.Close, contentDescription = "Очистить") }
                    }
                },
                keyboardOptions = KeyboardOptions(imeAction = ImeAction.Search),
                keyboardActions = KeyboardActions(onSearch = { kb?.hide() }),
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(12.dp)
                    .onFocusChanged { vm.onFocusSearch(it.isFocused) }
            )

            if (state.showHistory && state.history.isNotEmpty()) {
                HistoryBlock(
                    history = state.history,
                    onClick = vm::onHistoryClick,
                    onClear = vm::onClearHistory
                )
            } else {
                Box(Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                    when (state.status) {
                        ListStatus.LOADING -> CircularProgressIndicator()
                        ListStatus.EMPTY -> EmptyPlaceholder()
                        ListStatus.ERROR -> ErrorPlaceholder(onRetry = vm::retryLast)
                        else -> LazyColumn(Modifier.fillMaxSize()) {
                            items(state.tasks, key = { it.id }) { task ->
                                TaskItem(
                                    task = task,
                                    onClick = {
                                        vm.onTaskClicked(task)
                                        editing = task; showDialog = true
                                    },
                                    onToggle = { vm.createOrUpdate(task.copy(isDone = !task.isDone)) },
                                    onDelete = { vm.delete(task.id) }
                                )
                                Divider()
                            }
                        }
                    }
                }
            }
        }

        if (showDialog) {
            TaskEditDialog(
                initial = editing,
                onDismiss = { showDialog = false },
                onSave = { t -> vm.createOrUpdate(t); showDialog = false }
            )
        }
    }
}

@Composable
private fun HistoryBlock(history: List<String>, onClick: (String) -> Unit, onClear: () -> Unit) {
    Column(Modifier.fillMaxWidth().padding(horizontal = 12.dp)) {
        Text("История поиска", style = MaterialTheme.typography.titleMedium)
        Spacer(Modifier.height(8.dp))
        history.forEach { item ->
            ListItem(
                headlineContent = { Text(item) },
                leadingContent = { Icon(Icons.Default.History, null) },
                modifier = Modifier.fillMaxWidth().padding(vertical = 2.dp),
                tonalElevation = 0.dp
            )
            TextButton(onClick = { onClick(item) }) { Text("Искать снова") }
        }
        Spacer(Modifier.height(8.dp))
        OutlinedButton(onClick = onClear, modifier = Modifier.fillMaxWidth()) {
            Text("Очистить историю")
        }
    }
}

@Composable
private fun EmptyPlaceholder() {
    Column(horizontalAlignment = Alignment.CenterHorizontally) {
        Icon(Icons.Default.SearchOff, null, modifier = Modifier.size(64.dp))
        Spacer(Modifier.height(8.dp))
        Text("Ничего не найдено")
    }
}

@Composable
private fun ErrorPlaceholder(onRetry: () -> Unit) {
    Column(horizontalAlignment = Alignment.CenterHorizontally) {
        Icon(Icons.Default.CloudOff, null, modifier = Modifier.size(64.dp))
        Spacer(Modifier.height(8.dp))
        Text("Не удалось выполнить запрос")
        Spacer(Modifier.height(12.dp))
        Button(onClick = onRetry) { Text("Обновить") }
    }
}

@Composable
private fun TaskItem(task: Task, onClick: () -> Unit, onToggle: () -> Unit, onDelete: () -> Unit) {
    ListItem(
        headlineContent = { Text(task.title) },
        supportingContent = { if (task.description.isNotBlank()) Text(task.description) },
        leadingContent = {
            Checkbox(checked = task.isDone, onCheckedChange = { onToggle() })
        },
        trailingContent = {
            IconButton(onClick = onDelete) { Icon(Icons.Default.Delete, null) }
        },
        modifier = Modifier
            .fillMaxWidth()
            .clickable(onClick = onClick)
    )
}