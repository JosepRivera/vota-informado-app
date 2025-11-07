package com.rivera.votainformado.ui.comparar

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material.icons.outlined.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import coil.compose.rememberAsyncImagePainter
import com.google.accompanist.swiperefresh.SwipeRefresh
import com.google.accompanist.swiperefresh.rememberSwipeRefreshState
import com.rivera.votainformado.R
import com.rivera.votainformado.ui.theme.*
import com.rivera.votainformado.ui.navigation.BottomNavigationBar

// Colores optimizados
private val PrimaryBlue = Color(0xFF1565C0)
private val LightBlue = Color(0xFFE3F2FD)
private val SuccessGreen = Color(0xFF2E7D32)
private val WarningRed = Color(0xFFD32F2F)
private val NeutralGray = Color(0xFF757575)
private val BackgroundGray = Color(0xFFF5F7FA)
private val CardWhite = Color(0xFFFFFFFF)
private val TextPrimary = Color(0xFF212121)
private val TextSecondary = Color(0xFF616161)

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun CompararScreen(
    onNavigate: (String) -> Unit = {},
    onNavigateToDetail: (Int) -> Unit = {}
) {
    val viewModel: CompararViewModel = viewModel()
    val state by viewModel.compararState.collectAsState()

    var showSelector1 by remember { mutableStateOf(false) }
    var showSelector2 by remember { mutableStateOf(false) }

    Scaffold(
        topBar = {
            TopAppBar(
                title = {
                    Row(
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.spacedBy(8.dp)
                    ) {
                        Icon(
                            imageVector = Icons.Outlined.CompareArrows,
                            contentDescription = null,
                            tint = PrimaryBlue
                        )
                        Text(
                            text = "Comparar Candidatos",
                            style = MaterialTheme.typography.titleLarge.copy(
                                fontWeight = FontWeight.Bold,
                                color = PrimaryBlue
                            )
                        )
                    }
                },
                actions = {
                    if (state.candidato1 != null || state.candidato2 != null) {
                        IconButton(onClick = { viewModel.limpiarComparacion() }) {
                            Icon(
                                imageVector = Icons.Outlined.RestartAlt,
                                contentDescription = "Limpiar comparación",
                                tint = WarningRed
                            )
                        }
                    }
                },
                colors = TopAppBarDefaults.topAppBarColors(
                    containerColor = CardWhite
                )
            )
        },
        bottomBar = {
            BottomNavigationBar(
                currentRoute = "comparar",
                onNavigate = onNavigate
            )
        },
        containerColor = BackgroundGray
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
                modifier = Modifier.fillMaxSize(),
                contentPadding = PaddingValues(16.dp),
                verticalArrangement = Arrangement.spacedBy(16.dp)
            ) {
                // Selectores de candidatos
                item {
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.spacedBy(12.dp)
                    ) {
                        SelectorCandidato(
                            candidato = state.candidato1,
                            label = "Primer Candidato",
                            onClick = { showSelector1 = true },
                            onClear = { viewModel.limpiarCandidato1() },
                            modifier = Modifier.weight(1f)
                        )

                        SelectorCandidato(
                            candidato = state.candidato2,
                            label = "Segundo Candidato",
                            onClick = { showSelector2 = true },
                            onClear = { viewModel.limpiarCandidato2() },
                            modifier = Modifier.weight(1f)
                        )
                    }
                }

                // Comparación completa
                if (state.candidato1 != null && state.candidato2 != null) {
                    item {
                        AnimatedVisibility(
                            visible = true,
                            enter = fadeIn(),
                            exit = fadeOut()
                        ) {
                            Column(verticalArrangement = Arrangement.spacedBy(16.dp)) {
                                // Encabezado con fotos
                                ComparacionHeader(
                                    candidato1 = state.candidato1!!,
                                    candidato2 = state.candidato2!!,
                                    onNavigateToDetail = onNavigateToDetail
                                )

                                // Resumen de métricas
                                ComparacionMetricas(
                                    candidato1 = state.candidato1!!,
                                    candidato2 = state.candidato2!!
                                )

                                // Información básica
                                ComparacionBasica(
                                    candidato1 = state.candidato1!!,
                                    candidato2 = state.candidato2!!
                                )

                                // Denuncias
                                ComparacionAntecedentes(
                                    titulo = "Denuncias",
                                    icono = Icons.Outlined.Gavel,
                                    antecedentes1 = state.candidato1!!.denuncias ?: emptyList(),
                                    antecedentes2 = state.candidato2!!.denuncias ?: emptyList(),
                                    isNegative = true
                                )

                                // Proyectos de Ley
                                ComparacionAntecedentes(
                                    titulo = "Proyectos de Ley",
                                    icono = Icons.Outlined.Description,
                                    antecedentes1 = state.candidato1!!.proyectos ?: emptyList(),
                                    antecedentes2 = state.candidato2!!.proyectos ?: emptyList(),
                                    isNegative = false
                                )

                                // Propuestas
                                ComparacionAntecedentes(
                                    titulo = "Propuestas",
                                    icono = Icons.Outlined.Lightbulb,
                                    antecedentes1 = state.candidato1!!.propuestas ?: emptyList(),
                                    antecedentes2 = state.candidato2!!.propuestas ?: emptyList(),
                                    isNegative = false
                                )
                            }
                        }
                    }
                } else {
                    item {
                        EmptyComparisonCard()
                    }
                }

                item {
                    Spacer(modifier = Modifier.height(8.dp))
                }
            }
        }
    }

    // Diálogos de selección
    if (showSelector1) {
        CandidatoSelectorDialog(
            onCandidatoSelected = { id ->
                viewModel.cargarCandidato1(id)
                showSelector1 = false
            },
            onDismiss = { showSelector1 = false },
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
    modifier: Modifier = Modifier
) {
    Card(
        modifier = modifier
            .fillMaxWidth()
            .clickable { onClick() },
        shape = RoundedCornerShape(16.dp),
        colors = CardDefaults.cardColors(containerColor = CardWhite),
        elevation = CardDefaults.cardElevation(defaultElevation = 2.dp)
    ) {
        Box(modifier = Modifier.fillMaxWidth()) {
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(20.dp),
                horizontalAlignment = Alignment.CenterHorizontally,
                verticalArrangement = Arrangement.spacedBy(12.dp)
            ) {
                if (candidato == null) {
                    Box(
                        modifier = Modifier
                            .size(80.dp)
                            .clip(CircleShape)
                            .background(LightBlue),
                        contentAlignment = Alignment.Center
                    ) {
                        Icon(
                            imageVector = Icons.Outlined.PersonAdd,
                            contentDescription = null,
                            modifier = Modifier.size(40.dp),
                            tint = PrimaryBlue
                        )
                    }
                    Text(
                        text = label,
                        style = MaterialTheme.typography.titleSmall.copy(
                            fontWeight = FontWeight.SemiBold,
                            color = TextPrimary
                        )
                    )
                    Text(
                        text = "Toca para seleccionar",
                        style = MaterialTheme.typography.bodySmall,
                        color = TextSecondary
                    )
                } else {
                    Box(
                        modifier = Modifier
                            .size(90.dp)
                            .clip(CircleShape)
                            .background(LightBlue)
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
                            fontWeight = FontWeight.Bold,
                            color = TextPrimary
                        ),
                        textAlign = TextAlign.Center,
                        maxLines = 2
                    )
                    Text(
                        text = candidato.partido.nombrePartido,
                        style = MaterialTheme.typography.bodySmall.copy(
                            color = PrimaryBlue
                        ),
                        textAlign = TextAlign.Center,
                        maxLines = 1
                    )
                }
            }

            if (candidato != null) {
                IconButton(
                    onClick = onClear,
                    modifier = Modifier
                        .align(Alignment.TopEnd)
                        .padding(8.dp)
                        .size(32.dp)
                        .background(WarningRed.copy(alpha = 0.1f), CircleShape)
                ) {
                    Icon(
                        imageVector = Icons.Outlined.Close,
                        contentDescription = "Limpiar",
                        modifier = Modifier.size(18.dp),
                        tint = WarningRed
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
    onNavigateToDetail: (Int) -> Unit
) {
    Card(
        modifier = Modifier.fillMaxWidth(),
        shape = RoundedCornerShape(16.dp),
        colors = CardDefaults.cardColors(containerColor = CardWhite),
        elevation = CardDefaults.cardElevation(defaultElevation = 2.dp)
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(20.dp),
            horizontalArrangement = Arrangement.spacedBy(16.dp)
        ) {
            // Candidato 1
            Column(
                modifier = Modifier
                    .weight(1f)
                    .clickable { onNavigateToDetail(candidato1.id) },
                horizontalAlignment = Alignment.CenterHorizontally,
                verticalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                Box(
                    modifier = Modifier
                        .size(100.dp)
                        .clip(CircleShape)
                        .background(LightBlue)
                ) {
                    Image(
                        painter = rememberAsyncImagePainter(
                            model = candidato1.fotoUrl ?: candidato1.partido.logoUrl.orEmpty(),
                            error = painterResource(id = R.drawable.carrusel_foto_1)
                        ),
                        contentDescription = null,
                        modifier = Modifier.fillMaxSize(),
                        contentScale = ContentScale.Crop
                    )
                }
                Text(
                    text = candidato1.nombreCompleto,
                    style = MaterialTheme.typography.titleMedium.copy(
                        fontWeight = FontWeight.Bold,
                        color = TextPrimary
                    ),
                    textAlign = TextAlign.Center,
                    maxLines = 2
                )
                Text(
                    text = candidato1.cargo.nombreCargo,
                    style = MaterialTheme.typography.bodySmall,
                    color = TextSecondary,
                    textAlign = TextAlign.Center
                )
            }

            // Separador VS
            Column(
                horizontalAlignment = Alignment.CenterHorizontally,
                verticalArrangement = Arrangement.Center,
                modifier = Modifier.padding(vertical = 20.dp)
            ) {
                Box(
                    modifier = Modifier
                        .size(48.dp)
                        .clip(CircleShape)
                        .background(LightBlue),
                    contentAlignment = Alignment.Center
                ) {
                    Text(
                        text = "VS",
                        style = MaterialTheme.typography.titleMedium.copy(
                            fontWeight = FontWeight.Bold,
                            color = PrimaryBlue
                        )
                    )
                }
            }

            // Candidato 2
            Column(
                modifier = Modifier
                    .weight(1f)
                    .clickable { onNavigateToDetail(candidato2.id) },
                horizontalAlignment = Alignment.CenterHorizontally,
                verticalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                Box(
                    modifier = Modifier
                        .size(100.dp)
                        .clip(CircleShape)
                        .background(LightBlue)
                ) {
                    Image(
                        painter = rememberAsyncImagePainter(
                            model = candidato2.fotoUrl ?: candidato2.partido.logoUrl.orEmpty(),
                            error = painterResource(id = R.drawable.carrusel_foto_1)
                        ),
                        contentDescription = null,
                        modifier = Modifier.fillMaxSize(),
                        contentScale = ContentScale.Crop
                    )
                }
                Text(
                    text = candidato2.nombreCompleto,
                    style = MaterialTheme.typography.titleMedium.copy(
                        fontWeight = FontWeight.Bold,
                        color = TextPrimary
                    ),
                    textAlign = TextAlign.Center,
                    maxLines = 2
                )
                Text(
                    text = candidato2.cargo.nombreCargo,
                    style = MaterialTheme.typography.bodySmall,
                    color = TextSecondary,
                    textAlign = TextAlign.Center
                )
            }
        }
    }
}

@Composable
fun ComparacionMetricas(
    candidato1: com.rivera.votainformado.data.model.candidatos.CandidatoDetail,
    candidato2: com.rivera.votainformado.data.model.candidatos.CandidatoDetail
) {
    Card(
        modifier = Modifier.fillMaxWidth(),
        shape = RoundedCornerShape(16.dp),
        colors = CardDefaults.cardColors(containerColor = CardWhite),
        elevation = CardDefaults.cardElevation(defaultElevation = 2.dp)
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(20.dp),
            verticalArrangement = Arrangement.spacedBy(20.dp)
        ) {
            Row(
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                Icon(
                    imageVector = Icons.Outlined.Assessment,
                    contentDescription = null,
                    tint = PrimaryBlue,
                    modifier = Modifier.size(24.dp)
                )
                Text(
                    text = "Resumen Comparativo",
                    style = MaterialTheme.typography.titleMedium.copy(
                        fontWeight = FontWeight.Bold,
                        color = PrimaryBlue
                    )
                )
            }

            // Votos
            if (candidato1.totalVotos != null && candidato2.totalVotos != null) {
                MetricaComparativaGrande(
                    label = "Total de Votos",
                    valor1 = candidato1.totalVotos,
                    valor2 = candidato2.totalVotos,
                    icono = Icons.Outlined.HowToVote
                )
            }

            Divider(color = BackgroundGray, thickness = 1.dp)

            // Métricas en fila
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceEvenly
            ) {
                MetricaResumen(
                    label = "Denuncias",
                    valor1 = candidato1.denuncias?.size ?: 0,
                    valor2 = candidato2.denuncias?.size ?: 0,
                    icono = Icons.Outlined.Gavel,
                    isNegative = true
                )
                MetricaResumen(
                    label = "Proyectos",
                    valor1 = candidato1.proyectos?.size ?: 0,
                    valor2 = candidato2.proyectos?.size ?: 0,
                    icono = Icons.Outlined.Description,
                    isNegative = false
                )
                MetricaResumen(
                    label = "Propuestas",
                    valor1 = candidato1.propuestas?.size ?: 0,
                    valor2 = candidato2.propuestas?.size ?: 0,
                    icono = Icons.Outlined.Lightbulb,
                    isNegative = false
                )
            }
        }
    }
}

@Composable
fun MetricaComparativaGrande(
    label: String,
    valor1: Int,
    valor2: Int,
    icono: androidx.compose.ui.graphics.vector.ImageVector
) {
    Column(
        verticalArrangement = Arrangement.spacedBy(12.dp)
    ) {
        Row(
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.spacedBy(6.dp)
        ) {
            Icon(
                imageVector = icono,
                contentDescription = null,
                tint = TextSecondary,
                modifier = Modifier.size(20.dp)
            )
            Text(
                text = label,
                style = MaterialTheme.typography.bodyMedium.copy(
                    fontWeight = FontWeight.Medium,
                    color = TextSecondary
                )
            )
        }

        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceEvenly,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Column(
                horizontalAlignment = Alignment.CenterHorizontally,
                verticalArrangement = Arrangement.spacedBy(4.dp)
            ) {
                Text(
                    text = "$valor1",
                    style = MaterialTheme.typography.headlineLarge.copy(
                        fontWeight = FontWeight.Bold,
                        color = if (valor1 > valor2) SuccessGreen else TextPrimary
                    )
                )
                if (valor1 > valor2) {
                    Icon(
                        imageVector = Icons.Outlined.TrendingUp,
                        contentDescription = "Mayor",
                        tint = SuccessGreen,
                        modifier = Modifier.size(24.dp)
                    )
                }
            }

            Box(
                modifier = Modifier
                    .size(40.dp)
                    .clip(CircleShape)
                    .background(BackgroundGray),
                contentAlignment = Alignment.Center
            ) {
                Icon(
                    imageVector = Icons.Outlined.CompareArrows,
                    contentDescription = null,
                    tint = NeutralGray,
                    modifier = Modifier.size(24.dp)
                )
            }

            Column(
                horizontalAlignment = Alignment.CenterHorizontally,
                verticalArrangement = Arrangement.spacedBy(4.dp)
            ) {
                Text(
                    text = "$valor2",
                    style = MaterialTheme.typography.headlineLarge.copy(
                        fontWeight = FontWeight.Bold,
                        color = if (valor2 > valor1) SuccessGreen else TextPrimary
                    )
                )
                if (valor2 > valor1) {
                    Icon(
                        imageVector = Icons.Outlined.TrendingUp,
                        contentDescription = "Mayor",
                        tint = SuccessGreen,
                        modifier = Modifier.size(24.dp)
                    )
                }
            }
        }
    }
}

@Composable
fun MetricaResumen(
    label: String,
    valor1: Int,
    valor2: Int,
    icono: androidx.compose.ui.graphics.vector.ImageVector,
    isNegative: Boolean
) {
    Column(
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.spacedBy(8.dp)
    ) {
        Icon(
            imageVector = icono,
            contentDescription = null,
            tint = PrimaryBlue,
            modifier = Modifier.size(28.dp)
        )
        Text(
            text = label,
            style = MaterialTheme.typography.bodySmall.copy(
                fontWeight = FontWeight.Medium,
                color = TextSecondary
            )
        )
        Row(
            horizontalArrangement = Arrangement.spacedBy(8.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Text(
                text = "$valor1",
                style = MaterialTheme.typography.titleLarge.copy(
                    fontWeight = FontWeight.Bold
                ),
                color = when {
                    isNegative && valor1 < valor2 -> SuccessGreen
                    !isNegative && valor1 > valor2 -> SuccessGreen
                    valor1 != valor2 -> WarningRed
                    else -> TextPrimary
                }
            )
            Text(
                text = ":",
                style = MaterialTheme.typography.titleMedium,
                color = NeutralGray
            )
            Text(
                text = "$valor2",
                style = MaterialTheme.typography.titleLarge.copy(
                    fontWeight = FontWeight.Bold
                ),
                color = when {
                    isNegative && valor2 < valor1 -> SuccessGreen
                    !isNegative && valor2 > valor1 -> SuccessGreen
                    valor1 != valor2 -> WarningRed
                    else -> TextPrimary
                }
            )
        }
    }
}

@Composable
fun ComparacionBasica(
    candidato1: com.rivera.votainformado.data.model.candidatos.CandidatoDetail,
    candidato2: com.rivera.votainformado.data.model.candidatos.CandidatoDetail
) {
    Card(
        modifier = Modifier.fillMaxWidth(),
        shape = RoundedCornerShape(16.dp),
        colors = CardDefaults.cardColors(containerColor = CardWhite),
        elevation = CardDefaults.cardElevation(defaultElevation = 2.dp)
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(20.dp),
            verticalArrangement = Arrangement.spacedBy(16.dp)
        ) {
            Row(
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                Icon(
                    imageVector = Icons.Outlined.Info,
                    contentDescription = null,
                    tint = PrimaryBlue,
                    modifier = Modifier.size(24.dp)
                )
                Text(
                    text = "Información General",
                    style = MaterialTheme.typography.titleMedium.copy(
                        fontWeight = FontWeight.Bold,
                        color = PrimaryBlue
                    )
                )
            }

            ComparacionRow(
                label = "Partido Político",
                value1 = candidato1.partido.nombrePartido,
                value2 = candidato2.partido.nombrePartido,
                icono = Icons.Outlined.AccountBalance
            )

            Divider(color = BackgroundGray, thickness = 1.dp)

            ComparacionRow(
                label = "Cargo al que Postula",
                value1 = candidato1.cargo.nombreCargo,
                value2 = candidato2.cargo.nombreCargo,
                icono = Icons.Outlined.WorkOutline
            )
        }
    }
}

@Composable
fun ComparacionRow(
    label: String,
    value1: String,
    value2: String,
    icono: androidx.compose.ui.graphics.vector.ImageVector
) {
    Column(verticalArrangement = Arrangement.spacedBy(12.dp)) {
        Row(
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.spacedBy(6.dp)
        ) {
            Icon(
                imageVector = icono,
                contentDescription = null,
                tint = TextSecondary,
                modifier = Modifier.size(18.dp)
            )
            Text(
                text = label,
                style = MaterialTheme.typography.bodyMedium.copy(
                    fontWeight = FontWeight.Medium,
                    color = TextSecondary
                )
            )
        }
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.spacedBy(16.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Text(
                text = value1,
                style = MaterialTheme.typography.bodyMedium.copy(
                    fontWeight = FontWeight.SemiBold,
                    color = TextPrimary
                ),
                modifier = Modifier.weight(1f)
            )
            Icon(
                imageVector = Icons.Outlined.CompareArrows,
                contentDescription = null,
                tint = NeutralGray,
                modifier = Modifier.size(20.dp)
            )
            Text(
                text = value2,
                style = MaterialTheme.typography.bodyMedium.copy(
                    fontWeight = FontWeight.SemiBold,
                    color = TextPrimary
                ),
                modifier = Modifier.weight(1f)
            )
        }
    }
}

@Composable
fun ComparacionAntecedentes(
    titulo: String,
    icono: androidx.compose.ui.graphics.vector.ImageVector,
    antecedentes1: List<com.rivera.votainformado.data.model.candidatos.Antecedente>,
    antecedentes2: List<com.rivera.votainformado.data.model.candidatos.Antecedente>,
    isNegative: Boolean
) {
    Card(
        modifier = Modifier.fillMaxWidth(),
        shape = RoundedCornerShape(16.dp),
        colors = CardDefaults.cardColors(containerColor = CardWhite),
        elevation = CardDefaults.cardElevation(defaultElevation = 2.dp)
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(20.dp),
            verticalArrangement = Arrangement.spacedBy(16.dp)
        ) {
            // Encabezado con contador
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    Icon(
                        imageVector = icono,
                        contentDescription = null,
                        tint = PrimaryBlue,
                        modifier = Modifier.size(24.dp)
                    )
                    Text(
                        text = titulo,
                        style = MaterialTheme.typography.titleMedium.copy(
                            fontWeight = FontWeight.Bold,
                            color = PrimaryBlue
                        )
                    )
                }

                Row(
                    horizontalArrangement = Arrangement.spacedBy(12.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    // Candidato 1
                    Box(
                        modifier = Modifier
                            .size(48.dp)
                            .clip(CircleShape)
                            .background(
                                when {
                                    isNegative && antecedentes1.size < antecedentes2.size -> SuccessGreen.copy(alpha = 0.15f)
                                    !isNegative && antecedentes1.size > antecedentes2.size -> SuccessGreen.copy(alpha = 0.15f)
                                    antecedentes1.size != antecedentes2.size -> WarningRed.copy(alpha = 0.15f)
                                    else -> BackgroundGray
                                }
                            ),
                        contentAlignment = Alignment.Center
                    ) {
                        Text(
                            text = "${antecedentes1.size}",
                            style = MaterialTheme.typography.titleLarge.copy(
                                fontWeight = FontWeight.Bold,
                                color = when {
                                    isNegative && antecedentes1.size < antecedentes2.size -> SuccessGreen
                                    !isNegative && antecedentes1.size > antecedentes2.size -> SuccessGreen
                                    antecedentes1.size != antecedentes2.size -> WarningRed
                                    else -> TextPrimary
                                }
                            )
                        )
                    }

                    Icon(
                        imageVector = Icons.Outlined.CompareArrows,
                        contentDescription = null,
                        tint = NeutralGray,
                        modifier = Modifier.size(20.dp)
                    )

                    // Candidato 2
                    Box(
                        modifier = Modifier
                            .size(48.dp)
                            .clip(CircleShape)
                            .background(
                                when {
                                    isNegative && antecedentes2.size < antecedentes1.size -> SuccessGreen.copy(alpha = 0.15f)
                                    !isNegative && antecedentes2.size > antecedentes1.size -> SuccessGreen.copy(alpha = 0.15f)
                                    antecedentes1.size != antecedentes2.size -> WarningRed.copy(alpha = 0.15f)
                                    else -> BackgroundGray
                                }
                            ),
                        contentAlignment = Alignment.Center
                    ) {
                        Text(
                            text = "${antecedentes2.size}",
                            style = MaterialTheme.typography.titleLarge.copy(
                                fontWeight = FontWeight.Bold,
                                color = when {
                                    isNegative && antecedentes2.size < antecedentes1.size -> SuccessGreen
                                    !isNegative && antecedentes2.size > antecedentes1.size -> SuccessGreen
                                    antecedentes1.size != antecedentes2.size -> WarningRed
                                    else -> TextPrimary
                                }
                            )
                        )
                    }
                }
            }

            // Lista de antecedentes lado a lado
            if (antecedentes1.isNotEmpty() || antecedentes2.isNotEmpty()) {
                Divider(color = BackgroundGray, thickness = 1.dp)

                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(12.dp)
                ) {
                    Column(
                        modifier = Modifier.weight(1f),
                        verticalArrangement = Arrangement.spacedBy(8.dp)
                    ) {
                        if (antecedentes1.isEmpty()) {
                            Box(
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .clip(RoundedCornerShape(12.dp))
                                    .background(BackgroundGray)
                                    .padding(16.dp),
                                contentAlignment = Alignment.Center
                            ) {
                                Text(
                                    text = "Sin registros",
                                    style = MaterialTheme.typography.bodySmall,
                                    color = TextSecondary,
                                    textAlign = TextAlign.Center
                                )
                            }
                        } else {
                            antecedentes1.take(3).forEach { antecedente ->
                                AntecedenteCard(antecedente = antecedente)
                            }
                            if (antecedentes1.size > 3) {
                                Box(
                                    modifier = Modifier
                                        .fillMaxWidth()
                                        .clip(RoundedCornerShape(12.dp))
                                        .background(LightBlue)
                                        .padding(12.dp),
                                    contentAlignment = Alignment.Center
                                ) {
                                    Text(
                                        text = "+${antecedentes1.size - 3} más",
                                        style = MaterialTheme.typography.bodySmall.copy(
                                            fontWeight = FontWeight.SemiBold,
                                            color = PrimaryBlue
                                        )
                                    )
                                }
                            }
                        }
                    }

                    Column(
                        modifier = Modifier.weight(1f),
                        verticalArrangement = Arrangement.spacedBy(8.dp)
                    ) {
                        if (antecedentes2.isEmpty()) {
                            Box(
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .clip(RoundedCornerShape(12.dp))
                                    .background(BackgroundGray)
                                    .padding(16.dp),
                                contentAlignment = Alignment.Center
                            ) {
                                Text(
                                    text = "Sin registros",
                                    style = MaterialTheme.typography.bodySmall,
                                    color = TextSecondary,
                                    textAlign = TextAlign.Center
                                )
                            }
                        } else {
                            antecedentes2.take(3).forEach { antecedente ->
                                AntecedenteCard(antecedente = antecedente)
                            }
                            if (antecedentes2.size > 3) {
                                Box(
                                    modifier = Modifier
                                        .fillMaxWidth()
                                        .clip(RoundedCornerShape(12.dp))
                                        .background(LightBlue)
                                        .padding(12.dp),
                                    contentAlignment = Alignment.Center
                                ) {
                                    Text(
                                        text = "+${antecedentes2.size - 3} más",
                                        style = MaterialTheme.typography.bodySmall.copy(
                                            fontWeight = FontWeight.SemiBold,
                                            color = PrimaryBlue
                                        )
                                    )
                                }
                            }
                        }
                    }
                }
            }
        }
    }
}

@Composable
fun AntecedenteCard(
    antecedente: com.rivera.votainformado.data.model.candidatos.Antecedente,
    modifier: Modifier = Modifier
) {
    Surface(
        modifier = modifier.fillMaxWidth(),
        shape = RoundedCornerShape(12.dp),
        color = BackgroundGray
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(12.dp),
            verticalArrangement = Arrangement.spacedBy(6.dp)
        ) {
            Text(
                text = antecedente.titulo,
                style = MaterialTheme.typography.bodySmall.copy(
                    fontWeight = FontWeight.SemiBold,
                    color = TextPrimary
                ),
                maxLines = 2
            )
            antecedente.fecha?.let {
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.spacedBy(4.dp)
                ) {
                    Icon(
                        imageVector = Icons.Outlined.CalendarToday,
                        contentDescription = null,
                        tint = TextSecondary,
                        modifier = Modifier.size(14.dp)
                    )
                    Text(
                        text = it,
                        style = MaterialTheme.typography.bodySmall,
                        color = TextSecondary
                    )
                }
            }
        }
    }
}

@Composable
fun EmptyComparisonCard() {
    Card(
        modifier = Modifier.fillMaxWidth(),
        shape = RoundedCornerShape(16.dp),
        colors = CardDefaults.cardColors(containerColor = CardWhite),
        elevation = CardDefaults.cardElevation(defaultElevation = 2.dp)
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(40.dp),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.spacedBy(16.dp)
        ) {
            Box(
                modifier = Modifier
                    .size(80.dp)
                    .clip(CircleShape)
                    .background(LightBlue),
                contentAlignment = Alignment.Center
            ) {
                Icon(
                    imageVector = Icons.Outlined.CompareArrows,
                    contentDescription = null,
                    modifier = Modifier.size(40.dp),
                    tint = PrimaryBlue
                )
            }
            Text(
                text = "Comienza la Comparación",
                style = MaterialTheme.typography.titleLarge.copy(
                    fontWeight = FontWeight.Bold,
                    color = TextPrimary
                ),
                textAlign = TextAlign.Center
            )
            Text(
                text = "Selecciona dos candidatos usando los botones de arriba para ver una comparación detallada de sus perfiles, propuestas y antecedentes",
                style = MaterialTheme.typography.bodyMedium,
                color = TextSecondary,
                textAlign = TextAlign.Center
            )
        }
    }
}

@Composable
fun CandidatoSelectorDialog(
    onCandidatoSelected: (Int) -> Unit,
    onDismiss: () -> Unit,
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
            Row(
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                Icon(
                    imageVector = Icons.Outlined.PersonSearch,
                    contentDescription = null,
                    tint = PrimaryBlue,
                    modifier = Modifier.size(24.dp)
                )
                Text(
                    text = "Seleccionar Candidato",
                    style = MaterialTheme.typography.titleLarge.copy(
                        fontWeight = FontWeight.Bold,
                        color = TextPrimary
                    )
                )
            }
        },
        text = {
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .heightIn(max = 500.dp),
                verticalArrangement = Arrangement.spacedBy(16.dp)
            ) {
                // Barra de búsqueda
                OutlinedTextField(
                    value = searchQuery,
                    onValueChange = { searchQuery = it },
                    modifier = Modifier.fillMaxWidth(),
                    placeholder = {
                        Text("Buscar por nombre o partido...")
                    },
                    leadingIcon = {
                        Icon(
                            imageVector = Icons.Outlined.Search,
                            contentDescription = null,
                            tint = PrimaryBlue
                        )
                    },
                    trailingIcon = {
                        if (searchQuery.isNotEmpty()) {
                            IconButton(onClick = { searchQuery = "" }) {
                                Icon(
                                    imageVector = Icons.Outlined.Close,
                                    contentDescription = "Limpiar",
                                    tint = TextSecondary
                                )
                            }
                        }
                    },
                    singleLine = true,
                    shape = RoundedCornerShape(12.dp),
                    colors = OutlinedTextFieldDefaults.colors(
                        focusedBorderColor = PrimaryBlue,
                        unfocusedBorderColor = BackgroundGray
                    )
                )

                // Lista de candidatos
                if (state.isLoadingCandidatos) {
                    Box(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(40.dp),
                        contentAlignment = Alignment.Center
                    ) {
                        Column(
                            horizontalAlignment = Alignment.CenterHorizontally,
                            verticalArrangement = Arrangement.spacedBy(12.dp)
                        ) {
                            CircularProgressIndicator(
                                modifier = Modifier.size(40.dp),
                                color = PrimaryBlue
                            )
                            Text(
                                text = "Cargando candidatos...",
                                style = MaterialTheme.typography.bodyMedium,
                                color = TextSecondary
                            )
                        }
                    }
                } else if (filteredCandidatos.isEmpty()) {
                    Box(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(32.dp),
                        contentAlignment = Alignment.Center
                    ) {
                        Column(
                            horizontalAlignment = Alignment.CenterHorizontally,
                            verticalArrangement = Arrangement.spacedBy(12.dp)
                        ) {
                            Icon(
                                imageVector = Icons.Outlined.SearchOff,
                                contentDescription = null,
                                modifier = Modifier.size(48.dp),
                                tint = NeutralGray
                            )
                            Text(
                                text = if (searchQuery.isNotEmpty()) {
                                    "No se encontraron candidatos"
                                } else {
                                    "No hay candidatos disponibles"
                                },
                                style = MaterialTheme.typography.bodyMedium,
                                color = TextSecondary,
                                textAlign = TextAlign.Center
                            )
                        }
                    }
                } else {
                    LazyColumn(
                        modifier = Modifier
                            .fillMaxWidth()
                            .heightIn(max = 350.dp),
                        verticalArrangement = Arrangement.spacedBy(8.dp)
                    ) {
                        items(filteredCandidatos) { candidato ->
                            CandidatoSelectorItem(
                                candidato = candidato,
                                onClick = { onCandidatoSelected(candidato.id) }
                            )
                        }
                    }
                }
            }
        },
        confirmButton = {
            TextButton(
                onClick = onDismiss,
                colors = ButtonDefaults.textButtonColors(
                    contentColor = PrimaryBlue
                )
            ) {
                Text(
                    text = "Cancelar",
                    style = MaterialTheme.typography.bodyMedium.copy(
                        fontWeight = FontWeight.SemiBold
                    )
                )
            }
        },
        containerColor = CardWhite,
        shape = RoundedCornerShape(20.dp)
    )
}

@Composable
fun CandidatoSelectorItem(
    candidato: com.rivera.votainformado.data.model.candidatos.CandidatoItem,
    onClick: () -> Unit
) {
    Surface(
        modifier = Modifier
            .fillMaxWidth()
            .clickable { onClick() },
        shape = RoundedCornerShape(12.dp),
        color = BackgroundGray
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
                    .size(56.dp)
                    .clip(CircleShape)
                    .background(LightBlue)
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

            Column(
                modifier = Modifier.weight(1f),
                verticalArrangement = Arrangement.spacedBy(4.dp)
            ) {
                Text(
                    text = candidato.nombreCompleto,
                    style = MaterialTheme.typography.bodyMedium.copy(
                        fontWeight = FontWeight.SemiBold,
                        color = TextPrimary
                    ),
                    maxLines = 1
                )
                Text(
                    text = candidato.partido.nombrePartido,
                    style = MaterialTheme.typography.bodySmall,
                    color = PrimaryBlue,
                    maxLines = 1
                )
            }

            Icon(
                imageVector = Icons.Outlined.ChevronRight,
                contentDescription = null,
                tint = NeutralGray,
                modifier = Modifier.size(24.dp)
            )
        }
    }
}