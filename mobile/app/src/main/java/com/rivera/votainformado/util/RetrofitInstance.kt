package com.rivera.votainformado.util

import android.content.Context
import com.rivera.votainformado.data.api.AuthApi
import com.rivera.votainformado.data.api.CandidatosApi
import com.rivera.votainformado.data.api.VotosApi
import okhttp3.OkHttpClient
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory

object RetrofitInstance {

    private const val BASE_URL = "http://192.168.18.188:8000/api/"

    @Volatile
    private var appContext: Context? = null

    fun init(context: Context) {
        appContext = context.applicationContext
    }

    private val okHttpClient: OkHttpClient by lazy {
        val context = appContext ?: throw IllegalStateException(
            "RetrofitInstance no inicializado. Llama a RetrofitInstance.init(context) primero."
        )
        OkHttpClient.Builder()
            .addInterceptor(AuthInterceptor(context))
            .build()
    }

    private val retrofit: Retrofit by lazy {
        Retrofit.Builder()
            .baseUrl(BASE_URL)
            .client(okHttpClient)
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