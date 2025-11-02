package com.rivera.votainformado.data.api

import com.rivera.votainformado.data.model.candidatos.CandidatoDetail
import com.rivera.votainformado.data.model.candidatos.CandidatosPage
import com.rivera.votainformado.data.model.candidatos.Partido
import retrofit2.Response
import retrofit2.http.GET
import retrofit2.http.Path
import retrofit2.http.Query

interface CandidatosApi {
    @GET("candidatos/")
    suspend fun getCandidatos(
        @Query("page") page: Int? = null,
        @Query("cargo") cargo: String? = null,
        @Query("region") region: Int? = null,
        @Query("partido") partido: Int? = null,
        @Query("search") search: String? = null
    ): Response<CandidatosPage>
    
    @GET("candidatos/{id}/")
    suspend fun getCandidatoDetail(
        @Path("id") id: Int
    ): Response<CandidatoDetail>
    
    @GET("candidatos/partidos/")
    suspend fun getPartidos(): Response<List<Partido>>
}


