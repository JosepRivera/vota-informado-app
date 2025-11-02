package com.rivera.votainformado.ui.candidatos

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
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import coil.compose.rememberAsyncImagePainter
import com.google.gson.JsonElement
import com.google.gson.JsonObject
import com.google.gson.JsonPrimitive
import com.rivera.votainformado.ui.theme.*

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun CandidatoDetailScreen(
    candidateId: Int,
    onBack: () -> Unit
) {
    val isDarkMode = isSystemInDarkTheme()
    val viewModel: CandidatoDetailViewModel = androidx.lifecycle.viewmodel.compose.viewModel()
    val state by viewModel.state

    LaunchedEffect(candidateId) {
        viewModel.getCandidatoDetail(candidateId)
    }

    Scaffold(
        topBar = {
            TopAppBar(
                title = {
                    Text(
                        text = "Detalle del Candidato",
                        style = MaterialTheme.typography.titleLarge.copy(
                            fontWeight = FontWeight.Bold
                        )
                    )
                },
                navigationIcon = {
                    IconButton(onClick = onBack) {
                        Icon(
                            imageVector = Icons.Default.ArrowBack,
                            contentDescription = "Volver"
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
            state.isLoading -> {
                Box(
                    modifier = Modifier
                        .fillMaxSize()
                        .padding(innerPadding),
                    contentAlignment = Alignment.Center
                ) {
                    CircularProgressIndicator()
                }
            }
            state.errorMessage != null -> {
                Box(
                    modifier = Modifier
                        .fillMaxSize()
                        .padding(innerPadding)
                        .padding(16.dp),
                    contentAlignment = Alignment.Center
                ) {
                    Column(
                        horizontalAlignment = Alignment.CenterHorizontally,
                        verticalArrangement = Arrangement.spacedBy(16.dp)
                    ) {
                        Icon(
                            imageVector = Icons.Default.ErrorOutline,
                            contentDescription = "Error",
                            tint = ErrorRed,
                            modifier = Modifier.size(64.dp)
                        )
                        Text(
                            text = state.errorMessage!!,
                            style = MaterialTheme.typography.bodyLarge,
                            color = ErrorRed
                        )
                    }
                }
            }
            state.candidato != null -> {
                CandidatoDetailContent(
                    candidato = state.candidato!!,
                    isDarkMode = isDarkMode,
                    modifier = Modifier.padding(innerPadding)
                )
            }
        }
    }
}

@Composable
fun CandidatoDetailContent(
    candidato: com.rivera.votainformado.data.model.candidatos.CandidatoDetail,
    isDarkMode: Boolean,
    modifier: Modifier = Modifier
) {
    LazyColumn(
        modifier = modifier.fillMaxSize().background(MaterialTheme.colorScheme.background)
    ) {
        // Header con foto y datos básicos
        item {
            Card(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(16.dp),
                shape = RoundedCornerShape(20.dp),
                colors = CardDefaults.cardColors(
                    containerColor = if (isDarkMode) DarkSurf else NeutralWhite
                ),
                elevation = CardDefaults.cardElevation(defaultElevation = 4.dp)
            ) {
                Column(
                    modifier = Modifier.padding(24.dp),
                    horizontalAlignment = Alignment.CenterHorizontally
                ) {
                    // Foto del candidato
                    Image(
                        painter = rememberAsyncImagePainter(
                            candidato.fotoUrl ?: candidato.partido.logoUrl.orEmpty()
                        ),
                        contentDescription = null,
                        modifier = Modifier
                            .size(120.dp)
                            .clip(CircleShape)
                            .background(if (isDarkMode) DarkSurfVar else NeutralLight),
                        contentScale = ContentScale.Crop
                    )
                    
                    Spacer(modifier = Modifier.height(16.dp))
                    
                    Text(
                        text = candidato.nombreCompleto,
                        style = MaterialTheme.typography.headlineMedium.copy(
                            fontWeight = FontWeight.Bold,
                            color = if (isDarkMode) InstitutionalBlueLight else InstitutionalBlue
                        )
                    )
                    
                    Spacer(modifier = Modifier.height(8.dp))
                    
                    Text(
                        text = candidato.cargo.nombreCargo,
                        style = MaterialTheme.typography.titleMedium,
                        color = if (isDarkMode) NeutralGray else NeutralMedium
                    )
                    
                    Spacer(modifier = Modifier.height(4.dp))
                    
                    Text(
                        text = regionToText(candidato.region),
                        style = MaterialTheme.typography.bodyLarge,
                        color = if (isDarkMode) NeutralGray else NeutralMedium
                    )
                    
                    Spacer(modifier = Modifier.height(16.dp))
                    
                    // Partido
                    Row(
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.Center
                    ) {
                        Box(
                            modifier = Modifier
                                .size(12.dp)
                                .clip(CircleShape)
                                .background(if (isDarkMode) CivicGreenLight else CivicGreen)
                        )
                        Spacer(modifier = Modifier.width(8.dp))
                        Text(
                            text = candidato.partido.nombrePartido,
                            style = MaterialTheme.typography.titleMedium.copy(
                                fontWeight = FontWeight.SemiBold,
                                color = if (isDarkMode) CivicGreenLight else CivicGreen
                            )
                        )
                    }
                    
                    // Votos
                    if (candidato.totalVotos != null && candidato.totalVotos > 0) {
                        Spacer(modifier = Modifier.height(16.dp))
                        Divider()
                        Spacer(modifier = Modifier.height(12.dp))
                        
                        Row(
                            verticalAlignment = Alignment.CenterVertically,
                            horizontalArrangement = Arrangement.Center
                        ) {
                            Icon(
                                imageVector = Icons.Default.ThumbUp,
                                contentDescription = null,
                                tint = if (isDarkMode) InstitutionalBlueLight else InstitutionalBlue
                            )
                            Spacer(modifier = Modifier.width(8.dp))
                            Text(
                                text = "${candidato.totalVotos} votos",
                                style = MaterialTheme.typography.titleLarge.copy(
                                    fontWeight = FontWeight.Bold,
                                    color = if (isDarkMode) InstitutionalBlueLight else InstitutionalBlue
                                )
                            )
                        }
                    }
                }
            }
        }
        
        // Denuncias
        if (!candidato.denuncias.isNullOrEmpty()) {
            item {
                SectionHeader(
                    title = "Denuncias",
                    icon = Icons.Default.Warning,
                    isDarkMode = isDarkMode,
                    color = ErrorRed
                )
            }
            items(candidato.denuncias) { denuncia ->
                AntecedenteCard(
                    antecedente = denuncia,
                    isDarkMode = isDarkMode,
                    iconColor = ErrorRed
                )
            }
        }
        
        // Propuestas
        if (!candidato.propuestas.isNullOrEmpty()) {
            item {
                SectionHeader(
                    title = "Propuestas",
                    icon = Icons.Default.Lightbulb,
                    isDarkMode = isDarkMode,
                    color = if (isDarkMode) CivicGreenLight else CivicGreen
                )
            }
            items(candidato.propuestas) { propuesta ->
                AntecedenteCard(
                    antecedente = propuesta,
                    isDarkMode = isDarkMode,
                    iconColor = if (isDarkMode) CivicGreenLight else CivicGreen
                )
            }
        }
        
        // Proyectos
        if (!candidato.proyectos.isNullOrEmpty()) {
            item {
                SectionHeader(
                    title = "Proyectos de Ley",
                    icon = Icons.Default.Description,
                    isDarkMode = isDarkMode,
                    color = if (isDarkMode) InstitutionalBlueLight else InstitutionalBlue
                )
            }
            items(candidato.proyectos) { proyecto ->
                AntecedenteCard(
                    antecedente = proyecto,
                    isDarkMode = isDarkMode,
                    iconColor = if (isDarkMode) InstitutionalBlueLight else InstitutionalBlue
                )
            }
        }
        
        // Espaciado final
        item {
            Spacer(modifier = Modifier.height(16.dp))
        }
    }
}

@Composable
fun SectionHeader(
    title: String,
    icon: androidx.compose.ui.graphics.vector.ImageVector,
    isDarkMode: Boolean,
    color: Color
) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 16.dp, vertical = 12.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        Icon(
            imageVector = icon,
            contentDescription = null,
            tint = color,
            modifier = Modifier.size(24.dp)
        )
        Spacer(modifier = Modifier.width(12.dp))
        Text(
            text = title,
            style = MaterialTheme.typography.titleLarge.copy(
                fontWeight = FontWeight.Bold,
                color = color
            )
        )
    }
}

@Composable
fun AntecedenteCard(
    antecedente: com.rivera.votainformado.data.model.candidatos.Antecedente,
    isDarkMode: Boolean,
    iconColor: Color
) {
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 16.dp, vertical = 8.dp),
        shape = RoundedCornerShape(12.dp),
        colors = CardDefaults.cardColors(
            containerColor = if (isDarkMode) DarkSurfVar else NeutralLight
        ),
        elevation = CardDefaults.cardElevation(defaultElevation = 2.dp)
    ) {
        Column(
            modifier = Modifier.padding(16.dp)
        ) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.Top
            ) {
                Column(modifier = Modifier.weight(1f)) {
                    Text(
                        text = antecedente.titulo,
                        style = MaterialTheme.typography.titleMedium.copy(
                            fontWeight = FontWeight.Bold
                        ),
                        maxLines = 2,
                        overflow = TextOverflow.Ellipsis
                    )
                    
                    if (!antecedente.descripcion.isNullOrEmpty()) {
                        Spacer(modifier = Modifier.height(8.dp))
                        Text(
                            text = antecedente.descripcion,
                            style = MaterialTheme.typography.bodyMedium,
                            color = if (isDarkMode) NeutralGray else NeutralMedium,
                            maxLines = 3,
                            overflow = TextOverflow.Ellipsis
                        )
                    }
                }
                
                if (antecedente.fuenteUrl != null && antecedente.fuenteUrl.isNotEmpty()) {
                    IconButton(
                        onClick = { /* Open link */ },
                        modifier = Modifier.size(40.dp)
                    ) {
                        Icon(
                            imageVector = Icons.Default.Link,
                            contentDescription = "Ver fuente",
                            tint = iconColor
                        )
                    }
                }
            }
            
            if (antecedente.fecha != null) {
                Spacer(modifier = Modifier.height(8.dp))
                Row(
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Icon(
                        imageVector = Icons.Default.CalendarToday,
                        contentDescription = null,
                        tint = iconColor,
                        modifier = Modifier.size(16.dp)
                    )
                    Spacer(modifier = Modifier.width(8.dp))
                    Text(
                        text = antecedente.fecha,
                        style = MaterialTheme.typography.bodySmall,
                        color = if (isDarkMode) NeutralGray else NeutralMedium
                    )
                }
            }
        }
    }
}

fun regionToText(region: JsonElement?): String {
    if (region == null || region.isJsonNull) return "-"
    return when (region) {
        is JsonPrimitive -> if (region.isString) region.asString else "-"
        is JsonObject -> {
            val candidates = listOf("nombre", "nombre_region", "name", "region")
            for (key in candidates) {
                if (region.has(key) && region.get(key).isJsonPrimitive) {
                    val prim = region.get(key).asJsonPrimitive
                    if (prim.isString) return prim.asString
                }
            }
            if (region.has("id") && region.get("id").isJsonPrimitive) {
                return "Región #" + region.get("id").asInt
            }
            "-"
        }
        else -> "-"
    }
}

