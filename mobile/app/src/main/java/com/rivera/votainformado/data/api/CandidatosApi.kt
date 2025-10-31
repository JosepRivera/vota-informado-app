package com.rivera.votainformado.data.api

import com.rivera.votainformado.data.model.candidatos.CandidatosPage
import retrofit2.Response
import retrofit2.http.GET
import retrofit2.http.Query

interface CandidatosApi {
    @GET("candidatos/")
    suspend fun getCandidatos(
        @Query("page") page: Int? = null
    ): Response<CandidatosPage>
}


