package com.kylix

import com.kylix.authentication.JWTService
import com.kylix.authentication.hash
import io.ktor.server.engine.*
import io.ktor.server.netty.*
import com.kylix.plugins.*
import com.kylix.repository.DatabaseFactory
import com.kylix.repository.Repository
import io.ktor.application.*
import io.ktor.locations.*

@KtorExperimentalLocationsAPI
fun main() {
    embeddedServer(Netty, port = 8082, host = "localhost") {

        DatabaseFactory.init()
        val db = Repository()
        val jwtService = JWTService()
        val hashFunction = {s: String -> hash(s)}

        install(Locations)

        configureRouting(db, jwtService, hashFunction, )
        //configureSecurity()
        configureSerialization()
    }.start(wait = true)
}
