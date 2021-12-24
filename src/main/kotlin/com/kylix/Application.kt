package com.kylix

import com.kylix.authentication.JWTService
import com.kylix.authentication.hash
import io.ktor.server.engine.*
import io.ktor.server.netty.*
import com.kylix.plugins.*
import com.kylix.repository.DatabaseFactory
import com.kylix.repository.Repository
import io.ktor.application.*
import io.ktor.auth.*
import io.ktor.locations.*

@KtorExperimentalLocationsAPI
fun main() {
    embeddedServer(Netty, port = System.getenv("PORT").toInt(), host = "localhost") {

        DatabaseFactory.init()
        val db = Repository()
        val jwtService = JWTService()
        val hashFunction = {s: String -> hash(s)}

        install(Locations)
        install(Authentication)

        configureSecurity(db, jwtService)
        configureRouting(db, jwtService, hashFunction)
        configureSerialization()
    }.start(wait = true)
}
