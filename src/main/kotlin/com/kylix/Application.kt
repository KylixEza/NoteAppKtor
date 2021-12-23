package com.kylix

import io.ktor.server.engine.*
import io.ktor.server.netty.*
import com.kylix.plugins.*

fun main() {
    embeddedServer(Netty, port = 8080, host = "localhost") {
        configureRouting()
        //configureSecurity()
        configureSerialization()
    }.start(wait = true)
}
