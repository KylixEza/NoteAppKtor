package com.kylix.repository

import com.kylix.model.User
import com.kylix.table.UserTable
import org.jetbrains.exposed.sql.ResultRow
import org.jetbrains.exposed.sql.insert
import org.jetbrains.exposed.sql.select

class Repository {

    suspend fun addUser(user: User) {
        DatabaseFactory.dbQuery {
            UserTable.insert { table ->
                table[UserTable.email] = user.email
                table[UserTable.hashPassword] = user.hashPassword
                table[UserTable.name] = user.name
            }
        }
    }

    suspend fun findUserByEmail(email: String) = DatabaseFactory.dbQuery {
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
}