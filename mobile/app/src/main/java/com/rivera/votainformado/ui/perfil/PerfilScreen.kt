package com.rivera.votainformado.ui.perfil

import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
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
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.platform.LocalContext
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.ViewModel
import coil.compose.rememberAsyncImagePainter
import com.google.accompanist.swiperefresh.SwipeRefresh
import com.google.accompanist.swiperefresh.rememberSwipeRefreshState
import com.rivera.votainformado.R
import com.rivera.votainformado.ui.theme.*
import com.rivera.votainformado.util.TokenManager

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun PerfilScreen(
    onBack: () -> Unit = {},
    onNavigateToDetail: (Int) -> Unit = {},
    onLogout: () -> Unit = {},
    onNavigateToWelcome: () -> Unit = {}
) {
    val context = LocalContext.current
    val tokenManager = remember { TokenManager(context) }

    val viewModel: PerfilViewModel = viewModel(
        factory = object : ViewModelProvider.Factory {
            override fun <T : ViewModel> create(modelClass: Class<T>): T {
                @Suppress("UNCHECKED_CAST")
                return PerfilViewModel(tokenManager) as T
            }
        }
    )
    val state by viewModel.perfilState.collectAsState()

    Scaffold(
        topBar = {
            TopAppBar(
                title = {
                    Text(
                        text = "Mi Perfil",
                        style = MaterialTheme.typography.titleLarge.copy(
                            fontWeight = FontWeight.Bold,
                            color = InstitutionalBlue
                        )
                    )
                },
                navigationIcon = {
                    IconButton(onClick = onBack) {
                        Icon(
                            imageVector = Icons.Default.ArrowBack,
                            contentDescription = "Volver",
                            tint = InstitutionalBlue
                        )
                    }
                },
                colors = TopAppBarDefaults.topAppBarColors(
                    containerColor = MaterialTheme.colorScheme.surface
                )
            )
        }
    ) { innerPadding ->
        val esInvitado = tokenManager.getAccessToken() == null
        val swipeRefreshState = rememberSwipeRefreshState(isRefreshing = state.isLoading && state.perfil != null)

        when {
            esInvitado -> {
                LazyColumn(
                    modifier = Modifier
                        .fillMaxSize()
                        .padding(innerPadding)
                        .background(MaterialTheme.colorScheme.background),
                    contentPadding = PaddingValues(20.dp),
                    verticalArrangement = Arrangement.spacedBy(24.dp)
                ) {
                    item {
                        Card(
                            modifier = Modifier.fillMaxWidth(),
                            shape = RoundedCornerShape(20.dp),
                            colors = CardDefaults.cardColors(
                                containerColor = NeutralWhite
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
                                Box(
                                    modifier = Modifier
                                        .size(120.dp)
                                        .clip(CircleShape)
                                        .background(ErrorRed.copy(alpha = 0.1f))
                                        .border(
                                            width = 3.dp,
                                            color = ErrorRed.copy(alpha = 0.3f),
                                            shape = CircleShape
                                        ),
                                    contentAlignment = Alignment.Center
                                ) {
                                    Icon(
                                        imageVector = Icons.Default.AccountCircle,
                                        contentDescription = null,
                                        modifier = Modifier.size(80.dp),
                                        tint = ErrorRed
                                    )
                                }

                                Spacer(modifier = Modifier.height(4.dp))

                                Text(
                                    text = "Modo Invitado",
                                    style = MaterialTheme.typography.headlineSmall.copy(
                                        fontWeight = FontWeight.Bold
                                    ),
                                    color = MaterialTheme.colorScheme.onSurface
                                )
                            }
                        }
                    }

                    item {
                        Card(
                            modifier = Modifier.fillMaxWidth(),
                            shape = RoundedCornerShape(16.dp),
                            colors = CardDefaults.cardColors(
                                containerColor = ErrorRed.copy(alpha = 0.1f)
                            )
                        ) {
                            Column(
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .padding(20.dp),
                                verticalArrangement = Arrangement.spacedBy(12.dp)
                            ) {
                                Row(
                                    verticalAlignment = Alignment.CenterVertically,
                                    horizontalArrangement = Arrangement.spacedBy(12.dp)
                                ) {
                                    Icon(
                                        imageVector = Icons.Default.Info,
                                        contentDescription = null,
                                        tint = ErrorRed,
                                        modifier = Modifier.size(28.dp)
                                    )
                                    Text(
                                        text = "Estás navegando como invitado",
                                        style = MaterialTheme.typography.titleMedium.copy(
                                            fontWeight = FontWeight.Bold
                                        ),
                                        color = ErrorRed
                                    )
                                }

                                Text(
                                    text = "Como invitado puedes explorar la información de los candidatos, pero no podrás emitir votos. Para votar, necesitas registrarte e iniciar sesión.",
                                    style = MaterialTheme.typography.bodyMedium,
                                    color = ErrorRed.copy(alpha = 0.8f)
                                )
                            }
                        }
                    }

                    item {
                        Card(
                            modifier = Modifier.fillMaxWidth(),
                            shape = RoundedCornerShape(16.dp),
                            colors = CardDefaults.cardColors(
                                containerColor = NeutralLight
                            )
                        ) {
                            Column(
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .padding(20.dp),
                                verticalArrangement = Arrangement.spacedBy(12.dp)
                            ) {
                                Text(
                                    text = "¿Qué puedes hacer?",
                                    style = MaterialTheme.typography.titleMedium.copy(
                                        fontWeight = FontWeight.Bold
                                    ),
                                    color = MaterialTheme.colorScheme.onSurface
                                )

                                InfoRowText(
                                    icon = Icons.Default.Search,
                                    text = "Ver información de candidatos"
                                )
                                InfoRowText(
                                    icon = Icons.Default.Compare,
                                    text = "Comparar candidatos"
                                )
                                InfoRowText(
                                    icon = Icons.Default.Leaderboard,
                                    text = "Ver resultados de la votación"
                                )
                            }
                        }
                    }

                    item {
                        Card(
                            modifier = Modifier.fillMaxWidth(),
                            shape = RoundedCornerShape(16.dp),
                            colors = CardDefaults.cardColors(
                                containerColor = NeutralLight
                            )
                        ) {
                            Column(
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .padding(20.dp),
                                verticalArrangement = Arrangement.spacedBy(12.dp)
                            ) {
                                Text(
                                    text = "Para votar necesitas:",
                                    style = MaterialTheme.typography.titleMedium.copy(
                                        fontWeight = FontWeight.Bold
                                    ),
                                    color = MaterialTheme.colorScheme.onSurface
                                )

                                InfoRowText(
                                    icon = Icons.Default.AccountCircle,
                                    text = "Registrarte con tu DNI"
                                )
                                InfoRowText(
                                    icon = Icons.Default.Lock,
                                    text = "Iniciar sesión"
                                )
                                InfoRowText(
                                    icon = Icons.Default.HowToVote,
                                    text = "Emitir tu voto"
                                )
                            }
                        }
                    }

                    item {
                        Button(
                            onClick = onNavigateToWelcome,
                            modifier = Modifier.fillMaxWidth(),
                            colors = ButtonDefaults.buttonColors(
                                containerColor = InstitutionalBlue
                            )
                        ) {
                            Icon(
                                imageVector = Icons.Default.Login,
                                contentDescription = null,
                                modifier = Modifier.size(20.dp)
                            )
                            Spacer(modifier = Modifier.width(8.dp))
                            Text("Registrarse o Iniciar Sesión")
                        }
                    }

                    item {
                        Spacer(modifier = Modifier.height(16.dp))
                    }
                }
            }
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
                            color = InstitutionalBlue,
                            modifier = Modifier.size(48.dp)
                        )
                        Text(
                            text = "Cargando perfil...",
                            style = MaterialTheme.typography.bodyMedium,
                            color = NeutralMedium
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
                                containerColor = InstitutionalBlue
                            )
                        ) {
                            Text("Reintentar")
                        }
                    }
                }
            }
            else -> {
                SwipeRefresh(
                    state = swipeRefreshState,
                    onRefresh = { viewModel.refrescar() },
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
                    item {
                        PerfilHeaderCard(perfil = state.perfil)
                    }

                    if (state.perfil?.rol?.equals("invitado", ignoreCase = true) == true && !state.isLoading) {
                        item {
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
                                    verticalAlignment = Alignment.CenterVertically,
                                    horizontalArrangement = Arrangement.spacedBy(12.dp)
                                ) {
                                    Icon(
                                        imageVector = Icons.Default.Info,
                                        contentDescription = null,
                                        tint = ErrorRed,
                                        modifier = Modifier.size(24.dp)
                                    )
                                    Column(modifier = Modifier.weight(1f)) {
                                        Text(
                                            text = "Modo Invitado",
                                            style = MaterialTheme.typography.titleMedium.copy(
                                                fontWeight = FontWeight.Bold
                                            ),
                                            color = ErrorRed
                                        )
                                        Text(
                                            text = "Estás navegando como invitado. Regístrate para poder votar.",
                                            style = MaterialTheme.typography.bodySmall,
                                            color = ErrorRed.copy(alpha = 0.8f)
                                        )
                                    }
                                }
                            }
                        }
                    }

                    item {
                        EstadisticasRapidas(
                            totalVotos = state.misVotos.size,
                            isLoading = state.isLoadingVotos
                        )
                    }

                    item {
                        Text(
                            text = "Mis Votos",
                            style = MaterialTheme.typography.titleLarge.copy(
                                fontWeight = FontWeight.Bold,
                                color = InstitutionalBlue
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
                                    color = InstitutionalBlue,
                                    modifier = Modifier.size(32.dp)
                                )
                            }
                        }
                    }
                    
                    // SIEMPRE mostrar la lista de votos
                    if (state.misVotos.isEmpty() && !state.isLoadingVotos) {
                        item {
                            EmptyStateCard(
                                icon = Icons.Default.HowToVote,
                                title = "Aún no has votado",
                                description = "Participa en la votación para ver tus votos aquí"
                            )
                        }
                    }
                    
                    // Mostrar votos si existen
                    items(state.misVotos) { voto ->
                        VotoCard(
                            voto = voto,
                            onClick = { onNavigateToDetail(voto.candidato.id) },
                            modifier = Modifier.fillMaxWidth()
                        )
                    }

                    item {
                        OutlinedButton(
                            onClick = { viewModel.refrescar() },
                            modifier = Modifier.fillMaxWidth(),
                            colors = ButtonDefaults.outlinedButtonColors(
                                contentColor = InstitutionalBlue
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

                    item {
                        Button(
                            onClick = {
                                viewModel.logout(context)
                                onLogout()
                            },
                            modifier = Modifier.fillMaxWidth(),
                            colors = ButtonDefaults.buttonColors(
                                containerColor = ErrorRed
                            )
                        ) {
                            Icon(
                                imageVector = Icons.Default.ExitToApp,
                                contentDescription = null,
                                modifier = Modifier.size(20.dp)
                            )
                            Spacer(modifier = Modifier.width(8.dp))
                            Text("Cerrar Sesión")
                        }
                    }

                        item {
                            Spacer(modifier = Modifier.height(16.dp))
                        }
                    }
                }
            }
        }
    }
}

@Composable
fun PerfilHeaderCard(
    perfil: com.rivera.votainformado.data.model.auth.PerfilResponse?
) {
    Card(
        modifier = Modifier.fillMaxWidth(),
        shape = RoundedCornerShape(20.dp),
        colors = CardDefaults.cardColors(
            containerColor = NeutralWhite
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
            Box(
                modifier = Modifier
                    .size(120.dp)
                    .clip(CircleShape)
                    .background(InstitutionalBlue.copy(alpha = 0.1f))
                    .border(
                        width = 3.dp,
                        color = InstitutionalBlue.copy(alpha = 0.3f),
                        shape = CircleShape
                    ),
                contentAlignment = Alignment.Center
            ) {
                Icon(
                    imageVector = Icons.Default.AccountCircle,
                    contentDescription = null,
                    modifier = Modifier.size(80.dp),
                    tint = InstitutionalBlue
                )
            }

            Spacer(modifier = Modifier.height(4.dp))

            Text(
                text = perfil?.nombreCompleto ?: "Usuario",
                style = MaterialTheme.typography.headlineSmall.copy(
                    fontWeight = FontWeight.Bold
                ),
                color = MaterialTheme.colorScheme.onSurface
            )

            Column(
                modifier = Modifier.fillMaxWidth(),
                verticalArrangement = Arrangement.spacedBy(12.dp)
            ) {
                InfoRow(
                    icon = Icons.Default.Badge,
                    label = "DNI",
                    value = perfil?.dni ?: "-"
                )
                InfoRow(
                    icon = Icons.Default.LocationOn,
                    label = "Región",
                    value = perfil?.region?.let { regionToString(it) } ?: "-"
                )
                InfoRow(
                    icon = Icons.Default.CalendarToday,
                    label = "Registrado",
                    value = perfil?.createdAt?.let { formatDate(it) } ?: "-"
                )
            }
        }
    }
}

@Composable
fun InfoRow(
    icon: ImageVector,
    label: String,
    value: String
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
            tint = InstitutionalBlue
        )
        Column(modifier = Modifier.weight(1f)) {
            Text(
                text = label,
                style = MaterialTheme.typography.bodySmall,
                color = NeutralMedium
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
    isLoading: Boolean
) {
    Card(
        modifier = Modifier.fillMaxWidth(),
        shape = RoundedCornerShape(16.dp),
        colors = CardDefaults.cardColors(
            containerColor = InstitutionalBlue.copy(alpha = 0.1f)
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
                value = if (isLoading) "..." else totalVotos.toString()
            )
        }
    }
}

@Composable
fun StatItem(
    icon: ImageVector,
    label: String,
    value: String
) {
    Column(
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.spacedBy(8.dp)
    ) {
        Icon(
            imageVector = icon,
            contentDescription = null,
            modifier = Modifier.size(32.dp),
            tint = InstitutionalBlue
        )
        Text(
            text = value,
            style = MaterialTheme.typography.headlineMedium.copy(
                fontWeight = FontWeight.Bold
            ),
            color = InstitutionalBlue
        )
        Text(
            text = label,
            style = MaterialTheme.typography.bodySmall,
            color = NeutralMedium
        )
    }
}

@Composable
fun VotoCard(
    voto: com.rivera.votainformado.data.model.votos.Voto,
    onClick: () -> Unit,
    modifier: Modifier = Modifier
) {
    Card(
        modifier = modifier
            .fillMaxWidth()
            .clickable { onClick() },
        shape = RoundedCornerShape(16.dp),
        colors = CardDefaults.cardColors(
            containerColor = NeutralWhite
        ),
        elevation = CardDefaults.cardElevation(defaultElevation = 2.dp)
    ) {
        Row(
            modifier = Modifier
                .fillMaxSize()
                .padding(16.dp),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.spacedBy(16.dp)
        ) {
            Box(
                modifier = Modifier
                    .size(64.dp)
                    .clip(RoundedCornerShape(12.dp))
                    .background(NeutralLight)
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
                    color = InstitutionalBlue
                )
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.spacedBy(4.dp)
                ) {
                    Icon(
                        imageVector = Icons.Default.Work,
                        contentDescription = null,
                        modifier = Modifier.size(14.dp),
                        tint = NeutralMedium
                    )
                    Text(
                        text = voto.cargo,
                        style = MaterialTheme.typography.bodySmall,
                        color = NeutralMedium
                    )
                }
            }

            Icon(
                imageVector = Icons.Default.ChevronRight,
                contentDescription = null,
                tint = NeutralMedium,
                modifier = Modifier.size(24.dp)
            )
        }
    }
}

@Composable
fun EmptyStateCard(
    icon: ImageVector,
    title: String,
    description: String
) {
    Card(
        modifier = Modifier.fillMaxWidth(),
        shape = RoundedCornerShape(16.dp),
        colors = CardDefaults.cardColors(
            containerColor = NeutralLight
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
                tint = NeutralMedium
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
                color = NeutralMedium
            )
        }
    }
}

fun regionToString(region: com.rivera.votainformado.data.model.core.Region): String {
    return region.nombreRegion ?: "-"
}

fun formatDate(dateString: String): String {
    return try {
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

@Composable
fun InfoRowText(
    icon: ImageVector,
    text: String
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
            tint = InstitutionalBlue
        )
        Text(
            text = text,
            style = MaterialTheme.typography.bodyMedium,
            color = MaterialTheme.colorScheme.onSurface
        )
    }
}