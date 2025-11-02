package com.rivera.votainformado.ui.comparar

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.rivera.votainformado.data.repository.CandidatosRepository
import com.rivera.votainformado.util.Resource
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch

class CompararViewModel : ViewModel() {

    private val repository = CandidatosRepository()

    private val _compararState = MutableStateFlow(CompararState())
    val compararState: StateFlow<CompararState> = _compararState.asStateFlow()

    init {
        cargarListaCandidatos()
    }

    fun cargarListaCandidatos() {
        viewModelScope.launch {
            _compararState.value = _compararState.value.copy(isLoadingCandidatos = true)
            when (val result = repository.getAllCandidatos()) {
                is Resource.Success -> {
                    _compararState.value = _compararState.value.copy(
                        listaCandidatos = result.data ?: emptyList(),
                        isLoadingCandidatos = false
                    )
                }
                is Resource.Error -> {
                    _compararState.value = _compararState.value.copy(isLoadingCandidatos = false)
                }
                is Resource.Loading -> {}
            }
        }
    }

    fun cargarCandidato1(id: Int) {
        viewModelScope.launch {
            _compararState.value = _compararState.value.copy(
                isLoading = true,
                errorMessage = null,
                candidato1Id = id
            )

            when (val result = repository.getCandidatoDetail(id)) {
                is Resource.Success -> {
                    _compararState.value = _compararState.value.copy(
                        candidato1 = result.data,
                        isLoading = _compararState.value.candidato2 == null
                    )
                }
                is Resource.Error -> {
                    _compararState.value = _compararState.value.copy(
                        errorMessage = result.message,
                        isLoading = false
                    )
                }
                is Resource.Loading -> {}
            }
        }
    }

    fun cargarCandidato2(id: Int) {
        viewModelScope.launch {
            _compararState.value = _compararState.value.copy(
                isLoading = true,
                errorMessage = null,
                candidato2Id = id
            )

            when (val result = repository.getCandidatoDetail(id)) {
                is Resource.Success -> {
                    _compararState.value = _compararState.value.copy(
                        candidato2 = result.data,
                        isLoading = _compararState.value.candidato1 == null
                    )
                }
                is Resource.Error -> {
                    _compararState.value = _compararState.value.copy(
                        errorMessage = result.message,
                        isLoading = false
                    )
                }
                is Resource.Loading -> {}
            }
        }
    }

    fun limpiarComparacion() {
        _compararState.value = CompararState()
    }

    fun limpiarCandidato1() {
        _compararState.value = _compararState.value.copy(candidato1 = null, candidato1Id = null)
    }

    fun limpiarCandidato2() {
        _compararState.value = _compararState.value.copy(candidato2 = null, candidato2Id = null)
    }
}

