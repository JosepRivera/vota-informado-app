package com.rivera.votainformado.ui.votar

import com.rivera.votainformado.data.model.candidatos.CandidatoItem
import com.rivera.votainformado.data.model.core.Cargo
import com.rivera.votainformado.data.model.auth.PerfilResponse

data class VotarState(
    val isLoading: Boolean = false,
    val errorMessage: String? = null,
    val successMessage: String? = null,
    val cargoSeleccionado: String? = null, // "Presidente", "Senador", "Diputado"
    val candidatosPresidenciales: List<CandidatoItem> = emptyList(),
    val candidatosSenadores: List<CandidatoItem> = emptyList(),
    val candidatosDiputados: List<CandidatoItem> = emptyList(),
    val candidatoSeleccionado: CandidatoItem? = null,
    val mostrarConfirmacion: Boolean = false,
    val puedeVotarPresidente: Boolean = true,
    val puedeVotarSenador: Boolean = true,
    val puedeVotarDiputado: Boolean = true,
    val yaVotoPresidente: Boolean = false,
    val yaVotoSenador: Boolean = false,
    val yaVotoDiputado: Boolean = false,
    val perfil: PerfilResponse? = null,
    val searchQuery: String = ""
)

