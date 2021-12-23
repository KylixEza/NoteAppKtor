package com.kylix.plugins

import io.ktor.routing.*
import io.ktor.http.*
import io.ktor.application.*
import io.ktor.response.*
import io.ktor.request.*

fun Application.configureRouting() {

    routing {
        get("/") {
            call.respondText("Hello World!")
        }

        get("/note/{id}") {
            val id = call.parameters["id"]
            call.respond("$id")
        }

        get("/note") {
            val id = call.request.queryParameters["id"]
            call.respond("$id")
        }

        route("/notes") {
            route("/create") {
                post {
                    val body = call.receive<String>()
                    call.respond(body)
                }
            }

            delete {
                val body = call.receive<String>()
                call.respond(body)
            }
        }
    }
}
