package com.rivera.votainformado.ui.comparar

import com.rivera.votainformado.data.model.candidatos.CandidatoDetail

data class CompararState(
    val isLoading: Boolean = false,
    val errorMessage: String? = null,
    val candidato1: CandidatoDetail? = null,
    val candidato2: CandidatoDetail? = null,
    val candidato1Id: Int? = null,
    val candidato2Id: Int? = null,
    val listaCandidatos: List<com.rivera.votainformado.data.model.candidatos.CandidatoItem> = emptyList(),
    val isLoadingCandidatos: Boolean = false
)

