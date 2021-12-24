package com.kylix.model

import io.ktor.auth.*

data class User(
    val email: String,
    val name: String,
    val hashPassword: String
): Principal
