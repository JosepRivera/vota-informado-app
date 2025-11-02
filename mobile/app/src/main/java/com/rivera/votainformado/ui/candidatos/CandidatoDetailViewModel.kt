package com.rivera.votainformado.ui.candidatos

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.rivera.votainformado.util.RetrofitInstance
import kotlinx.coroutines.launch

class CandidatoDetailViewModel : ViewModel() {
    
    var state = androidx.compose.runtime.mutableStateOf(CandidatoDetailState())
        private set
    
    fun getCandidatoDetail(id: Int) {
        viewModelScope.launch {
            state.value = state.value.copy(isLoading = true, errorMessage = null)
            try {
                val response = RetrofitInstance.candidatosApi.getCandidatoDetail(id)
                if (response.isSuccessful) {
                    state.value = state.value.copy(
                        candidato = response.body(),
                        isLoading = false
                    )
                } else {
                    state.value = state.value.copy(
                        errorMessage = "Error ${response.code()} al cargar candidato",
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

data class CandidatoDetailState(
    val isLoading: Boolean = false,
    val errorMessage: String? = null,
    val candidato: com.rivera.votainformado.data.model.candidatos.CandidatoDetail? = null
)

