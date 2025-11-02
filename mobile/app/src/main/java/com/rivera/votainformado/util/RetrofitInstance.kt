package com.rivera.votainformado.util

import com.rivera.votainformado.data.api.AuthApi
import com.rivera.votainformado.data.api.CandidatosApi
import com.rivera.votainformado.data.api.VotosApi
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory

object RetrofitInstance {

    private const val BASE_URL = "http://192.168.18.188:8000/api/"

    private val retrofit: Retrofit by lazy {
        Retrofit.Builder()
            .baseUrl(BASE_URL)
            .addConverterFactory(GsonConverterFactory.create())
            .build()
    }

    val authApi: AuthApi by lazy {
        retrofit.create(AuthApi::class.java)
    }

    val candidatosApi: CandidatosApi by lazy {
        retrofit.create(CandidatosApi::class.java)
    }

    val votosApi: VotosApi by lazy {
        retrofit.create(VotosApi::class.java)
    }
}