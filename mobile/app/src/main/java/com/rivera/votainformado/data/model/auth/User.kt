package com.rivera.votainformado.data.model.auth

import com.google.gson.annotations.SerializedName
import com.rivera.votainformado.data.model.core.Region

/**
 * Modelo de usuario personalizado.
 * Usamos DNI como identificador único.
 */
data class User(
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
    val rol: String, // "votante" o "invitado"
    @SerializedName("is_active")
    val isActive: Boolean = true,
    @SerializedName("is_staff")
    val isStaff: Boolean = false,
    @SerializedName("created_at")
    val createdAt: String,
    @SerializedName("updated_at")
    val updatedAt: String? = null
) {
    /**
     * Verifica si el usuario puede votar (solo votantes registrados)
     */
    fun puedeVotar(): Boolean = rol == "votante"
}