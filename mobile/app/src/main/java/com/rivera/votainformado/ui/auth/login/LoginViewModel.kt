package com.rivera.votainformado.ui.auth.login

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.rivera.votainformado.data.repository.AuthRepository
import com.rivera.votainformado.util.Resource
import com.rivera.votainformado.util.TokenManager
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch

/**
 * ViewModel responsable de gestionar el flujo de autenticación (login).
 *
 * 🔹 Se comunica con el [AuthRepository] para realizar la solicitud de inicio de sesión.
 * 🔹 Usa [TokenManager] para guardar los tokens obtenidos del servidor.
 * 🔹 Expone un [StateFlow] observable que notifica a la UI los cambios de estado (cargando, éxito, error).
 *
 * @property tokenManager Maneja el almacenamiento local de los tokens de sesión.
 */
class LoginViewModel(
    private val tokenManager: TokenManager
) : ViewModel() {

    /** Repositorio que realiza la llamada a la API de autenticación. */
    private val repository = AuthRepository()

    /**
     * Estado interno y mutable del login.
     * Solo el ViewModel puede modificarlo directamente.
     */
    private val _loginState = MutableStateFlow(LoginState())

    /**
     * Estado público de solo lectura.
     * La UI (por ejemplo, una pantalla de Compose) puede observarlo, pero no modificarlo.
     */
    val loginState: StateFlow<LoginState> = _loginState.asStateFlow()

    /**
     * Función principal para iniciar sesión con email y contraseña.
     *
     * 🔸 Cambia el estado a "cargando".
     * 🔸 Llama al repositorio para hacer la solicitud.
     * 🔸 Guarda los tokens si el login es exitoso.
     * 🔸 Actualiza el estado según el resultado (éxito o error).
     *
     * @param dni DNI del usuario.
     * @param password Contraseña del usuario.
     */
    fun login(dni: String, password: String) {
        // viewModelScope asegura que la coroutine se cancele automáticamente si el ViewModel se destruye (por ejemplo, al cerrar la pantalla).
        viewModelScope.launch {

            // Indicamos que la app está procesando el inicio de sesión
            _loginState.value = LoginState(isLoading = true)

            // Llamamos al repositorio y manejamos el resultado
            when (val result = repository.login(dni, password)) {

                is Resource.Success -> {
                    /**
                     * Si el login fue exitoso:
                     * - Guardamos los tokens para futuras peticiones
                     * - Actualizamos el estado con un mensaje de éxito
                     */
                    result.data?.let { response ->
                        tokenManager.saveTokens(
                            accessToken = response.tokens.access,
                            refreshToken = response.tokens.refresh
                        )
                    }

                    _loginState.value = LoginState(
                        successMessage = "¡Bienvenido!"
                    )
                }

                is Resource.Error -> {
                    _loginState.value = LoginState(
                        errorMessage = result.message ?: "Error desconocido"
                    )
                }

                is Resource.Loading -> {
                    // (Opcional) Si el repositorio manejara un estado intermedio de carga
                }
            }
        }
    }
}