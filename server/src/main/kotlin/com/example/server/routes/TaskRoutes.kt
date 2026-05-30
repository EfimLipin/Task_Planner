package com.example.server.routes

import com.example.server.models.TaskDto
import com.example.server.repository.TaskRepository
import io.ktor.http.*
import io.ktor.server.application.*
import io.ktor.server.auth.*
import io.ktor.server.auth.jwt.*
import io.ktor.server.request.*
import io.ktor.server.response.*
import io.ktor.server.routing.*

fun Route.taskRoutes() {
    authenticate("auth-jwt") {
        route("/tasks") {
            get {
                val userId = call.userId()
                val q = call.request.queryParameters["q"]
                call.respond(TaskRepository.list(userId, q))
            }
            post {
                val userId = call.userId()
                val dto = call.receive<TaskDto>()
                call.respond(TaskRepository.create(userId, dto))
            }
            put {
                val userId = call.userId()
                val dto = call.receive<TaskDto>()
                val res = TaskRepository.update(userId, dto)
                if (res == null) call.respond(HttpStatusCode.NotFound) else call.respond(res)
            }
            delete("/{id}") {
                val userId = call.userId()
                val id = call.parameters["id"]?.toIntOrNull() ?: return@delete call.respond(HttpStatusCode.BadRequest)
                if (TaskRepository.delete(userId, id)) call.respond(HttpStatusCode.OK)
                else call.respond(HttpStatusCode.NotFound)
            }
        }
    }
}

private fun ApplicationCall.userId(): Int =
    principal<JWTPrincipal>()!!.payload.getClaim("userId").asInt()