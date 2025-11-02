package com.rivera.votainformado.ui.comparar

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
import com.rivera.votainformado.R
import com.rivera.votainformado.ui.theme.*

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun CompararScreen(
    onBack: () -> Unit = {},
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
        }
    ) { innerPadding ->
        LazyColumn(
            modifier = Modifier
                .fillMaxSize()
                .padding(innerPadding)
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

            // Comparación
            if (state.candidato1 != null && state.candidato2 != null) {
                item {
                    ComparacionHeader(
                        candidato1 = state.candidato1!!,
                        candidato2 = state.candidato2!!,
                        isDarkMode = isDarkMode,
                        onNavigateToDetail = onNavigateToDetail
                    )
                }

                item {
                    ComparacionBasica(
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
                        isDarkMode = isDarkMode
                    )
                }

                item {
                    ComparacionAntecedentes(
                        titulo = "Proyectos de Ley",
                        antecedentes1 = state.candidato1!!.proyectos ?: emptyList(),
                        antecedentes2 = state.candidato2!!.proyectos ?: emptyList(),
                        isDarkMode = isDarkMode
                    )
                }

                item {
                    ComparacionAntecedentes(
                        titulo = "Propuestas",
                        antecedentes1 = state.candidato1!!.propuestas ?: emptyList(),
                        antecedentes2 = state.candidato2!!.propuestas ?: emptyList(),
                        isDarkMode = isDarkMode
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
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                Text(
                    text = titulo,
                    style = MaterialTheme.typography.titleMedium.copy(
                        fontWeight = FontWeight.Bold,
                        color = if (isDarkMode) InstitutionalBlueLight else InstitutionalBlue
                    )
                )
                Row(horizontalArrangement = Arrangement.spacedBy(16.dp)) {
                    Text(
                        text = "${antecedentes1.size}",
                        style = MaterialTheme.typography.titleMedium.copy(
                            fontWeight = FontWeight.Bold
                        )
                    )
                    Text(
                        text = "vs",
                        style = MaterialTheme.typography.bodySmall,
                        color = if (isDarkMode) NeutralGray else NeutralMedium
                    )
                    Text(
                        text = "${antecedentes2.size}",
                        style = MaterialTheme.typography.titleMedium.copy(
                            fontWeight = FontWeight.Bold
                        )
                    )
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
