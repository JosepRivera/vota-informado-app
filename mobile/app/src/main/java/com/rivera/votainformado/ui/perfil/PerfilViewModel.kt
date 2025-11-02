package com.rivera.votainformado.ui.perfil

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.rivera.votainformado.data.repository.AuthRepository
import com.rivera.votainformado.data.repository.VotosRepository
import com.rivera.votainformado.util.Resource
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch

class PerfilViewModel : ViewModel() {

    private val authRepository = AuthRepository()
    private val votosRepository = VotosRepository()

    private val _perfilState = MutableStateFlow(PerfilState())
    val perfilState: StateFlow<PerfilState> = _perfilState.asStateFlow()

    init {
        loadPerfil()
        loadMisVotos()
    }

    fun loadPerfil() {
        viewModelScope.launch {
            _perfilState.value = _perfilState.value.copy(isLoading = true, errorMessage = null)

            when (val result = authRepository.getPerfil()) {
                is Resource.Success -> {
                    _perfilState.value = _perfilState.value.copy(
                        perfil = result.data,
                        isLoading = false
                    )
                }
                is Resource.Error -> {
                    _perfilState.value = _perfilState.value.copy(
                        errorMessage = result.message,
                        isLoading = false
                    )
                }
                is Resource.Loading -> {}
            }
        }
    }

    fun loadMisVotos() {
        viewModelScope.launch {
            _perfilState.value = _perfilState.value.copy(isLoadingVotos = true)

            when (val result = votosRepository.getMisVotos()) {
                is Resource.Success -> {
                    _perfilState.value = _perfilState.value.copy(
                        misVotos = result.data ?: emptyList(),
                        isLoadingVotos = false
                    )
                }
                is Resource.Error -> {
                    _perfilState.value = _perfilState.value.copy(
                        isLoadingVotos = false
                    )
                }
                is Resource.Loading -> {}
            }
        }
    }

    fun refrescar() {
        loadPerfil()
        loadMisVotos()
    }
}

