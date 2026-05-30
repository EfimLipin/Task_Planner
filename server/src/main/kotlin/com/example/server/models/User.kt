package com.example.server.models

import kotlinx.serialization.Serializable
import org.jetbrains.exposed.sql.Table

object Users : Table("users") {
    val id = integer("id").autoIncrement()
    val email = varchar("email", 255).uniqueIndex()
    val passwordHash = varchar("password_hash", 255)
    override val primaryKey = PrimaryKey(id)
}

@Serializable data class AuthRequest(val email: String, val password: String)
@Serializable data class AuthResponse(val token: String, val userId: Int, val email: String)
@Serializable data class ErrorResponse(val message: String)