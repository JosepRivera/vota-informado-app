package com.rivera.votainformado.util

import com.rivera.votainformado.data.api.AuthApi
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory

// object -> Define un SINGLETON: una única instancia global en toda la app
object RetrofitInstance {

    // URL base del backend (API REST)
    private const val BASE_URL = "http://192.168.18.188:8080/api/"

    // by lazy -> Crea el objeto solo la primera vez que se usa y lo reutiliza después.
    val authApi: AuthApi by lazy {
        Retrofit.Builder() // Se crea una instancia de Retrofit
            .baseUrl(BASE_URL) // Se indica la URL base
            .addConverterFactory(GsonConverterFactory.create()) // Se indica el convertidor de JSON a objetos Kotlin
            .build() // Se construye la instancia
            .create(AuthApi::class.java) // Crea una implementación de la interfaz AuthApi
    }
}