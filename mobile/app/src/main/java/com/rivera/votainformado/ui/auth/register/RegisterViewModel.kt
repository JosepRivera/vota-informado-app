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
    private val registerState : StateFlow<RegisterState> = _registerState.asStateFlow()

    fun register(dni : String, regionId : String, password : String){
        viewModelScope.launch {
            _registerState.value = RegisterState(isLoading = true)

            when (val result = repository.register(dni, regionId, password)) {
                is Resource.Success -> {
                    result.data?.let {
                        response -> tokenManager.saveTokens(
                            accessToken = response.tokens.access,
                            refreshToken = response.tokens.refresh
                        )
                    }

                    _registerState.value = RegisterState(successMessage = "Registrado")
                }

                is Resource.Error -> {
                    _registerState.value = RegisterState(errorMessage = result.message)
                }

                is Resource.Loading -> {
                    // No action needed
                }

            }
        }
    }
}