package com.rivera.votainformado.ui.auth.login

data class LoginState(
    val isLoading: Boolean = false,
    val successMessage: String? = null,
    val errorMessage: String? = null
)