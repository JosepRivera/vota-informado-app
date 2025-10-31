package com.rivera.votainformado.data.model.candidatos

import com.google.gson.annotations.SerializedName
import com.google.gson.JsonElement

data class CandidatosPage(
    val count: Int,
    val next: String?,
    val previous: String?,
    val results: List<CandidatoItem>
)

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
    val totalVotos: Int?
)

data class Partido(
    val id: Int,
    @SerializedName("nombre_partido")
    val nombrePartido: String,
    val sigla: String,
    @SerializedName("logo_url")
    val logoUrl: String?
)

data class Cargo(
    val id: Int,
    @SerializedName("nombre_cargo")
    val nombreCargo: String
)


