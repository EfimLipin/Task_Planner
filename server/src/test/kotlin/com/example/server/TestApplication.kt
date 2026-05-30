package com.example.server

import com.example.server.config.JwtConfig
import com.example.server.routes.authRoutes
import com.example.server.routes.taskRoutes
import io.ktor.serialization.kotlinx.json.json
import io.ktor.server.application.*
import io.ktor.server.auth.*
import io.ktor.server.auth.jwt.*
import io.ktor.server.plugins.contentnegotiation.*
import io.ktor.server.routing.*
import io.ktor.server.testing.ApplicationTestBuilder
import kotlinx.serialization.json.Json

fun ApplicationTestBuilder.testModule() {
    application {
        install(ContentNegotiation) {
            json(Json { ignoreUnknownKeys = true })
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
    }
}