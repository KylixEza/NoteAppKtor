package com.kylix

import io.ktor.server.engine.*
import io.ktor.server.netty.*
import com.kylix.plugins.*
import com.kylix.repository.DatabaseFactory

fun main() {
    embeddedServer(Netty, port = 8080, host = "localhost") {

        DatabaseFactory.init()

        configureRouting()
        //configureSecurity()
        configureSerialization()
    }.start(wait = true)
}
