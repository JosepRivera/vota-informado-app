package com.rivera.votainformado.data.model.candidatos

import com.google.gson.annotations.SerializedName
import com.google.gson.JsonElement
import com.rivera.votainformado.data.model.core.Cargo

/**
 * Página paginada de candidatos
 */
data class CandidatosPage(
    val count: Int,
    val next: String?,
    val previous: String?,
    val results: List<CandidatoItem>
)

/**
 * Candidato en lista (versión simplificada)
 */
data class CandidatoItem(
    val id: Int,
    val nombre: String,
    @SerializedName("apellido_paterno")
    val apellidoPaterno: String,
    @SerializedName("apellido_materno")
    val apellidoMaterno: String,
    @SerializedName("nombre_completo")
    val nombreCompleto: String,
    val partido: Partido,
    val cargo: Cargo,
    // region puede venir como null, string o objeto; lo dejamos flexible
    val region: JsonElement?,
    @SerializedName("foto_url")
    val fotoUrl: String?,
    @SerializedName("total_votos")
    val totalVotos: Int?,
    val activo: Boolean? = true,
    @SerializedName("created_at")
    val createdAt: String? = null,
    @SerializedName("updated_at")
    val updatedAt: String? = null
)

/**
 * Detalle completo de un candidato
 */
data class CandidatoDetail(
    val id: Int,
    val nombre: String,
    @SerializedName("apellido_paterno")
    val apellidoPaterno: String,
    @SerializedName("apellido_materno")
    val apellidoMaterno: String,
    @SerializedName("nombre_completo")
    val nombreCompleto: String,
    val partido: Partido,
    val cargo: Cargo,
    val region: JsonElement?,
    @SerializedName("foto_url")
    val fotoUrl: String?,
    @SerializedName("total_votos")
    val totalVotos: Int?,
    val denuncias: List<Antecedente>?,
    val proyectos: List<Antecedente>?,
    val propuestas: List<Antecedente>?,
    val activo: Boolean? = true,
    @SerializedName("created_at")
    val createdAt: String?,
    @SerializedName("updated_at")
    val updatedAt: String? = null
)

/**
 * Antecedente de un candidato (denuncia, proyecto o propuesta)
 */
data class Antecedente(
    val id: Int,
    val tipo: String, // "denuncia", "proyecto", "propuesta"
    val titulo: String,
    val descripcion: String?,
    val fecha: String?,
    @SerializedName("fuente_url")
    val fuenteUrl: String?,
    @SerializedName("created_at")
    val createdAt: String?,
    @SerializedName("updated_at")
    val updatedAt: String? = null
)

/**
 * Partido político
 */
data class Partido(
    val id: Int,
    @SerializedName("nombre_partido")
    val nombrePartido: String,
    val sigla: String,
    @SerializedName("logo_url")
    val logoUrl: String?,
    val activo: Boolean? = true,
    @SerializedName("created_at")
    val createdAt: String? = null,
    @SerializedName("updated_at")
    val updatedAt: String? = null
)


