package com.rivera.votainformado.ui.votar

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.rivera.votainformado.data.repository.AuthRepository
import com.rivera.votainformado.data.repository.CandidatosRepository
import com.rivera.votainformado.data.repository.VotosRepository
import com.rivera.votainformado.util.Resource
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch

class VotarViewModel : ViewModel() {

    private val candidatosRepository = CandidatosRepository()
    private val votosRepository = VotosRepository()
    private val authRepository = AuthRepository()

    private val _votarState = MutableStateFlow(VotarState())
    val votarState: StateFlow<VotarState> = _votarState.asStateFlow()

    init {
        loadCandidatos()
        viewModelScope.launch {
            verificarEstadoVotos()
        }
        loadPerfil()
    }

    fun loadPerfil() {
        viewModelScope.launch {
            when (val result = authRepository.getPerfil()) {
                is Resource.Success -> {
                    _votarState.value = _votarState.value.copy(perfil = result.data)
                }
                else -> {}
            }
        }
    }

    fun setSearchQuery(query: String) {
        _votarState.value = _votarState.value.copy(searchQuery = query)
    }

    fun loadCandidatos() {
        viewModelScope.launch {
            _votarState.value = _votarState.value.copy(isLoading = true)

            // Cargar candidatos presidenciales
            when (val result = candidatosRepository.getAllCandidatos(cargo = "Presidente")) {
                is Resource.Success -> {
                    _votarState.value = _votarState.value.copy(
                        candidatosPresidenciales = result.data ?: emptyList()
                    )
                }
                is Resource.Error -> {
                    _votarState.value = _votarState.value.copy(
                        errorMessage = result.message,
                        isLoading = false
                    )
                    return@launch
                }
                is Resource.Loading -> {}
            }

            // Cargar candidatos al senado
            when (val result = candidatosRepository.getAllCandidatos(cargo = "Senador")) {
                is Resource.Success -> {
                    _votarState.value = _votarState.value.copy(
                        candidatosSenadores = result.data ?: emptyList()
                    )
                }
                is Resource.Error -> {}
                is Resource.Loading -> {}
            }

            // Cargar candidatos a diputados
            when (val result = candidatosRepository.getAllCandidatos(cargo = "Diputado")) {
                is Resource.Success -> {
                    _votarState.value = _votarState.value.copy(
                        candidatosDiputados = result.data ?: emptyList(),
                        isLoading = false
                    )
                }
                is Resource.Error -> {
                    _votarState.value = _votarState.value.copy(isLoading = false)
                }
                is Resource.Loading -> {}
            }
        }
    }

    suspend fun verificarEstadoVotos() {
        // Verificar si puede votar por cada cargo
        when (val result = votosRepository.puedeVotar("Presidente")) {
            is Resource.Success -> {
                result.data?.let {
                    _votarState.value = _votarState.value.copy(
                        puedeVotarPresidente = it.puedeVotar,
                        yaVotoPresidente = it.yaVoto
                    )
                }
            }
            else -> {}
        }

        when (val result = votosRepository.puedeVotar("Senador")) {
            is Resource.Success -> {
                result.data?.let {
                    _votarState.value = _votarState.value.copy(
                        puedeVotarSenador = it.puedeVotar,
                        yaVotoSenador = it.yaVoto
                    )
                }
            }
            else -> {}
        }

        when (val result = votosRepository.puedeVotar("Diputado")) {
            is Resource.Success -> {
                result.data?.let {
                    _votarState.value = _votarState.value.copy(
                        puedeVotarDiputado = it.puedeVotar,
                        yaVotoDiputado = it.yaVoto
                    )
                }
            }
            else -> {}
        }
    }

    fun seleccionarCandidato(candidato: com.rivera.votainformado.data.model.candidatos.CandidatoItem) {
        _votarState.value = _votarState.value.copy(
            candidatoSeleccionado = candidato,
            mostrarConfirmacion = true,
            errorMessage = null // Limpiar cualquier error previo
        )
    }

    fun confirmarVoto() {
        viewModelScope.launch {
            val candidato = _votarState.value.candidatoSeleccionado ?: return@launch

            _votarState.value = _votarState.value.copy(
                isLoading = true,
                errorMessage = null,
                successMessage = null
            )

            when (val result = votosRepository.votar(candidato.id)) {
                is Resource.Success -> {
                    // Pequeña pausa para que el backend procese
                    kotlinx.coroutines.delay(800)

                    // Actualizar estado de votos PRIMERO y ESPERAR
                    verificarEstadoVotos()

                    // Pequeña pausa para asegurar que el estado se actualizó
                    kotlinx.coroutines.delay(500)
                    launch { loadCandidatos() }
                    kotlinx.coroutines.delay(800)
                    _votarState.value = _votarState.value.copy(
                        successMessage = result.data?.message ?: "Voto registrado exitosamente",
                        mostrarConfirmacion = false,
                        candidatoSeleccionado = null,
                        isLoading = false,
                        errorMessage = null // GARANTIZAR que no hay error
                    )
                }
                is Resource.Error -> {
                    _votarState.value = _votarState.value.copy(
                        errorMessage = result.message,
                        mostrarConfirmacion = false,
                        isLoading = false,
                        successMessage = null
                    )
                }
                is Resource.Loading -> {}
            }
        }
    }

    fun cancelarConfirmacion() {
        _votarState.value = _votarState.value.copy(
            mostrarConfirmacion = false,
            candidatoSeleccionado = null
        )
    }

    fun seleccionarCargo(cargo: String) {
        _votarState.value = _votarState.value.copy(cargoSeleccionado = cargo)
    }

    fun limpiarMensajeExito() {
        _votarState.value = _votarState.value.copy(successMessage = null)
    }
    
    fun limpiarError() {
        _votarState.value = _votarState.value.copy(errorMessage = null)
    }
}

