package com.rivera.votainformado.ui.resultados

import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import coil.compose.rememberAsyncImagePainter
import com.rivera.votainformado.R
import com.rivera.votainformado.ui.theme.*

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ResultadosScreen(
    onBack: () -> Unit = {}
) {
    val isDarkMode = isSystemInDarkTheme()
    val viewModel: ResultadosViewModel = viewModel()
    val state by viewModel.resultadosState.collectAsState()

    Scaffold(
        topBar = {
            TopAppBar(
                title = {
                    Text(
                        text = "Resultados",
                        style = MaterialTheme.typography.titleLarge.copy(
                            fontWeight = FontWeight.Bold,
                            color = if (isDarkMode) InstitutionalBlueLight else InstitutionalBlue
                        )
                    )
                },
                navigationIcon = {
                    IconButton(onClick = onBack) {
                        Icon(
                            imageVector = Icons.Default.ArrowBack,
                            contentDescription = "Volver",
                            tint = if (isDarkMode) InstitutionalBlueLight else InstitutionalBlue
                        )
                    }
                },
                actions = {
                    IconButton(
                        onClick = { viewModel.toggleVista() }
                    ) {
                        Icon(
                            imageVector = if (state.mostrarPorPartido) Icons.Default.Person else Icons.Default.Group,
                            contentDescription = "Cambiar vista",
                            tint = if (isDarkMode) InstitutionalBlueLight else InstitutionalBlue
                        )
                    }
                },
                colors = TopAppBarDefaults.topAppBarColors(
                    containerColor = MaterialTheme.colorScheme.surface
                )
            )
        }
    ) { innerPadding ->
        when {
            state.isLoading && state.resultados.isEmpty() && state.resultadosPorPartido.isEmpty() -> {
                Box(
                    modifier = Modifier
                        .fillMaxSize()
                        .padding(innerPadding),
                    contentAlignment = Alignment.Center
                ) {
                    Column(
                        horizontalAlignment = Alignment.CenterHorizontally,
                        verticalArrangement = Arrangement.spacedBy(16.dp)
                    ) {
                        CircularProgressIndicator(
                            color = if (isDarkMode) InstitutionalBlueLight else InstitutionalBlue,
                            modifier = Modifier.size(48.dp)
                        )
                        Text(
                            text = "Cargando resultados...",
                            style = MaterialTheme.typography.bodyMedium,
                            color = if (isDarkMode) NeutralGray else NeutralMedium
                        )
                    }
                }
            }
            state.errorMessage != null && state.resultados.isEmpty() && state.resultadosPorPartido.isEmpty() -> {
                Box(
                    modifier = Modifier
                        .fillMaxSize()
                        .padding(innerPadding),
                    contentAlignment = Alignment.Center
                ) {
                    Column(
                        horizontalAlignment = Alignment.CenterHorizontally,
                        verticalArrangement = Arrangement.spacedBy(16.dp),
                        modifier = Modifier.padding(24.dp)
                    ) {
                        Icon(
                            imageVector = Icons.Default.Error,
                            contentDescription = null,
                            tint = ErrorRed,
                            modifier = Modifier.size(48.dp)
                        )
                        Text(
                            text = state.errorMessage ?: "Error al cargar resultados",
                            style = MaterialTheme.typography.bodyLarge,
                            color = ErrorRed
                        )
                        Button(
                            onClick = { viewModel.loadResultados() },
                            colors = ButtonDefaults.buttonColors(
                                containerColor = if (isDarkMode) InstitutionalBlueLight else InstitutionalBlue
                            )
                        ) {
                            Text("Reintentar")
                        }
                    }
                }
            }
            else -> {
                LazyColumn(
                    modifier = Modifier
                        .fillMaxSize()
                        .padding(innerPadding)
                        .background(MaterialTheme.colorScheme.background),
                    contentPadding = PaddingValues(20.dp),
                    verticalArrangement = Arrangement.spacedBy(24.dp)
                ) {
                    // Estadísticas generales
                    state.estadisticas?.let { estadisticas ->
                        item {
                            EstadisticasGenerales(
                                estadisticas = estadisticas,
                                isDarkMode = isDarkMode
                            )
                        }
                    }

                    // Filtros
                    item {
                        FiltrosCargo(
                            cargoSeleccionado = state.cargoFiltro,
                            onCargoSeleccionado = { cargo ->
                                if (state.mostrarPorPartido) {
                                    viewModel.loadResultadosPorPartido(cargo)
                                } else {
                                    viewModel.loadResultados(cargo, state.regionFiltro)
                                }
                            },
                            isDarkMode = isDarkMode
                        )
                    }

                    // Título de vista
                    item {
                        Text(
                            text = if (state.mostrarPorPartido) {
                                "Resultados por Partido"
                            } else {
                                "Resultados por Candidato"
                            },
                            style = MaterialTheme.typography.titleLarge.copy(
                                fontWeight = FontWeight.Bold,
                                color = if (isDarkMode) InstitutionalBlueLight else InstitutionalBlue
                            )
                        )
                    }

                    // Resultados por candidato
                    if (!state.mostrarPorPartido) {
                        if (state.resultados.isEmpty()) {
                            item {
                                EmptyResultsCard(isDarkMode = isDarkMode)
                            }
                        } else {
                            items(state.resultados) { resultado ->
                                ResultadoCandidatoCard(
                                    resultado = resultado,
                                    maxVotos = state.resultados.maxOfOrNull { it.totalVotos } ?: 1,
                                    isDarkMode = isDarkMode
                                )
                            }
                        }
                    } else {
                        // Resultados por partido
                        if (state.resultadosPorPartido.isEmpty()) {
                            item {
                                EmptyResultsCard(isDarkMode = isDarkMode)
                            }
                        } else {
                            items(state.resultadosPorPartido) { resultado ->
                                ResultadoPartidoCard(
                                    resultado = resultado,
                                    maxVotos = state.resultadosPorPartido.maxOfOrNull { it.totalVotos } ?: 1,
                                    isDarkMode = isDarkMode
                                )
                            }
                        }
                    }

                    // Espaciado final
                    item {
                        Spacer(modifier = Modifier.height(16.dp))
                    }
                }
            }
        }
    }
}

@Composable
fun EstadisticasGenerales(
    estadisticas: com.rivera.votainformado.data.model.votos.Estadisticas,
    isDarkMode: Boolean
) {
    Card(
        modifier = Modifier.fillMaxWidth(),
        shape = RoundedCornerShape(20.dp),
        colors = CardDefaults.cardColors(
            containerColor = if (isDarkMode) DarkSurfVar else InstitutionalBlue.copy(alpha = 0.1f)
        ),
        elevation = CardDefaults.cardElevation(defaultElevation = 4.dp)
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(20.dp),
            verticalArrangement = Arrangement.spacedBy(16.dp)
        ) {
            Text(
                text = "Estadísticas Generales",
                style = MaterialTheme.typography.titleMedium.copy(
                    fontWeight = FontWeight.Bold,
                    color = if (isDarkMode) InstitutionalBlueLight else InstitutionalBlue
                )
            )

            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceEvenly
            ) {
                StatBox(
                    icon = Icons.Default.HowToVote,
                    label = "Total Votos",
                    value = estadisticas.totalVotos.toString(),
                    isDarkMode = isDarkMode
                )
                StatBox(
                    icon = Icons.Default.People,
                    label = "Votantes",
                    value = estadisticas.totalVotantes.toString(),
                    isDarkMode = isDarkMode
                )
                StatBox(
                    icon = Icons.Default.Person,
                    label = "Candidatos",
                    value = estadisticas.totalCandidatos.toString(),
                    isDarkMode = isDarkMode
                )
            }
        }
    }
}

@Composable
fun StatBox(
    icon: androidx.compose.ui.graphics.vector.ImageVector,
    label: String,
    value: String,
    isDarkMode: Boolean
) {
    Column(
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.spacedBy(8.dp)
    ) {
        Icon(
            imageVector = icon,
            contentDescription = null,
            modifier = Modifier.size(32.dp),
            tint = if (isDarkMode) InstitutionalBlueLight else InstitutionalBlue
        )
        Text(
            text = value,
            style = MaterialTheme.typography.headlineSmall.copy(
                fontWeight = FontWeight.Bold
            ),
            color = if (isDarkMode) InstitutionalBlueLight else InstitutionalBlue
        )
        Text(
            text = label,
            style = MaterialTheme.typography.bodySmall,
            color = if (isDarkMode) NeutralGray else NeutralMedium
        )
    }
}

@Composable
fun FiltrosCargo(
    cargoSeleccionado: String?,
    onCargoSeleccionado: (String?) -> Unit,
    isDarkMode: Boolean
) {
    val cargos = listOf("Todos" to null, "Presidente" to "Presidente", "Senador" to "Senador", "Diputado" to "Diputado")

    LazyRow(
        horizontalArrangement = Arrangement.spacedBy(12.dp),
        modifier = Modifier.fillMaxWidth()
    ) {
        items(cargos) { (label, cargo) ->
            FilterChip(
                selected = cargoSeleccionado == cargo,
                onClick = { onCargoSeleccionado(cargo) },
                label = { Text(label) },
                colors = FilterChipDefaults.filterChipColors(
                    selectedContainerColor = if (isDarkMode) InstitutionalBlueDark else InstitutionalBlue,
                    selectedLabelColor = NeutralWhite,
                    containerColor = if (isDarkMode) DarkSurfVar else NeutralGray
                )
            )
        }
    }
}

@Composable
fun ResultadoCandidatoCard(
    resultado: com.rivera.votainformado.data.model.votos.ResultadoGeneral,
    maxVotos: Int,
    isDarkMode: Boolean
) {
    val porcentaje = if (maxVotos > 0) (resultado.totalVotos.toFloat() / maxVotos.toFloat() * 100f) else 0f

    Card(
        modifier = Modifier.fillMaxWidth(),
        shape = RoundedCornerShape(16.dp),
        colors = CardDefaults.cardColors(
            containerColor = if (isDarkMode) DarkSurfVar else NeutralWhite
        ),
        elevation = CardDefaults.cardElevation(defaultElevation = 2.dp)
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp),
            verticalArrangement = Arrangement.spacedBy(12.dp)
        ) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(12.dp),
                verticalAlignment = Alignment.CenterVertically
            ) {
                // Foto del candidato
                Box(
                    modifier = Modifier
                        .size(60.dp)
                        .clip(RoundedCornerShape(12.dp))
                        .background(if (isDarkMode) DarkSurf else NeutralLight)
                ) {
                    Image(
                        painter = rememberAsyncImagePainter(
                            model = resultado.fotoUrl ?: resultado.partido.logoUrl.orEmpty(),
                            error = painterResource(id = R.drawable.carrusel_foto_1)
                        ),
                        contentDescription = null,
                        modifier = Modifier.fillMaxSize(),
                        contentScale = ContentScale.Crop
                    )
                }

                Column(modifier = Modifier.weight(1f)) {
                    Text(
                        text = resultado.nombreCompleto,
                        style = MaterialTheme.typography.titleMedium.copy(
                            fontWeight = FontWeight.Bold
                        ),
                        maxLines = 2
                    )
                    Text(
                        text = resultado.partido.nombre,
                        style = MaterialTheme.typography.bodySmall,
                        color = if (isDarkMode) InstitutionalBlueLight else InstitutionalBlue
                    )
                }

                Column(
                    horizontalAlignment = Alignment.End
                ) {
                    Text(
                        text = "${resultado.totalVotos}",
                        style = MaterialTheme.typography.headlineSmall.copy(
                            fontWeight = FontWeight.Bold,
                            color = if (isDarkMode) InstitutionalBlueLight else InstitutionalBlue
                        )
                    )
                    Text(
                        text = "votos",
                        style = MaterialTheme.typography.bodySmall,
                        color = if (isDarkMode) NeutralGray else NeutralMedium
                    )
                }
            }

            // Barra de progreso
            Column {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween
                ) {
                    Text(
                        text = "${String.format("%.1f", porcentaje)}%",
                        style = MaterialTheme.typography.bodySmall.copy(
                            fontWeight = FontWeight.Medium
                        ),
                        color = if (isDarkMode) NeutralGray else NeutralMedium
                    )
                }
                Spacer(modifier = Modifier.height(4.dp))
                LinearProgressIndicator(
                    progress = { porcentaje / 100f },
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(8.dp)
                        .clip(RoundedCornerShape(4.dp)),
                    color = if (isDarkMode) InstitutionalBlueLight else InstitutionalBlue,
                    trackColor = if (isDarkMode) DarkSurf else NeutralGray
                )
            }
        }
    }
}

@Composable
fun ResultadoPartidoCard(
    resultado: com.rivera.votainformado.data.model.votos.ResultadoPorPartido,
    maxVotos: Int,
    isDarkMode: Boolean
) {
    val porcentaje = if (maxVotos > 0) (resultado.totalVotos.toFloat() / maxVotos.toFloat() * 100f) else 0f

    Card(
        modifier = Modifier.fillMaxWidth(),
        shape = RoundedCornerShape(16.dp),
        colors = CardDefaults.cardColors(
            containerColor = if (isDarkMode) DarkSurfVar else NeutralWhite
        ),
        elevation = CardDefaults.cardElevation(defaultElevation = 2.dp)
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp),
            horizontalArrangement = Arrangement.spacedBy(16.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            // Logo del partido
            Box(
                modifier = Modifier
                    .size(60.dp)
                    .clip(RoundedCornerShape(12.dp))
                    .background(if (isDarkMode) DarkSurf else NeutralLight)
            ) {
                Image(
                    painter = rememberAsyncImagePainter(
                        model = resultado.logoUrl.orEmpty(),
                        error = painterResource(id = R.drawable.carrusel_foto_1)
                    ),
                    contentDescription = null,
                    modifier = Modifier.fillMaxSize(),
                    contentScale = ContentScale.Crop
                )
            }

            Column(modifier = Modifier.weight(1f)) {
                Text(
                    text = resultado.nombrePartido,
                    style = MaterialTheme.typography.titleMedium.copy(
                        fontWeight = FontWeight.Bold
                    )
                )
                Text(
                    text = resultado.sigla,
                    style = MaterialTheme.typography.bodySmall,
                    color = if (isDarkMode) InstitutionalBlueLight else InstitutionalBlue
                )
                Spacer(modifier = Modifier.height(8.dp))
                LinearProgressIndicator(
                    progress = { porcentaje / 100f },
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(6.dp)
                        .clip(RoundedCornerShape(3.dp)),
                    color = if (isDarkMode) InstitutionalBlueLight else InstitutionalBlue,
                    trackColor = if (isDarkMode) DarkSurf else NeutralGray
                )
            }

            Column(
                horizontalAlignment = Alignment.End
            ) {
                Text(
                    text = "${resultado.totalVotos}",
                    style = MaterialTheme.typography.headlineSmall.copy(
                        fontWeight = FontWeight.Bold,
                        color = if (isDarkMode) InstitutionalBlueLight else InstitutionalBlue
                    )
                )
                Text(
                    text = "votos",
                    style = MaterialTheme.typography.bodySmall,
                    color = if (isDarkMode) NeutralGray else NeutralMedium
                )
            }
        }
    }
}

@Composable
fun EmptyResultsCard(isDarkMode: Boolean) {
    Card(
        modifier = Modifier.fillMaxWidth(),
        shape = RoundedCornerShape(16.dp),
        colors = CardDefaults.cardColors(
            containerColor = if (isDarkMode) DarkSurfVar else NeutralLight
        )
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(32.dp),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.spacedBy(12.dp)
        ) {
            Icon(
                imageVector = Icons.Default.BarChart,
                contentDescription = null,
                modifier = Modifier.size(64.dp),
                tint = if (isDarkMode) NeutralGray else NeutralMedium
            )
            Text(
                text = "No hay resultados disponibles",
                style = MaterialTheme.typography.titleMedium.copy(
                    fontWeight = FontWeight.Bold
                ),
                color = MaterialTheme.colorScheme.onSurface
            )
            Text(
                text = "Los resultados aparecerán cuando haya votos registrados",
                style = MaterialTheme.typography.bodyMedium,
                color = if (isDarkMode) NeutralGray else NeutralMedium
            )
        }
    }
}
