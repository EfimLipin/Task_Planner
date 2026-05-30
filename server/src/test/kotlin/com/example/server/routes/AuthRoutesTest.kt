package com.example.server.routes

import com.example.server.config.DatabaseFactory
import com.example.server.models.AuthRequest
import com.example.server.models.AuthResponse
import com.example.server.models.Users
import com.example.server.testModule
import io.ktor.client.request.*
import io.ktor.client.statement.*
import io.ktor.http.*
import io.ktor.server.testing.*
import kotlinx.serialization.json.Json
import org.jetbrains.exposed.sql.deleteAll
import org.jetbrains.exposed.sql.transactions.transaction
import org.junit.jupiter.api.*
import org.junit.jupiter.api.Assertions.*

@TestInstance(TestInstance.Lifecycle.PER_CLASS)
class AuthRoutesTest {
    private val json = Json { ignoreUnknownKeys = true }

    @BeforeAll
    fun initDb() {
        DatabaseFactory.init()
    }

    @BeforeEach
    fun cleanUsers() {
        transaction {
            Users.deleteAll()
        }
    }

    @Test
    fun `1 - регистрация с корректными данными`() = testApplication {
        testModule()

        val response = client.post("/auth/register") {
            contentType(ContentType.Application.Json)
            setBody(json.encodeToString(AuthRequest.serializer(), AuthRequest("test@mail.com", "123456")))
        }
        assertEquals(HttpStatusCode.OK, response.status)
        val body = json.decodeFromString(AuthResponse.serializer(), response.bodyAsText())
        assertNotNull(body.token)
        assertEquals("test@mail.com", body.email)
        assertTrue(body.userId > 0)
    }

    @Test
    fun `2 - регистрация с существующим email`() = testApplication {
        testModule()
        client.post("/auth/register") {
            contentType(ContentType.Application.Json)
            setBody(json.encodeToString(AuthRequest.serializer(), AuthRequest("dup@mail.com", "123456")))
        }
        val response = client.post("/auth/register") {
            contentType(ContentType.Application.Json)
            setBody(json.encodeToString(AuthRequest.serializer(), AuthRequest("dup@mail.com", "123456")))
        }
        assertEquals(HttpStatusCode.Conflict, response.status)
        assertTrue(response.bodyAsText().contains("Email уже занят"))
    }

    @Test
    fun `3 - вход с верными данными`() = testApplication {
        testModule()
        client.post("/auth/register") {
            contentType(ContentType.Application.Json)
            setBody(json.encodeToString(AuthRequest.serializer(), AuthRequest("login@mail.com", "qwerty")))
        }
        val response = client.post("/auth/login") {
            contentType(ContentType.Application.Json)
            setBody(json.encodeToString(AuthRequest.serializer(), AuthRequest("login@mail.com", "qwerty")))
        }
        assertEquals(HttpStatusCode.OK, response.status)
        val body = json.decodeFromString(AuthResponse.serializer(), response.bodyAsText())
        assertNotNull(body.token)
    }

    @Test
    fun `4 - вход с неверным паролем`() = testApplication {
        testModule()
        client.post("/auth/register") {
            contentType(ContentType.Application.Json)
            setBody(json.encodeToString(AuthRequest.serializer(), AuthRequest("wrong@mail.com", "correct")))
        }
        val response = client.post("/auth/login") {
            contentType(ContentType.Application.Json)
            setBody(json.encodeToString(AuthRequest.serializer(), AuthRequest("wrong@mail.com", "badpass")))
        }
        assertEquals(HttpStatusCode.Unauthorized, response.status)
        assertTrue(response.bodyAsText().contains("Неверный логин или пароль"))
    }
}