package com.rivera.votainformado.data.model.auth

// data -> Proporciona funciones importantes automaticamente
data class AuthResponse(
    val user: User,
    val tokens: Tokens,
    val message: String
)