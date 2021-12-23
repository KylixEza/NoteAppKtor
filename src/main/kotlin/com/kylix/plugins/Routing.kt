package com.kylix.plugins

import com.kylix.authentication.JWTService
import com.kylix.authentication.hash
import com.kylix.model.User
import io.ktor.routing.*
import io.ktor.http.*
import io.ktor.application.*
import io.ktor.response.*
import io.ktor.request.*

fun Application.configureRouting(
    hashFunction: (String) -> String,
    jwtService: JWTService
) {

    routing {
        get("/") {
            call.respondText("Hello World!")
        }

        get("/note/{id}") {
            val id = call.parameters["id"]
            call.respond("$id")
        }

        get("/token") {
            val email = call.request.queryParameters["email"]!!
            val name = call.request.queryParameters["name"]!!
            val password = call.request.queryParameters["password"]!!

            val user = User(email, name, hashFunction(password))
            call.respond(jwtService.generateToken(user))
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
