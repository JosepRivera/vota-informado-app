package com.rivera.votainformado.ui.votar

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
import androidx.compose.ui.unit.sp
import androidx.compose.ui.window.Dialog
import androidx.lifecycle.viewmodel.compose.viewModel
import coil.compose.rememberAsyncImagePainter
import com.rivera.votainformado.R
import com.rivera.votainformado.ui.theme.*

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun VotarScreen(
    onBack: () -> Unit = {}
) {
    val isDarkMode = isSystemInDarkTheme()
    val viewModel: VotarViewModel = viewModel()
    val state by viewModel.votarState.collectAsState()

    // Manejar mensajes de éxito y error
    LaunchedEffect(state.successMessage) {
        if (state.successMessage != null) {
            // El mensaje se mostrará en el Dialog de confirmación
        }
    }

    Scaffold(
        topBar = {
            TopAppBar(
                title = {
                    Text(
                        text = "Votar",
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
                colors = TopAppBarDefaults.topAppBarColors(
                    containerColor = MaterialTheme.colorScheme.surface
                )
            )
        }
    ) { innerPadding ->
        when {
            state.isLoading && state.candidatosPresidenciales.isEmpty() -> {
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
                            text = "Cargando candidatos...",
                            style = MaterialTheme.typography.bodyMedium,
                            color = if (isDarkMode) NeutralGray else NeutralMedium
                        )
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
                    // Mensaje de bienvenida
                    item {
                        InfoCard(
                            title = "Votación Simulada",
                            description = "Selecciona tus candidatos preferidos para cada cargo. Solo puedes votar una vez por cargo.",
                            isDarkMode = isDarkMode
                        )
                    }

                    // Error
                    if (state.errorMessage != null && state.successMessage == null) {
                        item {
                            ErrorCard(
                                message = state.errorMessage!!,
                                isDarkMode = isDarkMode
                            )
                        }
                    }

                    // Candidatos Presidenciales
                    item {
                        CargoSection(
                            title = "Presidente",
                            candidatos = state.candidatosPresidenciales,
                            yaVoto = state.yaVotoPresidente,
                            puedeVotar = state.puedeVotarPresidente,
                            isDarkMode = isDarkMode,
                            onCandidatoClick = { viewModel.seleccionarCandidato(it) },
                            isLoading = state.isLoading
                        )
                    }

                    // Candidatos al Senado
                    item {
                        CargoSection(
                            title = "Senador",
                            candidatos = state.candidatosSenadores,
                            yaVoto = state.yaVotoSenador,
                            puedeVotar = state.puedeVotarSenador,
                            isDarkMode = isDarkMode,
                            onCandidatoClick = { viewModel.seleccionarCandidato(it) },
                            isLoading = state.isLoading
                        )
                    }

                    // Candidatos a Diputados
                    item {
                        CargoSection(
                            title = "Diputado",
                            candidatos = state.candidatosDiputados,
                            yaVoto = state.yaVotoDiputado,
                            puedeVotar = state.puedeVotarDiputado,
                            isDarkMode = isDarkMode,
                            onCandidatoClick = { viewModel.seleccionarCandidato(it) },
                            isLoading = state.isLoading
                        )
                    }

                    // Espaciado final
                    item {
                        Spacer(modifier = Modifier.height(16.dp))
                    }
                }
            }
        }

        // Dialog de confirmación
        if (state.mostrarConfirmacion && state.candidatoSeleccionado != null) {
            ConfirmacionVotoDialog(
                candidato = state.candidatoSeleccionado!!,
                isLoading = state.isLoading,
                onConfirmar = { viewModel.confirmarVoto() },
                onCancelar = { viewModel.cancelarConfirmacion() },
                isDarkMode = isDarkMode
            )
        }

        // Dialog de éxito
        if (state.successMessage != null && !state.mostrarConfirmacion) {
            SuccessDialog(
                message = state.successMessage!!,
                onDismiss = {
                    viewModel.limpiarMensajeExito()
                },
                isDarkMode = isDarkMode
            )
        }
    }
}

@Composable
fun InfoCard(
    title: String,
    description: String,
    isDarkMode: Boolean
) {
    Card(
        modifier = Modifier.fillMaxWidth(),
        shape = RoundedCornerShape(16.dp),
        colors = CardDefaults.cardColors(
            containerColor = if (isDarkMode) DarkSurfVar else InstitutionalBlue.copy(alpha = 0.1f)
        )
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp),
            horizontalArrangement = Arrangement.spacedBy(12.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Icon(
                imageVector = Icons.Default.Info,
                contentDescription = null,
                modifier = Modifier.size(32.dp),
                tint = if (isDarkMode) InstitutionalBlueLight else InstitutionalBlue
            )
            Column(modifier = Modifier.weight(1f)) {
                Text(
                    text = title,
                    style = MaterialTheme.typography.titleMedium.copy(
                        fontWeight = FontWeight.Bold
                    ),
                    color = if (isDarkMode) InstitutionalBlueLight else InstitutionalBlue
                )
                Spacer(modifier = Modifier.height(4.dp))
                Text(
                    text = description,
                    style = MaterialTheme.typography.bodySmall,
                    color = MaterialTheme.colorScheme.onSurface
                )
            }
        }
    }
}

@Composable
fun ErrorCard(
    message: String,
    isDarkMode: Boolean
) {
    Card(
        modifier = Modifier.fillMaxWidth(),
        shape = RoundedCornerShape(16.dp),
        colors = CardDefaults.cardColors(
            containerColor = ErrorRed.copy(alpha = 0.1f)
        )
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp),
            horizontalArrangement = Arrangement.spacedBy(12.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Icon(
                imageVector = Icons.Default.Error,
                contentDescription = null,
                modifier = Modifier.size(24.dp),
                tint = ErrorRed
            )
            Text(
                text = message,
                style = MaterialTheme.typography.bodyMedium,
                color = ErrorRed
            )
        }
    }
}

@Composable
fun CargoSection(
    title: String,
    candidatos: List<com.rivera.votainformado.data.model.candidatos.CandidatoItem>,
    yaVoto: Boolean,
    puedeVotar: Boolean,
    isDarkMode: Boolean,
    onCandidatoClick: (com.rivera.votainformado.data.model.candidatos.CandidatoItem) -> Unit,
    isLoading: Boolean
) {
    Column(
        verticalArrangement = Arrangement.spacedBy(16.dp)
    ) {
        // Header de sección
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Text(
                text = title,
                style = MaterialTheme.typography.titleLarge.copy(
                    fontWeight = FontWeight.Bold,
                    color = if (isDarkMode) InstitutionalBlueLight else InstitutionalBlue
                )
            )
            if (yaVoto) {
                Surface(
                    shape = RoundedCornerShape(12.dp),
                    color = CivicGreen.copy(alpha = 0.2f)
                ) {
                    Row(
                        modifier = Modifier.padding(horizontal = 12.dp, vertical = 6.dp),
                        horizontalArrangement = Arrangement.spacedBy(6.dp),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Icon(
                            imageVector = Icons.Default.CheckCircle,
                            contentDescription = null,
                            modifier = Modifier.size(16.dp),
                            tint = CivicGreen
                        )
                        Text(
                            text = "Votado",
                            style = MaterialTheme.typography.labelSmall.copy(
                                fontWeight = FontWeight.Bold
                            ),
                            color = CivicGreen
                        )
                    }
                }
            }
        }

        if (!puedeVotar && !yaVoto) {
            Text(
                text = "No puedes votar por este cargo",
                style = MaterialTheme.typography.bodyMedium,
                color = ErrorRed,
                modifier = Modifier.padding(top = 4.dp)
            )
        } else if (yaVoto) {
            Card(
                modifier = Modifier.fillMaxWidth(),
                shape = RoundedCornerShape(16.dp),
                colors = CardDefaults.cardColors(
                    containerColor = if (isDarkMode) DarkSurfVar else NeutralLight
                )
            ) {
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(16.dp),
                    horizontalArrangement = Arrangement.spacedBy(12.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Icon(
                        imageVector = Icons.Default.CheckCircle,
                        contentDescription = null,
                        tint = CivicGreen,
                        modifier = Modifier.size(32.dp)
                    )
                    Text(
                        text = "Ya has emitido tu voto para este cargo",
                        style = MaterialTheme.typography.bodyMedium,
                        color = MaterialTheme.colorScheme.onSurface
                    )
                }
            }
        } else if (candidatos.isEmpty()) {
            Card(
                modifier = Modifier.fillMaxWidth(),
                shape = RoundedCornerShape(16.dp),
                colors = CardDefaults.cardColors(
                    containerColor = if (isDarkMode) DarkSurfVar else NeutralLight
                )
            ) {
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(16.dp),
                    horizontalArrangement = Arrangement.spacedBy(12.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Icon(
                        imageVector = Icons.Default.Info,
                        contentDescription = null,
                        tint = if (isDarkMode) NeutralGray else NeutralMedium,
                        modifier = Modifier.size(32.dp)
                    )
                    Text(
                        text = "No hay candidatos disponibles",
                        style = MaterialTheme.typography.bodyMedium,
                        color = if (isDarkMode) NeutralGray else NeutralMedium
                    )
                }
            }
        } else {
            candidatos.forEach { candidato ->
                CandidatoVotoCard(
                    candidato = candidato,
                    isDarkMode = isDarkMode,
                    onClick = { if (puedeVotar) onCandidatoClick(candidato) },
                    enabled = puedeVotar && !isLoading
                )
            }
        }
    }
}

@Composable
fun CandidatoVotoCard(
    candidato: com.rivera.votainformado.data.model.candidatos.CandidatoItem,
    isDarkMode: Boolean,
    onClick: () -> Unit,
    enabled: Boolean = true
) {
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .clickable(enabled = enabled) { onClick() },
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
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.spacedBy(16.dp)
        ) {
            // Foto del candidato
            Box(
                modifier = Modifier
                    .size(70.dp)
                    .clip(RoundedCornerShape(14.dp))
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

            Column(
                modifier = Modifier.weight(1f),
                verticalArrangement = Arrangement.spacedBy(6.dp)
            ) {
                Text(
                    text = candidato.nombreCompleto,
                    style = MaterialTheme.typography.titleMedium.copy(
                        fontWeight = FontWeight.Bold
                    ),
                    maxLines = 2
                )
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.spacedBy(6.dp)
                ) {
                    Box(
                        modifier = Modifier
                            .size(8.dp)
                            .clip(CircleShape)
                            .background(if (isDarkMode) InstitutionalBlueLight else InstitutionalBlue)
                    )
                    Text(
                        text = candidato.partido.nombrePartido,
                        style = MaterialTheme.typography.bodyMedium,
                        color = if (isDarkMode) InstitutionalBlueLight else InstitutionalBlue,
                        maxLines = 1
                    )
                }
                if (candidato.totalVotos != null && candidato.totalVotos > 0) {
                    Row(
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.spacedBy(4.dp)
                    ) {
                        Icon(
                            imageVector = Icons.Default.HowToVote,
                            contentDescription = null,
                            modifier = Modifier.size(14.dp),
                            tint = CivicGreen
                        )
                        Text(
                            text = "${candidato.totalVotos} votos",
                            style = MaterialTheme.typography.bodySmall,
                            color = CivicGreen
                        )
                    }
                }
            }

            if (enabled) {
                Icon(
                    imageVector = Icons.Default.ChevronRight,
                    contentDescription = null,
                    tint = if (isDarkMode) NeutralGray else NeutralMedium,
                    modifier = Modifier.size(24.dp)
                )
            }
        }
    }
}

@Composable
fun ConfirmacionVotoDialog(
    candidato: com.rivera.votainformado.data.model.candidatos.CandidatoItem,
    isLoading: Boolean,
    onConfirmar: () -> Unit,
    onCancelar: () -> Unit,
    isDarkMode: Boolean
) {
    Dialog(onDismissRequest = { if (!isLoading) onCancelar() }) {
        Card(
            modifier = Modifier.fillMaxWidth(),
            shape = RoundedCornerShape(24.dp),
            colors = CardDefaults.cardColors(
                containerColor = MaterialTheme.colorScheme.surface
            )
        ) {
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(24.dp),
                horizontalAlignment = Alignment.CenterHorizontally,
                verticalArrangement = Arrangement.spacedBy(20.dp)
            ) {
                Text(
                    text = "Confirmar Voto",
                    style = MaterialTheme.typography.headlineSmall.copy(
                        fontWeight = FontWeight.Bold
                    )
                )

                // Información del candidato
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.spacedBy(16.dp)
                ) {
                    Box(
                        modifier = Modifier
                            .size(80.dp)
                            .clip(RoundedCornerShape(16.dp))
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

                    Column(modifier = Modifier.weight(1f)) {
                        Text(
                            text = candidato.nombreCompleto,
                            style = MaterialTheme.typography.titleMedium.copy(
                                fontWeight = FontWeight.Bold
                            )
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

                Text(
                    text = "¿Estás seguro de que deseas votar por este candidato?",
                    style = MaterialTheme.typography.bodyMedium,
                    color = if (isDarkMode) NeutralGray else NeutralMedium
                )

                // Botones
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(12.dp)
                ) {
                    OutlinedButton(
                        onClick = onCancelar,
                        modifier = Modifier.weight(1f),
                        enabled = !isLoading
                    ) {
                        Text("Cancelar")
                    }
                    Button(
                        onClick = onConfirmar,
                        modifier = Modifier.weight(1f),
                        enabled = !isLoading,
                        colors = ButtonDefaults.buttonColors(
                            containerColor = if (isDarkMode) InstitutionalBlueLight else InstitutionalBlue
                        )
                    ) {
                        if (isLoading) {
                            CircularProgressIndicator(
                                modifier = Modifier.size(20.dp),
                                color = NeutralWhite,
                                strokeWidth = 2.dp
                            )
                        } else {
                            Text("Confirmar")
                        }
                    }
                }
            }
        }
    }
}

@Composable
fun SuccessDialog(
    message: String,
    onDismiss: () -> Unit,
    isDarkMode: Boolean
) {
    Dialog(onDismissRequest = onDismiss) {
        Card(
            modifier = Modifier.fillMaxWidth(),
            shape = RoundedCornerShape(24.dp),
            colors = CardDefaults.cardColors(
                containerColor = MaterialTheme.colorScheme.surface
            )
        ) {
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(24.dp),
                horizontalAlignment = Alignment.CenterHorizontally,
                verticalArrangement = Arrangement.spacedBy(20.dp)
            ) {
                Icon(
                    imageVector = Icons.Default.CheckCircle,
                    contentDescription = null,
                    modifier = Modifier.size(64.dp),
                    tint = CivicGreen
                )
                Text(
                    text = "¡Voto Registrado!",
                    style = MaterialTheme.typography.headlineSmall.copy(
                        fontWeight = FontWeight.Bold
                    )
                )
                Text(
                    text = message,
                    style = MaterialTheme.typography.bodyMedium,
                    color = MaterialTheme.colorScheme.onSurface
                )
                Button(
                    onClick = onDismiss,
                    modifier = Modifier.fillMaxWidth(),
                    colors = ButtonDefaults.buttonColors(
                        containerColor = if (isDarkMode) InstitutionalBlueLight else InstitutionalBlue
                    )
                ) {
                    Text("Aceptar")
                }
            }
        }
    }
}
