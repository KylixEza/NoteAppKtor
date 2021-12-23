package com.kylix

import com.kylix.authentication.JWTService
import com.kylix.authentication.hash
import io.ktor.server.engine.*
import io.ktor.server.netty.*
import com.kylix.plugins.*
import com.kylix.repository.DatabaseFactory
import com.kylix.repository.Repository

fun main() {
    embeddedServer(Netty, port = 8081, host = "localhost") {

        DatabaseFactory.init()
        val db = Repository()
        val jwtService = JWTService()
        val hashFunction = {s: String -> hash(s)}

        configureRouting(hashFunction, jwtService)
        //configureSecurity()
        configureSerialization()
    }.start(wait = true)
}
