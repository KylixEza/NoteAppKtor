package com.kylix.routes

import com.kylix.model.Note
import com.kylix.model.Response
import com.kylix.model.User
import com.kylix.repository.Repository
import io.ktor.application.*
import io.ktor.auth.*
import io.ktor.http.*
import io.ktor.locations.*
import io.ktor.locations.post
import io.ktor.request.*
import io.ktor.response.*
import io.ktor.routing.*
import kotlin.Exception

const val NOTES = "$API_VERSION/notes"
const val CREATE = "$NOTES/create"
const val UPDATE = "$NOTES/update"
const val DELETE = "$NOTES/delete"

@KtorExperimentalLocationsAPI
@Location(NOTES)
class NoteGetRoute

@KtorExperimentalLocationsAPI
@Location(CREATE)
class NoteCreateRoute

@KtorExperimentalLocationsAPI
@Location(UPDATE)
class NoteUpdateRoute

@KtorExperimentalLocationsAPI
@Location(DELETE)
class NoteDeleteRoute

@KtorExperimentalLocationsAPI
fun Route.noteRoute(
    db: Repository,
    hashFunction: (String) -> String
) {

    authenticate("JWT") {

        //Create
        post<NoteCreateRoute> {
            val note = try {
                call.receive<Note>()
            } catch (e: Exception) {
                call.respond(HttpStatusCode.BadRequest, Response(false, "Missing Fields"))
                return@post
            }

            try {
                val email = call.principal<User>()!!.email
                db.addNote(note, email)
                call.respond(HttpStatusCode.OK, Response(true, "Note Added Successfully"))
            } catch (e: Exception) {
                call.respond(HttpStatusCode.Conflict, Response(false, e.message ?: "Some Error Occurred"))
            }
        }

        //Get All Notes
        get<NoteGetRoute> {
            try {
                val email = call.principal<User>()!!.email
                val notes = db.getAllNotes(email)
                call.respond(HttpStatusCode.OK, notes)
            } catch (e: Exception) {
                call.respond(HttpStatusCode.Conflict, emptyList<Note>())
            }
        }

        //Update
        post<NoteUpdateRoute> {
            val note = try {
                call.receive<Note>()
            } catch (e: Exception) {
                call.respond(HttpStatusCode.BadRequest, Response(false, "Missing Fields"))
                return@post
            }

            try {
                val email = call.principal<User>()!!.email
                db.updateNote(note, email)
                call.respond(HttpStatusCode.OK, Response(true, "Note Updated Successfully"))
            } catch (e: Exception) {
                call.respond(HttpStatusCode.Conflict, Response(false, e.message ?: "Some Error Occurred"))
            }
        }

        //Delete
        delete<NoteDeleteRoute> {
            val noteId = try {
                call.request.queryParameters["id"]!!
            } catch (e: Exception) {
                call.respond(HttpStatusCode.BadRequest, Response(false, e.message.toString()))
                return@delete
            }

            try {
                val email = call.principal<User>()!!.email
                db.deleteNote(noteId, email)
                call.respond(HttpStatusCode.OK, Response(true, "Note Deleted Successfully"))
            } catch (e: Exception) {
                call.respond(HttpStatusCode.Conflict, Response(false, e.message ?: "Some Error Occured"))
            }
        }
    }
}