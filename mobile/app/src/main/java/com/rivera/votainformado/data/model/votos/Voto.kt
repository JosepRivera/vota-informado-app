package com.rivera.votainformado.data.model.votos

import com.google.gson.annotations.SerializedName
import com.rivera.votainformado.data.model.auth.User
import com.rivera.votainformado.data.model.candidatos.CandidatoItem
import com.rivera.votainformado.data.model.core.Cargo

/**
 * Registro de votos emitidos por los usuarios.
 * - Un usuario solo puede votar UNA VEZ por cargo.
 * - Para Diputados: solo puede votar por candidatos de su región.
 */
data class Voto(
    val id: Int,
    val usuario: User,
    val candidato: CandidatoItem,
    val cargo: Cargo,
    @SerializedName("created_at")
    val createdAt: String? = null,
    @SerializedName("updated_at")
    val updatedAt: String? = null
)

/**
 * Request para emitir un voto
 * Body: {"candidato_id": 5}
 */
data class VotoRequest(
    @SerializedName("candidato_id")
    val candidatoId: Int
)

/**
 * Response después de emitir un voto
 */
data class VotoResponse(
    val message: String,
    val voto: Voto
)

/**
 * Response para verificar si puede votar
 */
data class PuedeVotarResponse(
    @SerializedName("puede_votar")
    val puedeVotar: Boolean,
    @SerializedName("ya_voto")
    val yaVoto: Boolean
)

/**
 * Resultado general de elecciones
 */
data class ResultadoGeneral(
    val id: Int,
    @SerializedName("nombre_completo")
    val nombreCompleto: String,
    val partido: PartidoResultado,
    val cargo: String,
    val region: String?,
    @SerializedName("total_votos")
    val totalVotos: Int,
    @SerializedName("foto_url")
    val fotoUrl: String?
)

data class PartidoResultado(
    val sigla: String,
    val nombre: String,
    @SerializedName("logo_url")
    val logoUrl: String?
)

/**
 * Resultado agrupado por partido
 */
data class ResultadoPorPartido(
    val id: Int,
    @SerializedName("nombre_partido")
    val nombrePartido: String,
    val sigla: String,
    @SerializedName("logo_url")
    val logoUrl: String?,
    @SerializedName("total_votos")
    val totalVotos: Int
)

/**
 * Estadísticas generales del sistema
 */
data class Estadisticas(
    @SerializedName("total_votos")
    val totalVotos: Int,
    @SerializedName("total_votantes")
    val totalVotantes: Int,
    @SerializedName("total_candidatos")
    val totalCandidatos: Int,
    @SerializedName("votos_por_cargo")
    val votosPorCargo: Map<String, Int>
)

