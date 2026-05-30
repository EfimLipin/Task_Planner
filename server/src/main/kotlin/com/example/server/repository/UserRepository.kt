package com.example.server.repository

import com.example.server.models.Users
import org.jetbrains.exposed.sql.*
import org.jetbrains.exposed.sql.transactions.transaction
import org.mindrot.jbcrypt.BCrypt

object UserRepository {
    fun register(email: String, password: String): Pair<Int, String>? = transaction {
        if (Users.select { Users.email eq email }.any()) return@transaction null
        val hash = BCrypt.hashpw(password, BCrypt.gensalt())
        val id = Users.insert {
            it[Users.email] = email
            it[passwordHash] = hash
        } get Users.id
        id to email
    }

    fun login(email: String, password: String): Pair<Int, String>? = transaction {
        val row = Users.select { Users.email eq email }.singleOrNull() ?: return@transaction null
        if (!BCrypt.checkpw(password, row[Users.passwordHash])) return@transaction null
        row[Users.id] to row[Users.email]
    }
}