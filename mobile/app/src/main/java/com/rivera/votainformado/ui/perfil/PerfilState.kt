package com.rivera.votainformado.ui.perfil

import com.rivera.votainformado.data.model.auth.PerfilResponse
import com.rivera.votainformado.data.model.votos.Voto

data class PerfilState(
    val isLoading: Boolean = false,
    val errorMessage: String? = null,
    val perfil: PerfilResponse? = null,
    val misVotos: List<Voto> = emptyList(),
    val isLoadingVotos: Boolean = false
)

