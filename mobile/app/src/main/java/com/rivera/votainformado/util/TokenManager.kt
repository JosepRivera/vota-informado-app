package com.rivera.votainformado.util

import android.content.Context
import androidx.core.content.edit

/**
 * Clase encargada de manejar el almacenamiento local de tokens de autenticación.
 * Usa SharedPreferences para guardar y recuperar los tokens de acceso y refresco.
 *
 * @param context Contexto de la aplicación, necesario para acceder a SharedPreferences.
 */
class TokenManager(private val context: Context) {

    /**
     * Crea o accede al archivo de preferencias llamado "auth_prefs".
     * MODE_PRIVATE significa que solo esta aplicación puede leerlo.
     */
    private val prefs = context.getSharedPreferences("auth_prefs", Context.MODE_PRIVATE)

    companion object {
        /** Clave para guardar el token de acceso (access token). */
        private const val KEY_ACCESS_TOKEN = "access_token"

        /** Clave para guardar el token de actualización (refresh token). */
        private const val KEY_REFRESH_TOKEN = "refresh_token"
    }

    /**
     * Guarda los tokens en SharedPreferences.
     *
     * @param accessToken El token de acceso (obligatorio).
     * @param refreshToken El token de actualización (opcional, puede ser null).
     */
    fun saveTokens(accessToken: String, refreshToken: String?) {
        prefs.edit().apply {
            putString(KEY_ACCESS_TOKEN, accessToken)
            // Solo guarda el refreshToken si no es nulo
            refreshToken?.let { putString(KEY_REFRESH_TOKEN, it) }
            // Aplica los cambios en segundo plano (sin bloquear el hilo principal)
            apply()
        }
    }

    /**
     * Obtiene el token de acceso guardado.
     *
     * @return El token de acceso, o null si no existe.
     */
    fun getAccessToken(): String? {
        return prefs.getString(KEY_ACCESS_TOKEN, null)
    }

    /**
     * Obtiene el token de actualización guardado.
     *
     * @return El token de actualización, o null si no existe.
     */
    fun getRefreshToken(): String? {
        return prefs.getString(KEY_REFRESH_TOKEN, null)
    }

    /**
     * Elimina todos los tokens almacenados.
     * Se usa al cerrar sesión o limpiar datos de usuario.
     */
    fun clearTokens() {
        prefs.edit { clear() }
    }
}