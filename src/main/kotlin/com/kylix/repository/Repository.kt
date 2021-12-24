package com.kylix.repository

import com.kylix.model.Note
import com.kylix.model.User
import com.kylix.table.UserTable
import com.kylix.repository.DatabaseFactory.dbQuery
import com.kylix.table.NoteTable
import org.jetbrains.exposed.sql.*

class Repository {

    suspend fun addUser(user: User) {
        dbQuery {
            UserTable.insert { table ->
                table[UserTable.email] = user.email
                table[UserTable.hashPassword] = user.hashPassword
                table[UserTable.name] = user.name
            }
        }
    }

    suspend fun findUserByEmail(email: String) = dbQuery {
        UserTable.select {
            UserTable.email.eq(email)
        }
            .map {
                rowToUser(it)
            }
            .singleOrNull()
    }

    private fun rowToUser(row: ResultRow?): User? {
        if (row == null) {
            return null
        }

        return User(
            email = row[UserTable.email],
            name = row[UserTable.name],
            hashPassword = row[UserTable.hashPassword]
        )
    }

    suspend fun addNote(note: Note, email: String) {
        dbQuery {
            NoteTable.insert { noteTable ->
                noteTable[NoteTable.id] = note.id
                noteTable[NoteTable.userEmail] = email
                noteTable[NoteTable.noteTitle] = note.noteTitle
                noteTable[NoteTable.description] = note.description
                noteTable[NoteTable.date] = note.date
            }
        }
    }

    suspend fun getAllNotes(email: String): List<Note> = dbQuery {
        NoteTable.select {
            NoteTable.userEmail.eq(email)
        }.mapNotNull { rowToNote(it) }
    }

    suspend fun updateNote(note: Note, email: String) {
        dbQuery {
            NoteTable.update(
                where = {
                    NoteTable.userEmail.eq(email) and NoteTable.id.eq(note.id)
                }
            ) { noteTable ->
                noteTable[NoteTable.noteTitle] = note.noteTitle
                noteTable[NoteTable.description] = note.description
                noteTable[NoteTable.date] = note.date
            }
        }
    }

    suspend fun deleteNote(id: String, email: String) {
        dbQuery {
            NoteTable.deleteWhere {
                NoteTable.id.eq(id) and NoteTable.userEmail.eq(email)
            }
        }
    }

    private fun rowToNote(row: ResultRow?): Note? {
        if (row == null) {
            return null
        }
        return Note(
            id = row[NoteTable.id],
            noteTitle =  row[NoteTable.noteTitle],
            description = row[NoteTable.description],
            date = row[NoteTable.date]
        )
    }
}