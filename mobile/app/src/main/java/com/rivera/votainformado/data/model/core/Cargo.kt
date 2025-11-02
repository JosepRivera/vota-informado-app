package com.rivera.votainformado.data.model.core

import com.google.gson.annotations.SerializedName

/**
 * Tipos de cargos electorales.
 * Solo 3 opciones: Presidente, Senador, Diputado
 */
data class Cargo(
    val id: Int,
    @SerializedName("nombre_cargo")
    val nombreCargo: String,
    @SerializedName("created_at")
    val createdAt: String? = null,
    @SerializedName("updated_at")
    val updatedAt: String? = null
)

