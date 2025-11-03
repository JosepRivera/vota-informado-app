package com.rivera.votainformado.util

import android.content.Context
import com.rivera.votainformado.data.api.AuthApi
import com.rivera.votainformado.data.api.CandidatosApi
import com.rivera.votainformado.data.api.VotosApi
import okhttp3.OkHttpClient
import okhttp3.logging.HttpLoggingInterceptor
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import java.util.concurrent.TimeUnit

object RetrofitInstance {

    private const val BASE_URL = "http://192.168.18.188:8000/api/"
    private const val CONNECT_TIMEOUT = 30L // segundos
    private const val READ_TIMEOUT = 30L // segundos
    private const val WRITE_TIMEOUT = 30L // segundos

    @Volatile
    private var appContext: Context? = null

    fun init(context: Context) {
        appContext = context.applicationContext
    }

    private val okHttpClient: OkHttpClient by lazy {
        val context = appContext ?: throw IllegalStateException(
            "RetrofitInstance no inicializado. Llama a RetrofitInstance.init(context) primero."
        )
        
        val loggingInterceptor = HttpLoggingInterceptor().apply {
            level = HttpLoggingInterceptor.Level.BODY
        }
        
        OkHttpClient.Builder()
            .addInterceptor(AuthInterceptor(context))
            .addInterceptor(loggingInterceptor)
            .connectTimeout(CONNECT_TIMEOUT, TimeUnit.SECONDS)
            .readTimeout(READ_TIMEOUT, TimeUnit.SECONDS)
            .writeTimeout(WRITE_TIMEOUT, TimeUnit.SECONDS)
            .retryOnConnectionFailure(true)
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