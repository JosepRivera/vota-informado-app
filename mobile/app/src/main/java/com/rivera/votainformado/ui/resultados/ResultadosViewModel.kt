package com.rivera.votainformado.ui.resultados

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.rivera.votainformado.data.repository.VotosRepository
import com.rivera.votainformado.util.Resource
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch

class ResultadosViewModel : ViewModel() {

    private val repository = VotosRepository()

    private val _resultadosState = MutableStateFlow(ResultadosState())
    val resultadosState: StateFlow<ResultadosState> = _resultadosState.asStateFlow()

    init {
        loadResultados()
        loadEstadisticas()
    }

    fun loadResultados(cargo: String? = null, region: Int? = null) {
        viewModelScope.launch {
            _resultadosState.value = _resultadosState.value.copy(
                isLoading = true,
                errorMessage = null,
                cargoFiltro = cargo,
                regionFiltro = region
            )

            when (val result = repository.getResultados(cargo, region)) {
                is Resource.Success -> {
                    _resultadosState.value = _resultadosState.value.copy(
                        resultados = result.data ?: emptyList(),
                        isLoading = false
                    )
                }
                is Resource.Error -> {
                    _resultadosState.value = _resultadosState.value.copy(
                        errorMessage = result.message,
                        isLoading = false
                    )
                }
                is Resource.Loading -> {}
            }
        }
    }

    fun loadResultadosPorPartido(cargo: String? = null) {
        viewModelScope.launch {
            _resultadosState.value = _resultadosState.value.copy(
                isLoading = true,
                mostrarPorPartido = true,
                errorMessage = null,
                cargoFiltro = cargo
            )

            when (val result = repository.getResultadosPorPartido(cargo)) {
                is Resource.Success -> {
                    _resultadosState.value = _resultadosState.value.copy(
                        resultadosPorPartido = result.data ?: emptyList(),
                        isLoading = false
                    )
                }
                is Resource.Error -> {
                    _resultadosState.value = _resultadosState.value.copy(
                        errorMessage = result.message,
                        isLoading = false
                    )
                }
                is Resource.Loading -> {}
            }
        }
    }

    fun loadEstadisticas() {
        viewModelScope.launch {
            when (val result = repository.getEstadisticas()) {
                is Resource.Success -> {
                    _resultadosState.value = _resultadosState.value.copy(
                        estadisticas = result.data
                    )
                }
                is Resource.Error -> {
                    // No mostramos error si falla estadísticas, solo si fallan resultados
                }
                is Resource.Loading -> {}
            }
        }
    }

    fun toggleVista() {
        viewModelScope.launch {
            val mostrarPorPartido = !_resultadosState.value.mostrarPorPartido
            _resultadosState.value = _resultadosState.value.copy(
                mostrarPorPartido = mostrarPorPartido
            )
            
            // Cargar datos según la vista seleccionada
            if (mostrarPorPartido) {
                loadResultadosPorPartido(_resultadosState.value.cargoFiltro)
            } else {
                loadResultados(_resultadosState.value.cargoFiltro, _resultadosState.value.regionFiltro)
            }
        }
    }
}

