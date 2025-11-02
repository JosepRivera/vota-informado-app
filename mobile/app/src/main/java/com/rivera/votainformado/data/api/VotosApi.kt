package com.rivera.votainformado.data.api

import com.rivera.votainformado.data.model.votos.Voto
import com.rivera.votainformado.data.model.votos.VotoRequest
import com.rivera.votainformado.data.model.votos.VotoResponse
import com.rivera.votainformado.data.model.votos.ResultadoGeneral
import com.rivera.votainformado.data.model.votos.ResultadoPorPartido
import com.rivera.votainformado.data.model.votos.Estadisticas
import com.rivera.votainformado.data.model.votos.PuedeVotarResponse
import retrofit2.Response
import retrofit2.http.Body
import retrofit2.http.GET
import retrofit2.http.POST
import retrofit2.http.Path
import retrofit2.http.Query

interface VotosApi {
    /**
     * POST /api/votos/votar/
     * Emite un voto para un candidato.
     * Requiere autenticación (JWT token)
     */
    @POST("votos/votar/")
    suspend fun votar(
        @Body request: VotoRequest
    ): Response<VotoResponse>
    
    /**
     * GET /api/votos/mis-votos/
     * Lista todos los votos que ha emitido el usuario autenticado.
     * Requiere autenticación (JWT token)
     */
    @GET("votos/mis-votos/")
    suspend fun getMisVotos(): Response<List<Voto>>
    
    /**
     * GET /api/votos/puede-votar/{cargo_nombre}/
     * Verifica si el usuario ya votó por un cargo específico.
     * Requiere autenticación (JWT token)
     */
    @GET("votos/puede-votar/{cargo_nombre}/")
    suspend fun puedeVotar(
        @Path("cargo_nombre") cargoNombre: String
    ): Response<PuedeVotarResponse>
    
    /**
     * GET /api/votos/resultados/
     * Resultados generales de todas las elecciones.
     * No requiere autenticación
     */
    @GET("votos/resultados/")
    suspend fun getResultados(
        @Query("cargo") cargo: String? = null,
        @Query("region") region: Int? = null
    ): Response<List<ResultadoGeneral>>
    
    /**
     * GET /api/votos/resultados/por-partido/
     * Resultados agrupados por partido político.
     * No requiere autenticación
     */
    @GET("votos/resultados/por-partido/")
    suspend fun getResultadosPorPartido(
        @Query("cargo") cargo: String? = null
    ): Response<List<ResultadoPorPartido>>
    
    /**
     * GET /api/votos/estadisticas/
     * Estadísticas generales del sistema.
     * No requiere autenticación
     */
    @GET("votos/estadisticas/")
    suspend fun getEstadisticas(): Response<Estadisticas>
}

