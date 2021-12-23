package com.kylix.repository

import com.zaxxer.hikari.HikariConfig
import com.zaxxer.hikari.HikariDataSource
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import org.jetbrains.exposed.sql.Database
import org.jetbrains.exposed.sql.transactions.transaction

object DatabaseFactory {

    fun init() {
        Database.connect(hikari())
    }

    suspend fun<T> dbQuery(block:() -> T): T =
        withContext(Dispatchers.IO) {
            transaction { block() }
        }


    fun hikari(): HikariDataSource {
        val config = HikariConfig()
        config.apply {
            driverClassName = System.getenv("JDBC_DRIVER")
            jdbcUrl = System.getenv("DATABASE_URL")
            maximumPoolSize = 6
            isAutoCommit = false
            transactionIsolation = "TRANSACTION_REPEATABLE_READ"
            validate()
        }
        return HikariDataSource(config)
    }
}