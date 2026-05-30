package com.example.taskplanner.presentation.tasks

import androidx.compose.foundation.layout.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import com.example.taskplanner.domain.model.Task

@Composable
fun TaskEditDialog(
    initial: Task?,
    onDismiss: () -> Unit,
    onSave: (Task) -> Unit
) {
    var title by remember { mutableStateOf(initial?.title ?: "") }
    var desc by remember { mutableStateOf(initial?.description ?: "") }

    AlertDialog(
        onDismissRequest = onDismiss,
        title = { Text(if (initial == null) "Новая задача" else "Изменить задачу") },
        text = {
            Column {
                OutlinedTextField(title, { title = it }, label = { Text("Название") },
                    modifier = Modifier.fillMaxWidth())
                Spacer(Modifier.height(8.dp))
                OutlinedTextField(desc, { desc = it }, label = { Text("Описание") },
                    modifier = Modifier.fillMaxWidth())
            }
        },
        confirmButton = {
            TextButton(
                onClick = {
                    if (title.isBlank()) return@TextButton
                    onSave(
                        (initial ?: Task(title = title)).copy(title = title.trim(), description = desc.trim())
                    )
                }
            ) { Text("Сохранить") }
        },
        dismissButton = { TextButton(onClick = onDismiss) { Text("Отмена") } }
    )
}