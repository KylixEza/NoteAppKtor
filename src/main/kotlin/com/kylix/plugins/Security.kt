package com.kylix.plugins

import io.ktor.sessions.*
import io.ktor.auth.*
import io.ktor.util.*
import io.ktor.auth.jwt.*
import com.auth0.jwt.JWT
import com.auth0.jwt.JWTVerifier
import com.auth0.jwt.algorithms.Algorithm
import com.kylix.authentication.JWTService
import com.kylix.repository.Repository
import io.ktor.application.*
import io.ktor.response.*
import io.ktor.request.*
import io.ktor.routing.*

fun Application.configureSecurity(db: Repository, jwtService: JWTService) {
    data class MySession(val count: Int = 0)
    install(Sessions) {
        cookie<MySession>("MY_SESSION") {
            cookie.extensions["SameSite"] = "lax"
        }
    }

    authentication {
        jwt("JWT"){
            verifier(jwtService.verifier)
            realm = "Note Server"
            validate { credential ->
                val payload = credential.payload
                val email = payload.getClaim("email").asString()
                val user = db.findUserByEmail(email)
                user
            }
        }
    }
}
