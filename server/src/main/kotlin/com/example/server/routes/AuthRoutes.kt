package com.example.server.routes

import com.example.server.config.JwtConfig
import com.example.server.models.*
import com.example.server.repository.UserRepository
import io.ktor.http.*
import io.ktor.server.application.*
import io.ktor.server.request.*
import io.ktor.server.response.*
import io.ktor.server.routing.*

fun Route.authRoutes() {
    route("/auth") {
        post("/register") {
            val req = call.receive<AuthRequest>()
            if (req.email.isBlank() || req.password.length < 6) {
                call.respond(HttpStatusCode.BadRequest, ErrorResponse("Некорректные данные"))
                return@post
            }
            val res = UserRepository.register(req.email, req.password)
            if (res == null) call.respond(HttpStatusCode.Conflict, ErrorResponse("Email уже занят"))
            else call.respond(AuthResponse(JwtConfig.makeToken(res.first), res.first, res.second))
        }
        post("/login") {
            val req = call.receive<AuthRequest>()
            val res = UserRepository.login(req.email, req.password)
            if (res == null) call.respond(HttpStatusCode.Unauthorized, ErrorResponse("Неверный логин или пароль"))
            else call.respond(AuthResponse(JwtConfig.makeToken(res.first), res.first, res.second))
        }
    }
}