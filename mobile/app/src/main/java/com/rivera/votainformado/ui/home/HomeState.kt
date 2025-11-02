package com.rivera.votainformado.ui.home

import com.rivera.votainformado.data.model.candidatos.CandidatoItem

data class HomeState(
    val isLoading: Boolean = false,
    val errorMessage: String? = null,
    val candidatos: List<CandidatoItem> = emptyList()
)
