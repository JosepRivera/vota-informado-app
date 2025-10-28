package com.rivera.votainformado.data.model.auth

import com.google.gson.annotations.SerializedName

data class DniValidationResponse(
    val dni: String,
    val nombre: String,
    @SerializedName("apellido_paterno")
    val apellidoPaterno: String,
    @SerializedName("apellido_materno")
    val apellidoMaterno: String
)