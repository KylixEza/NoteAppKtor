package com.kylix.plugins

import com.kylix.authentication.JWTService
import com.kylix.authentication.hash
import com.kylix.model.User
import com.kylix.repository.Repository
import com.kylix.routes.noteRoute
import com.kylix.routes.userRouting
import io.ktor.routing.*
import io.ktor.http.*
import io.ktor.application.*
import io.ktor.locations.*
import io.ktor.response.*
import io.ktor.request.*

@KtorExperimentalLocationsAPI
fun Application.configureRouting(
    db: Repository,
    jwtService: JWTService,
    hashFunction: (String) -> String
) {

    routing {
        get("/") {
            call.respondText("Hello World!")
        }

        userRouting(db, jwtService, hashFunction)
        noteRoute(db, hashFunction)

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
