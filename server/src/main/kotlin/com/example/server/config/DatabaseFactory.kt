package com.example.server.config

import com.example.server.models.Tasks
import com.example.server.models.Users
import org.jetbrains.exposed.sql.Database
import org.jetbrains.exposed.sql.SchemaUtils
import org.jetbrains.exposed.sql.transactions.transaction

object DatabaseFactory {
    fun init() {
        Database.connect("jdbc:sqlite:planner.db", driver = "org.sqlite.JDBC")
        transaction { SchemaUtils.create(Users, Tasks) }
    }
}