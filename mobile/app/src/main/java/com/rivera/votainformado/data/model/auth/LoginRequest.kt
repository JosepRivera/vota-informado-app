package com.rivera.votainformado.data.model.auth

data class LoginRequest (
    val dni: String,
    val password: String
)