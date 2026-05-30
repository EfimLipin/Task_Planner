package com.example.server

import com.example.server.config.DatabaseFactory
import com.example.server.config.JwtConfig
import com.example.server.routes.authRoutes
import com.example.server.routes.taskRoutes
import io.ktor.serialization.kotlinx.json.*
import io.ktor.server.application.*
import io.ktor.server.auth.*
import io.ktor.server.auth.jwt.*
import io.ktor.server.engine.*
import io.ktor.server.netty.*
import io.ktor.server.plugins.contentnegotiation.*
import io.ktor.server.plugins.cors.routing.*
import io.ktor.server.routing.*
import kotlinx.serialization.json.Json

fun main() {
    embeddedServer(Netty, port = 8080, host = "0.0.0.0") {
        DatabaseFactory.init()

        install(ContentNegotiation) { json(Json { ignoreUnknownKeys = true }) }
        install(CORS) {
            anyHost()
            allowHeader("Authorization")
            allowHeader("Content-Type")
            allowMethod(io.ktor.http.HttpMethod.Put)
            allowMethod(io.ktor.http.HttpMethod.Delete)
        }
        install(Authentication) {
            jwt("auth-jwt") {
                realm = JwtConfig.realm
                verifier(JwtConfig.verifier)
                validate { credential ->
                    if (credential.payload.getClaim("userId").asInt() != 0)
                        JWTPrincipal(credential.payload) else null
                }
            }
        }
        routing {
            authRoutes()
            taskRoutes()
        }
    }.start(wait = true)
}