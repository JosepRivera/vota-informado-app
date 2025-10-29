package com.rivera.votainformado.data.model.auth

import com.google.gson.annotations.SerializedName
import com.rivera.votainformado.data.model.auth.Region

data class PerfilResponse(
    val id: Int,
    val dni: String,
    val nombre: String,
    @SerializedName("apellido_paterno")
    val apellidoPaterno: String,
    @SerializedName("apellido_materno")
    val apellidoMaterno: String,
    @SerializedName("nombre_completo")
    val nombreCompleto: String,
    val region: Region,
    val rol: String,
    @SerializedName("created_at")
    val createdAt: String
)