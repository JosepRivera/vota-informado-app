package com.rivera.votainformado.ui.auth.register

import com.rivera.votainformado.data.model.Region
import com.rivera.votainformado.data.model.auth.DniValidationResponse

data class RegisterState(
    val isLoading: Boolean = false,
    val isValidatingDni: Boolean = false,
    val isLoadingRegiones: Boolean = false,
    val dniValidated: DniValidationResponse? = null,
    val regiones: List<Region> = emptyList(),
    val errorMessage: String? = null,
    val successMessage: String? = null
)