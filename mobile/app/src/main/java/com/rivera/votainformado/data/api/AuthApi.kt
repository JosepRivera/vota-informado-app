package com.rivera.votainformado.data.api

import com.rivera.votainformado.data.model.Region
import com.rivera.votainformado.data.model.auth.AuthResponse
import com.rivera.votainformado.data.model.auth.DniValidationRequest
import com.rivera.votainformado.data.model.auth.DniValidationResponse
import com.rivera.votainformado.data.model.auth.LoginRequest
import com.rivera.votainformado.data.model.auth.RegisterRequest
import retrofit2.Response
import retrofit2.http.Body
import retrofit2.http.GET
import retrofit2.http.POST
import retrofit2.http.Query

/** interface -> Define un contrato, declaramos que métodos existen pero no como se implementan.
En esta caso define los endpoints pero sin implementarlos.*/
interface AuthApi {
    @POST("usuarios/login/")
    /**suspend -> Función que puede ejecutarse en una coroutine (de forma asíncrona).
    La app no se bloquea mientras espera la respuesta del servidor.*/
    suspend fun login(
        /**El cuerpo (body) de la petición será un objeto LoginRequest, que Retrofit convertirá automáticamente a JSON antes de enviarlo al servidor.*/
        @Body request: LoginRequest
    ): Response<AuthResponse> //La respuesta del servidor se convertirá en LoginResponse

    @POST("usuarios/registro/")
    suspend fun register(
        @Body requestBody: RegisterRequest
    ): Response<AuthResponse>

    @POST("usuarios/validar-dni/")
    suspend fun validateDni(
        @Body request: DniValidationRequest
    ): Response<DniValidationResponse>

    @GET("usuarios/regiones/")
    suspend fun getRegiones(): Response<List<Region>>
}