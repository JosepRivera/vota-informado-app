package com.rivera.votainformado.ui.comparar

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.scaleIn
import androidx.compose.animation.scaleOut
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
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
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import coil.compose.rememberAsyncImagePainter
import com.google.accompanist.swiperefresh.SwipeRefresh
import com.google.accompanist.swiperefresh.rememberSwipeRefreshState
import com.rivera.votainformado.R
import com.rivera.votainformado.ui.theme.*
import com.rivera.votainformado.ui.navigation.BottomNavigationBar

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun CompararScreen(
    onNavigate: (String) -> Unit = {},
    onNavigateToDetail: (Int) -> Unit = {}
) {
    val isDarkMode = isSystemInDarkTheme()
    val viewModel: CompararViewModel = viewModel()
    val state by viewModel.compararState.collectAsState()

    var showSelector1 by remember { mutableStateOf(false) }
    var showSelector2 by remember { mutableStateOf(false) }

    Scaffold(
        topBar = {
            TopAppBar(
                title = {
                    Text(
                        text = "Comparar Candidatos",
                        style = MaterialTheme.typography.titleLarge.copy(
                            fontWeight = FontWeight.Bold,
                            color = if (isDarkMode) InstitutionalBlueLight else InstitutionalBlue
                        )
                    )
                },
                actions = {
                    if (state.candidato1 != null || state.candidato2 != null) {
                        IconButton(onClick = { viewModel.limpiarComparacion() }) {
                            Icon(
                                imageVector = Icons.Default.Clear,
                                contentDescription = "Limpiar",
                                tint = if (isDarkMode) InstitutionalBlueLight else InstitutionalBlue
                            )
                        }
                    }
                },
                colors = TopAppBarDefaults.topAppBarColors(
                    containerColor = MaterialTheme.colorScheme.surface
                )
            )
        },
        bottomBar = {
            BottomNavigationBar(
                currentRoute = "comparar",
                onNavigate = onNavigate
            )
        }
    ) { innerPadding ->
        val swipeRefreshState = rememberSwipeRefreshState(isRefreshing = state.isLoading)
        
        SwipeRefresh(
            state = swipeRefreshState,
            onRefresh = { viewModel.cargarListaCandidatos() },
            modifier = Modifier
                .fillMaxSize()
                .padding(innerPadding)
        ) {
            LazyColumn(
                modifier = Modifier
                    .fillMaxSize()
                    .background(MaterialTheme.colorScheme.background),
                contentPadding = PaddingValues(20.dp),
                verticalArrangement = Arrangement.spacedBy(24.dp)
            ) {
            // Selectores de candidatos
            item {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(12.dp)
                ) {
                    // Selector 1
                    SelectorCandidato(
                        candidato = state.candidato1,
                        label = "Candidato 1",
                        onClick = { showSelector1 = true },
                        onClear = { viewModel.limpiarCandidato1() },
                        modifier = Modifier.weight(1f),
                        isDarkMode = isDarkMode
                    )

                    // Selector 2
                    SelectorCandidato(
                        candidato = state.candidato2,
                        label = "Candidato 2",
                        onClick = { showSelector2 = true },
                        onClear = { viewModel.limpiarCandidato2() },
                        modifier = Modifier.weight(1f),
                        isDarkMode = isDarkMode
                    )
                }
            }

            // Comparación con animación
            if (state.candidato1 != null && state.candidato2 != null) {
                item {
                    AnimatedVisibility(
                        visible = true,
                        enter = fadeIn() + scaleIn(),
                        exit = fadeOut() + scaleOut()
                    ) {
                        ComparacionHeader(
                            candidato1 = state.candidato1!!,
                            candidato2 = state.candidato2!!,
                            isDarkMode = isDarkMode,
                            onNavigateToDetail = onNavigateToDetail
                        )
                    }
                }

                item {
                    ComparacionBasica(
                        candidato1 = state.candidato1!!,
                        candidato2 = state.candidato2!!,
                        isDarkMode = isDarkMode
                    )
                }

                // Indicador visual de mejor/peor
                item {
                    ComparacionMetricas(
                        candidato1 = state.candidato1!!,
                        candidato2 = state.candidato2!!,
                        isDarkMode = isDarkMode
                    )
                }

                item {
                    ComparacionAntecedentes(
                        titulo = "Denuncias",
                        antecedentes1 = state.candidato1!!.denuncias ?: emptyList(),
                        antecedentes2 = state.candidato2!!.denuncias ?: emptyList(),
                        isDarkMode = isDarkMode,
                        isNegative = true // Denuncias son negativas
                    )
                }

                item {
                    ComparacionAntecedentes(
                        titulo = "Proyectos de Ley",
                        antecedentes1 = state.candidato1!!.proyectos ?: emptyList(),
                        antecedentes2 = state.candidato2!!.proyectos ?: emptyList(),
                        isDarkMode = isDarkMode,
                        isNegative = false // Proyectos son positivos
                    )
                }

                item {
                    ComparacionAntecedentes(
                        titulo = "Propuestas",
                        antecedentes1 = state.candidato1!!.propuestas ?: emptyList(),
                        antecedentes2 = state.candidato2!!.propuestas ?: emptyList(),
                        isDarkMode = isDarkMode,
                        isNegative = false // Propuestas son positivas
                    )
                }
            } else {
                item {
                    EmptyComparisonCard(isDarkMode = isDarkMode)
                }
            }

                // Espaciado final
                item {
                    Spacer(modifier = Modifier.height(16.dp))
                }
            }
        }
    }

    // Dialogs para seleccionar candidatos
    if (showSelector1) {
        CandidatoSelectorDialog(
            onCandidatoSelected = { id ->
                viewModel.cargarCandidato1(id)
                showSelector1 = false
            },
            onDismiss = { showSelector1 = false },
            isDarkMode = isDarkMode,
            viewModel = viewModel
        )
    }

    if (showSelector2) {
        CandidatoSelectorDialog(
            onCandidatoSelected = { id ->
                viewModel.cargarCandidato2(id)
                showSelector2 = false
            },
            onDismiss = { showSelector2 = false },
            isDarkMode = isDarkMode,
            viewModel = viewModel
        )
    }
}

@Composable
fun SelectorCandidato(
    candidato: com.rivera.votainformado.data.model.candidatos.CandidatoDetail?,
    label: String,
    onClick: () -> Unit,
    onClear: () -> Unit,
    modifier: Modifier = Modifier,
    isDarkMode: Boolean
) {
    Card(
        modifier = modifier
            .fillMaxWidth()
            .clickable { onClick() },
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
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.spacedBy(12.dp)
        ) {
            if (candidato == null) {
                Icon(
                    imageVector = Icons.Default.Add,
                    contentDescription = null,
                    modifier = Modifier.size(48.dp),
                    tint = if (isDarkMode) NeutralGray else NeutralMedium
                )
                Text(
                    text = label,
                    style = MaterialTheme.typography.bodyMedium,
                    color = if (isDarkMode) NeutralGray else NeutralMedium
                )
                Text(
                    text = "Seleccionar",
                    style = MaterialTheme.typography.bodySmall,
                    color = if (isDarkMode) NeutralGray else NeutralMedium
                )
            } else {
                Box(
                    modifier = Modifier
                        .size(80.dp)
                        .clip(CircleShape)
                        .background(if (isDarkMode) DarkSurf else NeutralLight)
                ) {
                    Image(
                        painter = rememberAsyncImagePainter(
                            model = candidato.fotoUrl ?: candidato.partido.logoUrl.orEmpty(),
                            error = painterResource(id = R.drawable.carrusel_foto_1)
                        ),
                        contentDescription = null,
                        modifier = Modifier.fillMaxSize(),
                        contentScale = ContentScale.Crop
                    )
                }
                Text(
                    text = candidato.nombreCompleto,
                    style = MaterialTheme.typography.titleSmall.copy(
                        fontWeight = FontWeight.Bold
                    ),
                    maxLines = 2
                )
                Text(
                    text = candidato.partido.nombrePartido,
                    style = MaterialTheme.typography.bodySmall,
                    color = if (isDarkMode) InstitutionalBlueLight else InstitutionalBlue,
                    maxLines = 1
                )
                IconButton(
                    onClick = onClear,
                    modifier = Modifier.size(32.dp)
                ) {
                    Icon(
                        imageVector = Icons.Default.Close,
                        contentDescription = "Limpiar",
                        modifier = Modifier.size(20.dp),
                        tint = ErrorRed
                    )
                }
            }
        }
    }
}

@Composable
fun ComparacionHeader(
    candidato1: com.rivera.votainformado.data.model.candidatos.CandidatoDetail,
    candidato2: com.rivera.votainformado.data.model.candidatos.CandidatoDetail,
    isDarkMode: Boolean,
    onNavigateToDetail: (Int) -> Unit
) {
    Row(
        modifier = Modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.spacedBy(16.dp)
    ) {
        CandidatoHeaderCard(
            candidato = candidato1,
            isDarkMode = isDarkMode,
            onClick = { onNavigateToDetail(candidato1.id) },
            modifier = Modifier.weight(1f)
        )
        CandidatoHeaderCard(
            candidato = candidato2,
            isDarkMode = isDarkMode,
            onClick = { onNavigateToDetail(candidato2.id) },
            modifier = Modifier.weight(1f)
        )
    }
}

@Composable
fun CandidatoHeaderCard(
    candidato: com.rivera.votainformado.data.model.candidatos.CandidatoDetail,
    isDarkMode: Boolean,
    onClick: () -> Unit,
    modifier: Modifier = Modifier
) {
    Card(
        modifier = modifier
            .fillMaxWidth()
            .clickable { onClick() },
        shape = RoundedCornerShape(16.dp),
        colors = CardDefaults.cardColors(
            containerColor = if (isDarkMode) DarkSurfVar else NeutralWhite
        ),
        elevation = CardDefaults.cardElevation(defaultElevation = 4.dp)
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.spacedBy(12.dp)
        ) {
            Box(
                modifier = Modifier
                    .size(100.dp)
                    .clip(CircleShape)
                    .background(if (isDarkMode) DarkSurf else NeutralLight)
            ) {
                Image(
                    painter = rememberAsyncImagePainter(
                        model = candidato.fotoUrl ?: candidato.partido.logoUrl.orEmpty(),
                        error = painterResource(id = R.drawable.carrusel_foto_1)
                    ),
                    contentDescription = null,
                    modifier = Modifier.fillMaxSize(),
                    contentScale = ContentScale.Crop
                )
            }
            Text(
                text = candidato.nombreCompleto,
                style = MaterialTheme.typography.titleMedium.copy(
                    fontWeight = FontWeight.Bold
                ),
                maxLines = 2
            )
            Text(
                text = candidato.partido.nombrePartido,
                style = MaterialTheme.typography.bodyMedium,
                color = if (isDarkMode) InstitutionalBlueLight else InstitutionalBlue
            )
            Text(
                text = candidato.cargo.nombreCargo,
                style = MaterialTheme.typography.bodySmall,
                color = if (isDarkMode) NeutralGray else NeutralMedium
            )
        }
    }
}

@Composable
fun ComparacionMetricas(
    candidato1: com.rivera.votainformado.data.model.candidatos.CandidatoDetail,
    candidato2: com.rivera.votainformado.data.model.candidatos.CandidatoDetail,
    isDarkMode: Boolean
) {
    Card(
        modifier = Modifier.fillMaxWidth(),
        shape = RoundedCornerShape(16.dp),
        colors = CardDefaults.cardColors(
            containerColor = if (isDarkMode) DarkSurfVar else NeutralWhite
        )
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp),
            verticalArrangement = Arrangement.spacedBy(16.dp)
        ) {
            Text(
                text = "Resumen Comparativo",
                style = MaterialTheme.typography.titleMedium.copy(
                    fontWeight = FontWeight.Bold,
                    color = if (isDarkMode) InstitutionalBlueLight else InstitutionalBlue
                )
            )

            // Comparación de votos si están disponibles
            if (candidato1.totalVotos != null && candidato2.totalVotos != null) {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Column(
                        modifier = Modifier.weight(1f),
                        horizontalAlignment = Alignment.CenterHorizontally
                    ) {
                        Text(
                            text = "${candidato1.totalVotos}",
                            style = MaterialTheme.typography.headlineMedium.copy(
                                fontWeight = FontWeight.Bold,
                                color = if (candidato1.totalVotos > candidato2.totalVotos) 
                                    CivicGreen else MaterialTheme.colorScheme.onSurface
                            )
                        )
                        Text(
                            text = "votos",
                            style = MaterialTheme.typography.bodySmall,
                            color = if (isDarkMode) NeutralGray else NeutralMedium
                        )
                        if (candidato1.totalVotos > candidato2.totalVotos) {
                            Icon(
                                imageVector = Icons.Default.TrendingUp,
                                contentDescription = "Líder",
                                tint = CivicGreen,
                                modifier = Modifier.size(20.dp)
                            )
                        }
                    }
                    
                    Icon(
                        imageVector = Icons.Default.CompareArrows,
                        contentDescription = null,
                        modifier = Modifier.size(32.dp),
                        tint = if (isDarkMode) NeutralGray else NeutralMedium
                    )
                    
                    Column(
                        modifier = Modifier.weight(1f),
                        horizontalAlignment = Alignment.CenterHorizontally
                    ) {
                        Text(
                            text = "${candidato2.totalVotos}",
                            style = MaterialTheme.typography.headlineMedium.copy(
                                fontWeight = FontWeight.Bold,
                                color = if (candidato2.totalVotos > candidato1.totalVotos) 
                                    CivicGreen else MaterialTheme.colorScheme.onSurface
                            )
                        )
                        Text(
                            text = "votos",
                            style = MaterialTheme.typography.bodySmall,
                            color = if (isDarkMode) NeutralGray else NeutralMedium
                        )
                        if (candidato2.totalVotos > candidato1.totalVotos) {
                            Icon(
                                imageVector = Icons.Default.TrendingUp,
                                contentDescription = "Líder",
                                tint = CivicGreen,
                                modifier = Modifier.size(20.dp)
                            )
                        }
                    }
                }
            }
            
            // Comparación de antecedentes
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceEvenly
            ) {
                MetricaResumen(
                    label = "Denuncias",
                    valor1 = candidato1.denuncias?.size ?: 0,
                    valor2 = candidato2.denuncias?.size ?: 0,
                    isNegative = true,
                    isDarkMode = isDarkMode
                )
                MetricaResumen(
                    label = "Proyectos",
                    valor1 = candidato1.proyectos?.size ?: 0,
                    valor2 = candidato2.proyectos?.size ?: 0,
                    isNegative = false,
                    isDarkMode = isDarkMode
                )
                MetricaResumen(
                    label = "Propuestas",
                    valor1 = candidato1.propuestas?.size ?: 0,
                    valor2 = candidato2.propuestas?.size ?: 0,
                    isNegative = false,
                    isDarkMode = isDarkMode
                )
            }
        }
    }
}

@Composable
fun MetricaResumen(
    label: String,
    valor1: Int,
    valor2: Int,
    isNegative: Boolean,
    isDarkMode: Boolean
) {
    Column(
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.spacedBy(4.dp)
    ) {
        Text(
            text = label,
            style = MaterialTheme.typography.bodySmall,
            color = if (isDarkMode) NeutralGray else NeutralMedium
        )
        Row(
            horizontalArrangement = Arrangement.spacedBy(4.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Text(
                text = "$valor1",
                style = MaterialTheme.typography.bodyMedium.copy(
                    fontWeight = FontWeight.Bold
                ),
                color = when {
                    isNegative && valor1 < valor2 -> CivicGreen
                    !isNegative && valor1 > valor2 -> CivicGreen
                    valor1 != valor2 -> ErrorRed
                    else -> MaterialTheme.colorScheme.onSurface
                }
            )
            Text(
                text = "-",
                style = MaterialTheme.typography.bodySmall,
                color = if (isDarkMode) NeutralGray else NeutralMedium
            )
            Text(
                text = "$valor2",
                style = MaterialTheme.typography.bodyMedium.copy(
                    fontWeight = FontWeight.Bold
                ),
                color = when {
                    isNegative && valor2 < valor1 -> CivicGreen
                    !isNegative && valor2 > valor1 -> CivicGreen
                    valor1 != valor2 -> ErrorRed
                    else -> MaterialTheme.colorScheme.onSurface
                }
            )
        }
    }
}

@Composable
fun ComparacionBasica(
    candidato1: com.rivera.votainformado.data.model.candidatos.CandidatoDetail,
    candidato2: com.rivera.votainformado.data.model.candidatos.CandidatoDetail,
    isDarkMode: Boolean
) {
    Card(
        modifier = Modifier.fillMaxWidth(),
        shape = RoundedCornerShape(16.dp),
        colors = CardDefaults.cardColors(
            containerColor = if (isDarkMode) DarkSurfVar else NeutralWhite
        )
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp),
            verticalArrangement = Arrangement.spacedBy(16.dp)
        ) {
            Text(
                text = "Información Básica",
                style = MaterialTheme.typography.titleMedium.copy(
                    fontWeight = FontWeight.Bold,
                    color = if (isDarkMode) InstitutionalBlueLight else InstitutionalBlue
                )
            )

            ComparacionRow(
                label = "Partido",
                value1 = candidato1.partido.nombrePartido,
                value2 = candidato2.partido.nombrePartido,
                isDarkMode = isDarkMode
            )

            ComparacionRow(
                label = "Cargo",
                value1 = candidato1.cargo.nombreCargo,
                value2 = candidato2.cargo.nombreCargo,
                isDarkMode = isDarkMode
            )

            if (candidato1.totalVotos != null && candidato2.totalVotos != null) {
                ComparacionRow(
                    label = "Votos",
                    value1 = candidato1.totalVotos.toString(),
                    value2 = candidato2.totalVotos.toString(),
                    isDarkMode = isDarkMode
                )
            }
        }
    }
}

@Composable
fun ComparacionRow(
    label: String,
    value1: String,
    value2: String,
    isDarkMode: Boolean
) {
    Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
        Text(
            text = label,
            style = MaterialTheme.typography.bodySmall,
            color = if (isDarkMode) NeutralGray else NeutralMedium
        )
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.spacedBy(12.dp)
        ) {
            Text(
                text = value1,
                style = MaterialTheme.typography.bodyMedium,
                modifier = Modifier.weight(1f)
            )
            Text(
                text = "vs",
                style = MaterialTheme.typography.bodySmall,
                color = if (isDarkMode) NeutralGray else NeutralMedium
            )
            Text(
                text = value2,
                style = MaterialTheme.typography.bodyMedium,
                modifier = Modifier.weight(1f)
            )
        }
    }
}

@Composable
fun ComparacionAntecedentes(
    titulo: String,
    antecedentes1: List<com.rivera.votainformado.data.model.candidatos.Antecedente>,
    antecedentes2: List<com.rivera.votainformado.data.model.candidatos.Antecedente>,
    isDarkMode: Boolean,
    isNegative: Boolean = false // Denuncias son negativas, proyectos/propuestas son positivas
) {
    Card(
        modifier = Modifier.fillMaxWidth(),
        shape = RoundedCornerShape(16.dp),
        colors = CardDefaults.cardColors(
            containerColor = if (isDarkMode) DarkSurfVar else NeutralWhite
        )
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp),
            verticalArrangement = Arrangement.spacedBy(16.dp)
        ) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(
                    text = titulo,
                    style = MaterialTheme.typography.titleMedium.copy(
                        fontWeight = FontWeight.Bold,
                        color = if (isDarkMode) InstitutionalBlueLight else InstitutionalBlue
                    )
                )
                Row(
                    horizontalArrangement = Arrangement.spacedBy(16.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    // Candidato 1
                    Row(
                        horizontalArrangement = Arrangement.spacedBy(4.dp),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Text(
                            text = "${antecedentes1.size}",
                            style = MaterialTheme.typography.titleMedium.copy(
                                fontWeight = FontWeight.Bold,
                                color = when {
                                    isNegative && antecedentes1.size < antecedentes2.size -> CivicGreen
                                    !isNegative && antecedentes1.size > antecedentes2.size -> CivicGreen
                                    antecedentes1.size != antecedentes2.size -> ErrorRed
                                    else -> MaterialTheme.colorScheme.onSurface
                                }
                            )
                        )
                        if (antecedentes1.size != antecedentes2.size) {
                            Icon(
                                imageVector = if (
                                    (isNegative && antecedentes1.size < antecedentes2.size) ||
                                    (!isNegative && antecedentes1.size > antecedentes2.size)
                                ) Icons.Default.CheckCircle else Icons.Default.Warning,
                                contentDescription = null,
                                modifier = Modifier.size(16.dp),
                                tint = if (
                                    (isNegative && antecedentes1.size < antecedentes2.size) ||
                                    (!isNegative && antecedentes1.size > antecedentes2.size)
                                ) CivicGreen else ErrorRed
                            )
                        }
                    }
                    
                    Text(
                        text = "vs",
                        style = MaterialTheme.typography.bodySmall,
                        color = if (isDarkMode) NeutralGray else NeutralMedium
                    )
                    
                    // Candidato 2
                    Row(
                        horizontalArrangement = Arrangement.spacedBy(4.dp),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Text(
                            text = "${antecedentes2.size}",
                            style = MaterialTheme.typography.titleMedium.copy(
                                fontWeight = FontWeight.Bold,
                                color = when {
                                    isNegative && antecedentes2.size < antecedentes1.size -> CivicGreen
                                    !isNegative && antecedentes2.size > antecedentes1.size -> CivicGreen
                                    antecedentes1.size != antecedentes2.size -> ErrorRed
                                    else -> MaterialTheme.colorScheme.onSurface
                                }
                            )
                        )
                        if (antecedentes1.size != antecedentes2.size) {
                            Icon(
                                imageVector = if (
                                    (isNegative && antecedentes2.size < antecedentes1.size) ||
                                    (!isNegative && antecedentes2.size > antecedentes1.size)
                                ) Icons.Default.CheckCircle else Icons.Default.Warning,
                                contentDescription = null,
                                modifier = Modifier.size(16.dp),
                                tint = if (
                                    (isNegative && antecedentes2.size < antecedentes1.size) ||
                                    (!isNegative && antecedentes2.size > antecedentes1.size)
                                ) CivicGreen else ErrorRed
                            )
                        }
                    }
                }
            }

            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(12.dp)
            ) {
                Column(
                    modifier = Modifier.weight(1f),
                    verticalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    antecedentes1.take(3).forEach { antecedente ->
                        AntecedenteCard(
                            antecedente = antecedente,
                            isDarkMode = isDarkMode,
                            modifier = Modifier.fillMaxWidth()
                        )
                    }
                }
                Column(
                    modifier = Modifier.weight(1f),
                    verticalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    antecedentes2.take(3).forEach { antecedente ->
                        AntecedenteCard(
                            antecedente = antecedente,
                            isDarkMode = isDarkMode,
                            modifier = Modifier.fillMaxWidth()
                        )
                    }
                }
            }
        }
    }
}

@Composable
fun AntecedenteCard(
    antecedente: com.rivera.votainformado.data.model.candidatos.Antecedente,
    isDarkMode: Boolean,
    modifier: Modifier = Modifier
) {
    Surface(
        modifier = modifier,
        shape = RoundedCornerShape(12.dp),
        color = if (isDarkMode) DarkSurf else NeutralLight
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(12.dp),
            verticalArrangement = Arrangement.spacedBy(4.dp)
        ) {
            Text(
                text = antecedente.titulo,
                style = MaterialTheme.typography.bodySmall.copy(
                    fontWeight = FontWeight.Medium
                ),
                maxLines = 2
            )
            antecedente.fecha?.let {
                Text(
                    text = it,
                    style = MaterialTheme.typography.bodySmall,
                    color = if (isDarkMode) NeutralGray else NeutralMedium
                )
            }
        }
    }
}

@Composable
fun EmptyComparisonCard(isDarkMode: Boolean) {
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
                imageVector = Icons.Default.Compare,
                contentDescription = null,
                modifier = Modifier.size(64.dp),
                tint = if (isDarkMode) NeutralGray else NeutralMedium
            )
            Text(
                text = "Selecciona dos candidatos",
                style = MaterialTheme.typography.titleMedium.copy(
                    fontWeight = FontWeight.Bold
                ),
                color = MaterialTheme.colorScheme.onSurface
            )
            Text(
                text = "Toca en las tarjetas de arriba para seleccionar los candidatos que deseas comparar",
                style = MaterialTheme.typography.bodyMedium,
                color = if (isDarkMode) NeutralGray else NeutralMedium
            )
        }
    }
}

@Composable
fun CandidatoSelectorDialog(
    onCandidatoSelected: (Int) -> Unit,
    onDismiss: () -> Unit,
    isDarkMode: Boolean,
    viewModel: CompararViewModel
) {
    val state by viewModel.compararState.collectAsState()
    var searchQuery by remember { mutableStateOf("") }

    val filteredCandidatos = if (searchQuery.isNotEmpty()) {
        state.listaCandidatos.filter {
            it.nombreCompleto.contains(searchQuery, ignoreCase = true) ||
            it.partido.nombrePartido.contains(searchQuery, ignoreCase = true)
        }
    } else {
        state.listaCandidatos
    }

    AlertDialog(
        onDismissRequest = onDismiss,
        title = {
            Text(
                text = "Seleccionar Candidato",
                style = MaterialTheme.typography.titleLarge.copy(
                    fontWeight = FontWeight.Bold
                )
            )
        },
        text = {
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .heightIn(max = 400.dp),
                verticalArrangement = Arrangement.spacedBy(12.dp)
            ) {
                // Barra de búsqueda
                OutlinedTextField(
                    value = searchQuery,
                    onValueChange = { searchQuery = it },
                    modifier = Modifier.fillMaxWidth(),
                    placeholder = {
                        Text("Buscar candidato...")
                    },
                    leadingIcon = {
                        Icon(
                            imageVector = Icons.Default.Search,
                            contentDescription = null
                        )
                    },
                    trailingIcon = {
                        if (searchQuery.isNotEmpty()) {
                            IconButton(onClick = { searchQuery = "" }) {
                                Icon(
                                    imageVector = Icons.Default.Clear,
                                    contentDescription = "Limpiar"
                                )
                            }
                        }
                    },
                    singleLine = true
                )

                // Lista de candidatos
                if (state.isLoadingCandidatos) {
                    Box(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(32.dp),
                        contentAlignment = Alignment.Center
                    ) {
                        CircularProgressIndicator(
                            modifier = Modifier.size(32.dp),
                            color = if (isDarkMode) InstitutionalBlueLight else InstitutionalBlue
                        )
                    }
                } else if (filteredCandidatos.isEmpty()) {
                    Text(
                        text = if (searchQuery.isNotEmpty()) {
                            "No se encontraron candidatos"
                        } else {
                            "No hay candidatos disponibles"
                        },
                        style = MaterialTheme.typography.bodyMedium,
                        color = if (isDarkMode) NeutralGray else NeutralMedium,
                        modifier = Modifier.padding(16.dp)
                    )
                } else {
                    androidx.compose.foundation.lazy.LazyColumn(
                        modifier = Modifier
                            .fillMaxWidth()
                            .heightIn(max = 300.dp),
                        verticalArrangement = Arrangement.spacedBy(8.dp)
                    ) {
                        items(filteredCandidatos) { candidato ->
                            CandidatoSelectorItem(
                                candidato = candidato,
                                isDarkMode = isDarkMode,
                                onClick = {
                                    onCandidatoSelected(candidato.id)
                                }
                            )
                        }
                    }
                }
            }
        },
        confirmButton = {
            TextButton(onClick = onDismiss) {
                Text("Cancelar")
            }
        },
        containerColor = MaterialTheme.colorScheme.surface
    )
}

@Composable
fun CandidatoSelectorItem(
    candidato: com.rivera.votainformado.data.model.candidatos.CandidatoItem,
    isDarkMode: Boolean,
    onClick: () -> Unit
) {
    Surface(
        modifier = Modifier
            .fillMaxWidth()
            .clickable { onClick() },
        shape = RoundedCornerShape(12.dp),
        color = if (isDarkMode) DarkSurfVar else NeutralLight
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(12.dp),
            horizontalArrangement = Arrangement.spacedBy(12.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Box(
                modifier = Modifier
                    .size(50.dp)
                    .clip(RoundedCornerShape(10.dp))
                    .background(if (isDarkMode) DarkSurf else NeutralGray)
            ) {
                Image(
                    painter = rememberAsyncImagePainter(
                        model = candidato.fotoUrl ?: candidato.partido.logoUrl.orEmpty(),
                        error = painterResource(id = R.drawable.carrusel_foto_1)
                    ),
                    contentDescription = null,
                    modifier = Modifier.fillMaxSize(),
                    contentScale = ContentScale.Crop
                )
            }

            Column(modifier = Modifier.weight(1f)) {
                Text(
                    text = candidato.nombreCompleto,
                    style = MaterialTheme.typography.bodyMedium.copy(
                        fontWeight = FontWeight.Medium
                    ),
                    maxLines = 1
                )
                Text(
                    text = candidato.partido.nombrePartido,
                    style = MaterialTheme.typography.bodySmall,
                    color = if (isDarkMode) InstitutionalBlueLight else InstitutionalBlue,
                    maxLines = 1
                )
            }
        }
    }
}
