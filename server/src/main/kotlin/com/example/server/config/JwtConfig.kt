package com.example.server.config

import com.auth0.jwt.JWT
import com.auth0.jwt.algorithms.Algorithm
import java.util.*

object JwtConfig {
    private const val secret = "super_secret_key_change_me"
    const val issuer = "task-planner"
    const val realm = "task-planner-app"
    private val algorithm = Algorithm.HMAC256(secret)

    val verifier: com.auth0.jwt.JWTVerifier = JWT.require(algorithm).withIssuer(issuer).build()

    fun makeToken(userId: Int): String = JWT.create()
        .withIssuer(issuer)
        .withClaim("userId", userId)
        .withExpiresAt(Date(System.currentTimeMillis() + 1000L * 60 * 60 * 24 * 7))
        .sign(algorithm)
}