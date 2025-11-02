package com.rivera.votainformado.data.repository

import com.rivera.votainformado.data.model.candidatos.CandidatoDetail
import com.rivera.votainformado.data.model.candidatos.CandidatosPage
import com.rivera.votainformado.data.model.candidatos.Partido
import com.rivera.votainformado.util.Resource
import com.rivera.votainformado.util.RetrofitInstance
import org.json.JSONObject

class CandidatosRepository {

    private val api = RetrofitInstance.candidatosApi

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
     * Obtiene todos los candidatos con filtros opcionales
     */
    suspend fun getCandidatos(
        page: Int? = null,
        cargo: String? = null,
        region: Int? = null,
        partido: Int? = null,
        search: String? = null
    ): Resource<CandidatosPage> {
        return try {
            val response = api.getCandidatos(page, cargo, region, partido, search)
            if (response.isSuccessful && response.body() != null) {
                Resource.Success(response.body()!!)
            } else {
                val errorMessage = parseErrorMessage(
                    response.errorBody()?.string(),
                    "Error al cargar candidatos"
                )
                Resource.Error(errorMessage)
            }
        } catch (e: Exception) {
            Resource.Error("Error de conexión. Inténtalo nuevamente.")
        }
    }

    /**
     * Obtiene todos los candidatos cargando todas las páginas disponibles
     */
    suspend fun getAllCandidatos(
        cargo: String? = null,
        region: Int? = null,
        partido: Int? = null,
        search: String? = null
    ): Resource<List<com.rivera.votainformado.data.model.candidatos.CandidatoItem>> {
        return try {
            var allCandidatos = emptyList<com.rivera.votainformado.data.model.candidatos.CandidatoItem>()
            var currentPage = 1
            var hasNextPage = true

            while (hasNextPage) {
                val response = api.getCandidatos(currentPage, cargo, region, partido, search)
                if (response.isSuccessful && response.body() != null) {
                    val page = response.body()!!
                    allCandidatos = allCandidatos + page.results
                    hasNextPage = page.next != null
                    currentPage++
                } else {
                    hasNextPage = false
                    if (currentPage == 1) {
                        // Solo retornamos error en la primera página
                        val errorMessage = parseErrorMessage(
                            response.errorBody()?.string(),
                            "Error al cargar candidatos"
                        )
                        return Resource.Error(errorMessage)
                    }
                }
            }

            Resource.Success(allCandidatos)
        } catch (e: Exception) {
            Resource.Error("Error de conexión. Inténtalo nuevamente.")
        }
    }

    /**
     * Obtiene el detalle de un candidato específico
     */
    suspend fun getCandidatoDetail(id: Int): Resource<CandidatoDetail> {
        return try {
            val response = api.getCandidatoDetail(id)
            if (response.isSuccessful && response.body() != null) {
                Resource.Success(response.body()!!)
            } else {
                val errorMessage = parseErrorMessage(
                    response.errorBody()?.string(),
                    "Error al cargar el detalle del candidato"
                )
                Resource.Error(errorMessage)
            }
        } catch (e: Exception) {
            Resource.Error("Error de conexión. Inténtalo nuevamente.")
        }
    }

    /**
     * Obtiene todos los partidos políticos
     */
    suspend fun getPartidos(): Resource<List<Partido>> {
        return try {
            val response = api.getPartidos()
            if (response.isSuccessful && response.body() != null) {
                Resource.Success(response.body()!!)
            } else {
                val errorMessage = parseErrorMessage(
                    response.errorBody()?.string(),
                    "Error al cargar partidos"
                )
                Resource.Error(errorMessage)
            }
        } catch (e: Exception) {
            Resource.Error("Error de conexión. Inténtalo nuevamente.")
        }
    }
}

