package com.example.server.routes

import com.example.server.config.DatabaseFactory
import com.example.server.models.AuthRequest
import com.example.server.models.AuthResponse
import com.example.server.models.TaskDto
import com.example.server.models.Tasks
import com.example.server.models.Users
import com.example.server.testModule
import io.ktor.client.request.*
import io.ktor.client.statement.*
import io.ktor.http.*
import io.ktor.server.testing.*
import junit.framework.TestCase.assertTrue
import kotlinx.serialization.json.Json
import org.jetbrains.exposed.sql.deleteAll
import org.jetbrains.exposed.sql.transactions.transaction
import org.junit.jupiter.api.*
import kotlin.test.assertEquals

@TestInstance(TestInstance.Lifecycle.PER_CLASS)
class TaskRoutesTest {
    private val json = Json { ignoreUnknownKeys = true }
    private lateinit var userToken: String
    private var userId = 0

    @BeforeAll
    fun prepare() = testApplication {
        DatabaseFactory.init()
        transaction {
            Users.deleteAll()
            Tasks.deleteAll()
        }
        testModule()
        val reg = client.post("/auth/register") {
            contentType(ContentType.Application.Json)
            setBody(json.encodeToString(AuthRequest.serializer(), AuthRequest("tasker@mail.com", "123456")))
        }
        assertEquals(HttpStatusCode.OK, reg.status)
        val auth = json.decodeFromString(AuthResponse.serializer(), reg.bodyAsText())
        userToken = auth.token
        userId = auth.userId
    }

    @Test
    fun `5 - создание задачи`() = testApplication {
        testModule()
        val dto = TaskDto(title = "Купить молоко", description = "В магазине через дорогу")
        val response = client.post("/tasks") {
            contentType(ContentType.Application.Json)
            bearerAuth(userToken)
            setBody(json.encodeToString(TaskDto.serializer(), dto))
        }
        assertEquals(HttpStatusCode.OK, response.status)
        val created = json.decodeFromString(TaskDto.serializer(), response.bodyAsText())
        assertEquals("Купить молоко", created.title)
        assertTrue(created.id > 0)
    }

    @Test
    fun `6 - поиск задачи по заголовку`() = testApplication {
        testModule()
        client.post("/tasks") {
            contentType(ContentType.Application.Json)
            bearerAuth(userToken)
            setBody(json.encodeToString(TaskDto.serializer(), TaskDto(title = "Кофе")))
        }
        client.post("/tasks") {
            contentType(ContentType.Application.Json)
            bearerAuth(userToken)
            setBody(json.encodeToString(TaskDto.serializer(), TaskDto(title = "Чай")))
        }
        val response = client.get("/tasks?q=Коф") {
            bearerAuth(userToken)
        }
        assertEquals(HttpStatusCode.OK, response.status)
        val list = json.decodeFromString<List<TaskDto>>(response.bodyAsText())
        assertEquals(1, list.size)
        assertEquals("Кофе", list[0].title)
    }
}