package com.rivera.votainformado.ui.perfil

import androidx.compose.foundation.Image
import androidx.compose.foundation.background
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
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.viewmodel.compose.viewModel
import coil.compose.rememberAsyncImagePainter
import com.rivera.votainformado.R
import com.rivera.votainformado.ui.theme.*

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun PerfilScreen(
    onBack: () -> Unit = {},
    onNavigateToDetail: (Int) -> Unit = {}
) {
    val isDarkMode = isSystemInDarkTheme()
    val viewModel: PerfilViewModel = viewModel()
    val state by viewModel.perfilState.collectAsState()

    Scaffold(
        topBar = {
            TopAppBar(
                title = {
                    Text(
                        text = "Mi Perfil",
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
            state.isLoading && state.perfil == null -> {
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
                            text = "Cargando perfil...",
                            style = MaterialTheme.typography.bodyMedium,
                            color = if (isDarkMode) NeutralGray else NeutralMedium
                        )
                    }
                }
            }
            state.errorMessage != null && state.perfil == null -> {
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
                            text = state.errorMessage ?: "Error al cargar perfil",
                            style = MaterialTheme.typography.bodyLarge,
                            color = ErrorRed
                        )
                        Button(
                            onClick = { viewModel.loadPerfil() },
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
                    // Información del usuario
                    item {
                        PerfilHeaderCard(
                            perfil = state.perfil,
                            isDarkMode = isDarkMode
                        )
                    }

                    // Estadísticas rápidas
                    item {
                        EstadisticasRapidas(
                            totalVotos = state.misVotos.size,
                            isLoading = state.isLoadingVotos,
                            isDarkMode = isDarkMode
                        )
                    }

                    // Mis votos
                    item {
                        Text(
                            text = "Mis Votos",
                            style = MaterialTheme.typography.titleLarge.copy(
                                fontWeight = FontWeight.Bold,
                                color = if (isDarkMode) InstitutionalBlueLight else InstitutionalBlue
                            ),
                            modifier = Modifier.padding(top = 8.dp, bottom = 8.dp)
                        )
                    }

                    if (state.isLoadingVotos) {
                        item {
                            Box(
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .padding(32.dp),
                                contentAlignment = Alignment.Center
                            ) {
                                CircularProgressIndicator(
                                    color = if (isDarkMode) InstitutionalBlueLight else InstitutionalBlue,
                                    modifier = Modifier.size(32.dp)
                                )
                            }
                        }
                    } else if (state.misVotos.isEmpty()) {
                        item {
                            EmptyStateCard(
                                icon = Icons.Default.HowToVote,
                                title = "Aún no has votado",
                                description = "Participa en la votación para ver tus votos aquí",
                                isDarkMode = isDarkMode
                            )
                        }
                    } else {
                        items(state.misVotos) { voto ->
                            VotoCard(
                                voto = voto,
                                isDarkMode = isDarkMode,
                                onClick = { onNavigateToDetail(voto.candidato.id) },
                                modifier = Modifier.fillMaxWidth()
                            )
                        }
                    }

                    // Botón de refrescar
                    item {
                        OutlinedButton(
                            onClick = { viewModel.refrescar() },
                            modifier = Modifier.fillMaxWidth(),
                            colors = ButtonDefaults.outlinedButtonColors(
                                contentColor = if (isDarkMode) InstitutionalBlueLight else InstitutionalBlue
                            )
                        ) {
                            Icon(
                                imageVector = Icons.Default.Refresh,
                                contentDescription = null,
                                modifier = Modifier.size(20.dp)
                            )
                            Spacer(modifier = Modifier.width(8.dp))
                            Text("Actualizar")
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
fun PerfilHeaderCard(
    perfil: com.rivera.votainformado.data.model.auth.PerfilResponse?,
    isDarkMode: Boolean
) {
    Card(
        modifier = Modifier.fillMaxWidth(),
        shape = RoundedCornerShape(20.dp),
        colors = CardDefaults.cardColors(
            containerColor = if (isDarkMode) DarkSurfVar else NeutralWhite
        ),
        elevation = CardDefaults.cardElevation(defaultElevation = 4.dp)
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(24.dp),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.spacedBy(16.dp)
        ) {
            // Avatar
            Box(
                modifier = Modifier
                    .size(100.dp)
                    .clip(CircleShape)
                    .background(
                        if (isDarkMode) InstitutionalBlueDark else InstitutionalBlue.copy(alpha = 0.1f)
                    )
                    .shadow(8.dp, CircleShape),
                contentAlignment = Alignment.Center
            ) {
                Icon(
                    imageVector = Icons.Default.AccountCircle,
                    contentDescription = null,
                    modifier = Modifier.size(80.dp),
                    tint = if (isDarkMode) InstitutionalBlueLight else InstitutionalBlue
                )
            }

            Spacer(modifier = Modifier.height(4.dp))

            // Nombre completo
            Text(
                text = perfil?.nombreCompleto ?: "Usuario",
                style = MaterialTheme.typography.headlineSmall.copy(
                    fontWeight = FontWeight.Bold
                ),
                color = MaterialTheme.colorScheme.onSurface
            )

            // Información adicional
            Column(
                modifier = Modifier.fillMaxWidth(),
                verticalArrangement = Arrangement.spacedBy(12.dp)
            ) {
                InfoRow(
                    icon = Icons.Default.Badge,
                    label = "DNI",
                    value = perfil?.dni ?: "-",
                    isDarkMode = isDarkMode
                )
                InfoRow(
                    icon = Icons.Default.LocationOn,
                    label = "Región",
                    value = perfil?.region?.let { regionToString(it) } ?: "-",
                    isDarkMode = isDarkMode
                )
                InfoRow(
                    icon = Icons.Default.CalendarToday,
                    label = "Registrado",
                    value = perfil?.createdAt?.let { formatDate(it) } ?: "-",
                    isDarkMode = isDarkMode
                )
            }
        }
    }
}

@Composable
fun InfoRow(
    icon: ImageVector,
    label: String,
    value: String,
    isDarkMode: Boolean
) {
    Row(
        modifier = Modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.spacedBy(12.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        Icon(
            imageVector = icon,
            contentDescription = null,
            modifier = Modifier.size(20.dp),
            tint = if (isDarkMode) InstitutionalBlueLight else InstitutionalBlue
        )
        Column(modifier = Modifier.weight(1f)) {
            Text(
                text = label,
                style = MaterialTheme.typography.bodySmall,
                color = if (isDarkMode) NeutralGray else NeutralMedium
            )
            Text(
                text = value,
                style = MaterialTheme.typography.bodyLarge.copy(
                    fontWeight = FontWeight.Medium
                ),
                color = MaterialTheme.colorScheme.onSurface
            )
        }
    }
}

@Composable
fun EstadisticasRapidas(
    totalVotos: Int,
    isLoading: Boolean,
    isDarkMode: Boolean
) {
    Card(
        modifier = Modifier.fillMaxWidth(),
        shape = RoundedCornerShape(16.dp),
        colors = CardDefaults.cardColors(
            containerColor = if (isDarkMode) DarkSurf else InstitutionalBlue.copy(alpha = 0.1f)
        )
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(20.dp),
            horizontalArrangement = Arrangement.SpaceEvenly,
            verticalAlignment = Alignment.CenterVertically
        ) {
            StatItem(
                icon = Icons.Default.HowToVote,
                label = "Votos Emitidos",
                value = if (isLoading) "..." else totalVotos.toString(),
                isDarkMode = isDarkMode
            )
        }
    }
}

@Composable
fun StatItem(
    icon: ImageVector,
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
            style = MaterialTheme.typography.headlineMedium.copy(
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
fun VotoCard(
    voto: com.rivera.votainformado.data.model.votos.Voto,
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
                    .size(64.dp)
                    .clip(RoundedCornerShape(12.dp))
                    .background(if (isDarkMode) DarkSurf else NeutralLight)
            ) {
                Image(
                    painter = rememberAsyncImagePainter(
                        model = voto.candidato.fotoUrl ?: voto.candidato.partido.logoUrl.orEmpty(),
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
                    text = voto.candidato.nombreCompleto,
                    style = MaterialTheme.typography.titleMedium.copy(
                        fontWeight = FontWeight.Bold
                    ),
                    maxLines = 2
                )
                Text(
                    text = voto.candidato.partido.nombrePartido,
                    style = MaterialTheme.typography.bodyMedium,
                    color = if (isDarkMode) InstitutionalBlueLight else InstitutionalBlue
                )
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.spacedBy(4.dp)
                ) {
                    Icon(
                        imageVector = Icons.Default.Work,
                        contentDescription = null,
                        modifier = Modifier.size(14.dp),
                        tint = if (isDarkMode) NeutralGray else NeutralMedium
                    )
                    Text(
                        text = voto.cargo.nombreCargo,
                        style = MaterialTheme.typography.bodySmall,
                        color = if (isDarkMode) NeutralGray else NeutralMedium
                    )
                }
            }

            Icon(
                imageVector = Icons.Default.ChevronRight,
                contentDescription = null,
                tint = if (isDarkMode) NeutralGray else NeutralMedium,
                modifier = Modifier.size(24.dp)
            )
        }
    }
}

@Composable
fun EmptyStateCard(
    icon: ImageVector,
    title: String,
    description: String,
    isDarkMode: Boolean
) {
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
                imageVector = icon,
                contentDescription = null,
                modifier = Modifier.size(64.dp),
                tint = if (isDarkMode) NeutralGray else NeutralMedium
            )
            Text(
                text = title,
                style = MaterialTheme.typography.titleMedium.copy(
                    fontWeight = FontWeight.Bold
                ),
                color = MaterialTheme.colorScheme.onSurface
            )
            Text(
                text = description,
                style = MaterialTheme.typography.bodyMedium,
                color = if (isDarkMode) NeutralGray else NeutralMedium
            )
        }
    }
}

fun regionToString(region: com.rivera.votainformado.data.model.core.Region): String {
    return region.nombreRegion ?: "-"
}

fun formatDate(dateString: String): String {
    return try {
        // Formato simple: "2024-01-15" -> "15/01/2024"
        val parts = dateString.split("T")[0].split("-")
        if (parts.size == 3) {
            "${parts[2]}/${parts[1]}/${parts[0]}"
        } else {
            dateString
        }
    } catch (e: Exception) {
        dateString
    }
}
