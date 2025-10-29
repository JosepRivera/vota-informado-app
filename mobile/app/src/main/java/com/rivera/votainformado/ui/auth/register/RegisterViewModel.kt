package com.rivera.votainformado.ui.auth.register

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.rivera.votainformado.data.repository.AuthRepository
import com.rivera.votainformado.util.Resource
import com.rivera.votainformado.util.TokenManager
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch

class RegisterViewModel(
    private val tokenManager: TokenManager
) : ViewModel() {
    private val repository = AuthRepository()
    private val _registerState = MutableStateFlow(RegisterState())
    val registerState: StateFlow<RegisterState> = _registerState.asStateFlow()

    init {
        loadRegiones()
    }

    fun loadRegiones() {
        viewModelScope.launch {
            _registerState.value = _registerState.value.copy(isLoadingRegiones = true)
            when (val result = repository.getRegiones()) {
                is Resource.Success -> {
                    _registerState.value = _registerState.value.copy(
                        regiones = result.data ?: emptyList(),
                        isLoadingRegiones = false
                    )
                }
                is Resource.Error -> {
                    _registerState.value = _registerState.value.copy(
                        errorMessage = "Error al cargar regiones: ${result.message}",
                        isLoadingRegiones = false
                    )
                }
                is Resource.Loading -> {}
            }
        }
    }

    fun validateDni(dni: String) {
        viewModelScope.launch {
            _registerState.value = _registerState.value.copy(
                isValidatingDni = true,
                errorMessage = null
            )
            when (val result = repository.validateDni(dni)) {
                is Resource.Success -> {
                    _registerState.value = _registerState.value.copy(
                        dniValidated = result.data,
                        isValidatingDni = false
                    )
                }
                is Resource.Error -> {
                    _registerState.value = _registerState.value.copy(
                        errorMessage = result.message ?: "DNI no encontrado",
                        isValidatingDni = false
                    )
                }
                is Resource.Loading -> {}
            }
        }
    }

    fun clearDniValidation() {
        _registerState.value = _registerState.value.copy(dniValidated = null)
    }

    fun register(dni: String, regionId: String, password: String) {
        viewModelScope.launch {
            _registerState.value = _registerState.value.copy(isLoading = true)
            when (val result = repository.register(dni, regionId, password)) {
                is Resource.Success -> {
                    result.data?.let { response ->
                        tokenManager.saveTokens(
                            accessToken = response.tokens.access,
                            refreshToken = response.tokens.refresh
                        )
                    }
                    _registerState.value = _registerState.value.copy(
                        successMessage = "Registro exitoso",
                        isLoading = false
                    )
                }
                is Resource.Error -> {
                    _registerState.value = _registerState.value.copy(
                        errorMessage = result.message,
                        isLoading = false
                    )
                }
                is Resource.Loading -> {}
            }
        }
    }
}