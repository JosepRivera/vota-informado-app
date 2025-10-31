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
                val firstResponse = RetrofitInstance.candidatosApi.getCandidatos(page = 1)
                if (firstResponse.isSuccessful) {
                    val firstPage = firstResponse.body()
                    val firstResults = firstPage?.results ?: emptyList<CandidatoItem>()

                    // Intentamos cargar una segunda página si existe
                    val combined = if (firstPage?.next != null) {
                        val secondResponse = RetrofitInstance.candidatosApi.getCandidatos(page = 2)
                        val secondResults = if (secondResponse.isSuccessful) {
                            secondResponse.body()?.results ?: emptyList()
                        } else emptyList()
                        firstResults + secondResults
                    } else firstResults

                    state.value = state.value.copy(
                        candidatos = combined,
                        isLoading = false
                    )
                } else {
                    state.value = state.value.copy(
                        errorMessage = "Error ${firstResponse.code()} al cargar candidatos",
                        isLoading = false
                    )
                }
            } catch (e: Exception) {
                state.value = state.value.copy(
                    errorMessage = e.message ?: "Error de red",
                    isLoading = false
                )
            }
        }
    }
}
