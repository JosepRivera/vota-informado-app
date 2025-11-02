package com.rivera.votainformado.ui.home

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.rivera.votainformado.data.model.candidatos.CandidatoItem
import com.rivera.votainformado.util.RetrofitInstance
import kotlinx.coroutines.launch

class HomeViewModel : ViewModel() {

    var state = androidx.compose.runtime.mutableStateOf(HomeState())
        private set

    init {
        getCandidatos()
    }

    fun getCandidatos() {
        viewModelScope.launch {
            state.value = state.value.copy(isLoading = true, errorMessage = null)
            try {
                var allCandidatos = emptyList<CandidatoItem>()
                var currentPage = 1
                var hasNextPage = true

                // Cargar todas las páginas disponibles
                while (hasNextPage) {
                    val response = RetrofitInstance.candidatosApi.getCandidatos(page = currentPage)
                    if (response.isSuccessful) {
                        val page = response.body()
                        val pageResults = page?.results ?: emptyList<CandidatoItem>()
                        allCandidatos = allCandidatos + pageResults

                        // Verificar si hay más páginas
                        hasNextPage = page?.next != null
                        currentPage++
                    } else {
                        hasNextPage = false
                    }
                }

                state.value = state.value.copy(
                    candidatos = allCandidatos,
                    isLoading = false
                )
            } catch (e: Exception) {
                state.value = state.value.copy(
                    errorMessage = e.message ?: "Error de red",
                    isLoading = false
                )
            }
        }
    }
}
