package com.rivera.votainformado.data.repository

import com.rivera.votainformado.data.model.auth.Region
import com.rivera.votainformado.data.model.auth.*
import com.rivera.votainformado.util.Resource
import com.rivera.votainformado.util.RetrofitInstance
import org.json.JSONObject

class AuthRepository {

    private val api = RetrofitInstance.authApi

    // --- Función auxiliar para leer errores JSON de forma segura ---
    private fun parseErrorMessage(errorBody: String?, fallback: String): String {
        return try {
            val json = JSONObject(errorBody ?: "{}")
            val msg = json.optString("error").ifBlank {
                json.optString("message").ifBlank { fallback }
            }
            msg
        } catch (_: Exception) {
            fallback
        }
    }

    suspend fun login(dni: String, password: String): Resource<AuthResponse> {
        return try {
            val response = api.login(LoginRequest(dni, password))
            if (response.isSuccessful && response.body() != null) {
                Resource.Success(response.body()!!)
            } else {
                val errorMessage = parseErrorMessage(
                    response.errorBody()?.string(),
                    "Credenciales inválidas o usuario no encontrado"
                )
                Resource.Error(errorMessage)
            }
        } catch (e: Exception) {
            Resource.Error("Error de conexión. Inténtalo nuevamente.")
        }
    }

    suspend fun register(dni: String, regionId: String, password: String): Resource<AuthResponse> {
        return try {
            val response = api.register(RegisterRequest(dni, regionId, password))
            if (response.isSuccessful && response.body() != null) {
                Resource.Success(response.body()!!)
            } else {
                val errorMessage = parseErrorMessage(
                    response.errorBody()?.string(),
                    "No se pudo completar el registro"
                )
                Resource.Error(errorMessage)
            }
        } catch (e: Exception) {
            Resource.Error("Error de conexión. Inténtalo nuevamente.")
        }
    }

    suspend fun validateDni(dni: String): Resource<DniValidationResponse> {
        return try {
            val response = api.validateDni(DniValidationRequest(dni))
            if (response.isSuccessful && response.body() != null) {
                Resource.Success(response.body()!!)
            } else {
                val errorMessage = parseErrorMessage(
                    response.errorBody()?.string(),
                    "DNI no encontrado o inválido"
                )
                Resource.Error(errorMessage)
            }
        } catch (e: Exception) {
            Resource.Error("Error de conexión. Inténtalo nuevamente.")
        }
    }

    suspend fun getRegiones(): Resource<List<Region>> {
        return try {
            val response = api.getRegiones()
            if (response.isSuccessful && response.body() != null) {
                Resource.Success(response.body()!!)
            } else {
                val errorMessage = parseErrorMessage(
                    response.errorBody()?.string(),
                    "No se pudieron cargar las regiones"
                )
                Resource.Error(errorMessage)
            }
        } catch (e: Exception) {
            Resource.Error("Error de conexión. Inténtalo nuevamente.")
        }
    }
}
