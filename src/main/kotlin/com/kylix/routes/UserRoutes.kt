package com.kylix.routes

import com.kylix.authentication.JWTService
import com.kylix.model.LoginRequest
import com.kylix.model.RegisterRequest
import com.kylix.model.Response
import com.kylix.model.User
import com.kylix.repository.Repository
import io.ktor.application.*
import io.ktor.http.*
import io.ktor.locations.*
import io.ktor.locations.post
import io.ktor.request.*
import io.ktor.response.*
import io.ktor.routing.*
import kotlin.Exception


const val API_VERSION = "/v1"
const val USERS = "$API_VERSION/users"
const val REGISTER_REQUEST = "$USERS/register"
const val LOGIN_REQUEST = "$USERS/login"

@KtorExperimentalLocationsAPI
@Location(REGISTER_REQUEST)
class UserRegisterRoute

@KtorExperimentalLocationsAPI
@Location(LOGIN_REQUEST)
class UserLoginRoute

@KtorExperimentalLocationsAPI
fun Route.userRouting(
    db: Repository,
    jwtService: JWTService,
    hashFunction: (String) -> String
) {

    post<UserRegisterRoute> {
        val registerRequest = try {
            call.receive<RegisterRequest>()
        } catch (e: Exception) {
            call.respond(HttpStatusCode.BadRequest, Response(false, "Missing some fields"))
            return@post
        }

        try {
            val user = User(registerRequest.email, hashFunction(registerRequest.password), registerRequest.name)
            db.addUser(user)
            call.respond(HttpStatusCode.OK, Response(true, jwtService.generateToken(user)))
        } catch (e: Exception) {
            call.respond(HttpStatusCode.Conflict, Response(false, e.message?: "Some Error Occurred"))
        }
    }
    post<UserLoginRoute> {
        val loginRequest = try {
            call.receive<LoginRequest>()
        } catch (e: Exception) {
            call.respond(HttpStatusCode.BadRequest, Response(false, "Missing some fields"))
            return@post
        }

        try {
            val user = db.findUserByEmail(loginRequest.email)
            if (user == null) {
                call.respond(HttpStatusCode.BadRequest, Response(false, "Wrong Email"))
            } else {
                if (user.hashPassword == hashFunction(loginRequest.password)) {
                    call.respond(HttpStatusCode.OK, Response(true, jwtService.generateToken(user)))
                } else {
                    call.respond(HttpStatusCode.BadRequest, Response(false, "Password Incorrect"))
                }
            }
        } catch (e: Exception) {
            call.respond(HttpStatusCode.Conflict, Response(false, e.message ?: "Some Error Occurred"))
        }
    }
}