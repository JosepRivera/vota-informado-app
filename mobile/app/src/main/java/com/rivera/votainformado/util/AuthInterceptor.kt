package com.rivera.votainformado.util

import android.content.Context
import okhttp3.Interceptor
import okhttp3.Response

/**
 * Interceptor que agrega automáticamente el token JWT a las peticiones HTTP.
 * Lee el token desde SharedPreferences y lo agrega al header Authorization.
 */
class AuthInterceptor(private val context: Context) : Interceptor {
    
    companion object {
        private const val PREF_NAME = "auth_prefs"
        private const val KEY_ACCESS_TOKEN = "access_token"
    }

    override fun intercept(chain: Interceptor.Chain): Response {
        val originalRequest = chain.request()
        
        // Leer el token desde SharedPreferences
        val token = context.getSharedPreferences(PREF_NAME, Context.MODE_PRIVATE)
            .getString(KEY_ACCESS_TOKEN, null)

        // Si no hay token, hacer la petición sin autenticación
        val request = if (token != null) {
            originalRequest.newBuilder()
                .header("Authorization", "Bearer $token")
                .build()
        } else {
            originalRequest
        }

        return chain.proceed(request)
    }
}
