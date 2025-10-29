package com.rivera.votainformado.data.repository

import com.rivera.votainformado.data.model.Region
import com.rivera.votainformado.data.model.auth.AuthResponse
import com.rivera.votainformado.data.model.auth.DniValidationRequest
import com.rivera.votainformado.data.model.auth.DniValidationResponse
import com.rivera.votainformado.data.model.auth.LoginRequest
import com.rivera.votainformado.data.model.auth.RegisterRequest
import com.rivera.votainformado.util.Resource
import com.rivera.votainformado.util.RetrofitInstance

class AuthRepository {
    /**
     * Instancia del API de autenticación usando Retrofit.
     */
    private val api = RetrofitInstance.authApi
    /**
     * Función para iniciar sesión.
     * @param dni dni del usuario
     * @param password contraseña del usuario
     * @return Resource<LoginResponse> que puede ser Success o Error
     */
    suspend fun login(dni: String, password: String) : Resource<AuthResponse> {
        return try {
            /**
             * Llamada al endpoint de login enviando los datos en un LoginRequest
             */
            val response = api.login(LoginRequest(dni, password))

            // Verifica si la respuesta fue exitosa y contiene datos
            if (response.isSuccessful && response.body() != null) {
                Resource.Success(response.body()!!)
            } else {
                Resource.Error(response.message() ?: "Error desconocido")
            }

        } catch (e: Exception){
            // Captura errores de conexión u otros fallos en la llamada HTTP
            Resource.Error("Error de conexión: ${e.message}")
        }
    }

    suspend fun register(dni: String, regionId: String , password: String) : Resource<AuthResponse> {
        return try {
            val response = api.register(RegisterRequest(dni, regionId, password))

            if (response.isSuccessful && response.body() != null) {
                Resource.Success(response.body()!!)
            } else {
                Resource.Error(response.message() ?: "Error desconocido")
            }
        } catch (e: Exception){
            Resource.Error("Error de conexión: ${e.message}")
        }
    }

    suspend fun validateDni(dni: String): Resource<DniValidationResponse> {
        return try {
            val response = api.validateDni(DniValidationRequest(dni))
            if (response.isSuccessful && response.body() != null) {
                Resource.Success(response.body()!!)
            } else {
                Resource.Error(response.message() ?: "DNI no encontrado")
            }
        } catch (e: Exception) {
            Resource.Error("Error de conexión: ${e.message}")
        }
    }
    suspend fun getRegiones(): Resource<List<Region>> {
        return try {
            val response = api.getRegiones()
            if (response.isSuccessful && response.body() != null) {
                Resource.Success(response.body()!!)
            } else {
                Resource.Error(response.message() ?: "Error al cargar regiones")
            }
        } catch (e: Exception) {
            Resource.Error("Error de conexión: ${e.message}")
        }
    }
}