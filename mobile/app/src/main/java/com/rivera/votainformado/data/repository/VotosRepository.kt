package com.rivera.votainformado.data.repository

import com.rivera.votainformado.data.model.votos.*
import com.rivera.votainformado.util.Resource
import com.rivera.votainformado.util.RetrofitInstance
import org.json.JSONObject

class VotosRepository {

    private val api = RetrofitInstance.votosApi

    // Función auxiliar para leer errores JSON de forma segura
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

    /**
     * Emite un voto para un candidato
     * Requiere autenticación (JWT token)
     */
    suspend fun votar(candidatoId: Int): Resource<VotoResponse> {
        return try {
            val response = api.votar(VotoRequest(candidatoId))
            if (response.isSuccessful) {
                val body = response.body()
                if (body != null) {
                    Resource.Success(body)
                } else {
                    // Backend devuelve 201 pero sin body (raro, pero manejarlo)
                    Resource.Error("Respuesta vacía del servidor")
                }
            } else {
                val errorMessage = parseErrorMessage(
                    response.errorBody()?.string(),
                    "No se pudo registrar el voto"
                )
                Resource.Error(errorMessage)
            }
        } catch (e: Exception) {
            Resource.Error("Error de conexión: ${e.localizedMessage ?: "Inténtalo nuevamente"}")
        }
    }

    /**
     * Obtiene todos los votos que ha emitido el usuario autenticado
     * Requiere autenticación (JWT token)
     */
    suspend fun getMisVotos(): Resource<List<Voto>> {
        return try {
            val response = api.getMisVotos()
            if (response.isSuccessful) {
                // Backend siempre devuelve array, aunque sea vacío
                Resource.Success(response.body() ?: emptyList())
            } else {
                val errorMessage = parseErrorMessage(
                    response.errorBody()?.string(),
                    "Error al cargar tus votos"
                )
                Resource.Error(errorMessage)
            }
        } catch (e: Exception) {
            Resource.Error("Error de conexión: ${e.localizedMessage ?: "Inténtalo nuevamente"}")
        }
    }

    /**
     * Verifica si el usuario ya votó por un cargo específico
     * Requiere autenticación (JWT token)
     */
    suspend fun puedeVotar(cargoNombre: String): Resource<PuedeVotarResponse> {
        return try {
            val response = api.puedeVotar(cargoNombre)
            if (response.isSuccessful) {
                val body = response.body()
                if (body != null) {
                    Resource.Success(body)
                } else {
                    Resource.Error("Respuesta vacía del servidor")
                }
            } else {
                val errorMessage = parseErrorMessage(
                    response.errorBody()?.string(),
                    "Error al verificar si puedes votar"
                )
                Resource.Error(errorMessage)
            }
        } catch (e: Exception) {
            Resource.Error("Error de conexión: ${e.localizedMessage ?: "Inténtalo nuevamente"}")
        }
    }

    /**
     * Obtiene resultados generales de las elecciones
     * No requiere autenticación
     */
    suspend fun getResultados(
        cargo: String? = null,
        region: Int? = null
    ): Resource<List<ResultadoGeneral>> {
        return try {
            val response = api.getResultados(cargo, region)
            if (response.isSuccessful && response.body() != null) {
                Resource.Success(response.body()!!)
            } else {
                val errorMessage = parseErrorMessage(
                    response.errorBody()?.string(),
                    "Error al cargar resultados"
                )
                Resource.Error(errorMessage)
            }
        } catch (e: Exception) {
            Resource.Error("Error de conexión: ${e.localizedMessage ?: "Inténtalo nuevamente"}")
        }
    }

    /**
     * Obtiene resultados agrupados por partido político
     * No requiere autenticación
     */
    suspend fun getResultadosPorPartido(
        cargo: String? = null
    ): Resource<List<ResultadoPorPartido>> {
        return try {
            val response = api.getResultadosPorPartido(cargo)
            if (response.isSuccessful && response.body() != null) {
                Resource.Success(response.body()!!)
            } else {
                val errorMessage = parseErrorMessage(
                    response.errorBody()?.string(),
                    "Error al cargar resultados por partido"
                )
                Resource.Error(errorMessage)
            }
        } catch (e: Exception) {
            Resource.Error("Error de conexión: ${e.localizedMessage ?: "Inténtalo nuevamente"}")
        }
    }

    /**
     * Obtiene estadísticas generales del sistema
     * No requiere autenticación
     */
    suspend fun getEstadisticas(): Resource<Estadisticas> {
        return try {
            val response = api.getEstadisticas()
            if (response.isSuccessful && response.body() != null) {
                Resource.Success(response.body()!!)
            } else {
                val errorMessage = parseErrorMessage(
                    response.errorBody()?.string(),
                    "Error al cargar estadísticas"
                )
                Resource.Error(errorMessage)
            }
        } catch (e: Exception) {
            Resource.Error("Error de conexión: ${e.localizedMessage ?: "Inténtalo nuevamente"}")
        }
    }
}

