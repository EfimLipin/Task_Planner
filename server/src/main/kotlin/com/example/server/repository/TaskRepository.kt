package com.example.server.repository

import com.example.server.models.TaskDto
import com.example.server.models.Tasks
import org.jetbrains.exposed.sql.*
import org.jetbrains.exposed.sql.SqlExpressionBuilder.eq
import org.jetbrains.exposed.sql.transactions.transaction

object TaskRepository {
    fun list(userId: Int, query: String? = null): List<TaskDto> = transaction {
        val base = Tasks.select { Tasks.userId eq userId }
        val rows = if (query.isNullOrBlank()) base
        else base.andWhere {
            (Tasks.title like "%$query%") or (Tasks.description like "%$query%")
        }
        rows.orderBy(Tasks.createdAt to SortOrder.DESC).map { it.toDto() }
    }

    fun create(userId: Int, dto: TaskDto): TaskDto = transaction {
        val id = Tasks.insert {
            it[Tasks.userId] = userId
            it[title] = dto.title
            it[description] = dto.description
            it[dueDate] = dto.dueDate
            it[isDone] = dto.isDone
            it[createdAt] = System.currentTimeMillis()
        } get Tasks.id
        Tasks.select { Tasks.id eq id }.single().toDto()
    }

    fun update(userId: Int, dto: TaskDto): TaskDto? = transaction {
        val updated = Tasks.update({ (Tasks.id eq dto.id) and (Tasks.userId eq userId) }) {
            it[title] = dto.title
            it[description] = dto.description
            it[dueDate] = dto.dueDate
            it[isDone] = dto.isDone
        }
        if (updated == 0) null
        else Tasks.select { Tasks.id eq dto.id }.single().toDto()
    }

    fun delete(userId: Int, id: Int): Boolean = transaction {
        Tasks.deleteWhere { (Tasks.id eq id) and (Tasks.userId eq userId) } > 0
    }

    private fun ResultRow.toDto() = TaskDto(
        id = this[Tasks.id],
        title = this[Tasks.title],
        description = this[Tasks.description],
        dueDate = this[Tasks.dueDate],
        isDone = this[Tasks.isDone],
        createdAt = this[Tasks.createdAt]
    )
}