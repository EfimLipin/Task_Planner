package com.example.server.models

import kotlinx.serialization.Serializable
import org.jetbrains.exposed.sql.Table

object Tasks : Table("tasks") {
    val id = integer("id").autoIncrement()
    val userId = integer("user_id").references(Users.id)
    val title = varchar("title", 255)
    val description = text("description")
    val dueDate = long("due_date")
    val isDone = bool("is_done").default(false)
    val createdAt = long("created_at")
    override val primaryKey = PrimaryKey(id)
}

@Serializable
data class TaskDto(
    val id: Int = 0,
    val title: String,
    val description: String = "",
    val dueDate: Long = 0,
    val isDone: Boolean = false,
    val createdAt: Long = System.currentTimeMillis()
)