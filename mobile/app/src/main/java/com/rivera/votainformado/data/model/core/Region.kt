package com.rivera.votainformado.data.model.core

import com.google.gson.annotations.SerializedName

/**
 * Regiones del Perú (departamentos).
 * Ejemplo: Lima, Cusco, Arequipa, etc.
 */
data class Region(
    val id: Int,
    @SerializedName("nombre_region")
    val nombreRegion: String,
    @SerializedName("created_at")
    val createdAt: String? = null,
    @SerializedName("updated_at")
    val updatedAt: String? = null
)

