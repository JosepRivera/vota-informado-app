package com.rivera.votainformado.ui.resultados

import com.rivera.votainformado.data.model.votos.ResultadoGeneral
import com.rivera.votainformado.data.model.votos.ResultadoPorPartido
import com.rivera.votainformado.data.model.votos.Estadisticas

data class ResultadosState(
    val isLoading: Boolean = false,
    val errorMessage: String? = null,
    val resultados: List<ResultadoGeneral> = emptyList(),
    val resultadosPorPartido: List<ResultadoPorPartido> = emptyList(),
    val estadisticas: Estadisticas? = null,
    val cargoFiltro: String? = null, // "Presidente", "Senador", "Diputado"
    val regionFiltro: Int? = null,
    val mostrarPorPartido: Boolean = false
)

