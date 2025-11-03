package com.rivera.votainformado.ui.resultados

import androidx.compose.animation.*
import androidx.compose.animation.core.*
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.lazy.itemsIndexed
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
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.geometry.Size
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.StrokeCap
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.viewmodel.compose.viewModel
import coil.compose.rememberAsyncImagePainter
import com.rivera.votainformado.R
import com.rivera.votainformado.ui.theme.*
import com.rivera.votainformado.ui.navigation.BottomNavigationBar
import kotlin.math.cos
import kotlin.math.sin

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ResultadosScreen(
    onNavigate: (String) -> Unit = {}
) {
    val isDarkMode = isSystemInDarkTheme()
    val viewModel: ResultadosViewModel = viewModel()
    val state by viewModel.resultadosState.collectAsState()

    Scaffold(
        topBar = {
            TopAppBar(
                title = {
                    Row(
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.spacedBy(8.dp)
                    ) {
                        Icon(
                            imageVector = Icons.Default.BarChart,
                            contentDescription = null,
                            tint = if (isDarkMode) InstitutionalBlueLight else InstitutionalBlue,
                            modifier = Modifier.size(24.dp)
                        )
                        Text(
                            text = "Resultados Electorales",
                            style = MaterialTheme.typography.titleLarge.copy(
                                fontWeight = FontWeight.Bold,
                                color = if (isDarkMode) InstitutionalBlueLight else InstitutionalBlue
                            )
                        )
                    }
                },
                actions = {
                    Surface(
                        shape = RoundedCornerShape(12.dp),
                        color = if (state.mostrarPorPartido) {
                            if (isDarkMode) InstitutionalBlueDark else InstitutionalBlue.copy(alpha = 0.1f)
                        } else {
                            Color.Transparent
                        },
                        modifier = Modifier.padding(end = 8.dp)
                    ) {
                        IconButton(
                            onClick = { viewModel.toggleVista() }
                        ) {
                            Icon(
                                imageVector = if (state.mostrarPorPartido) Icons.Default.Group else Icons.Default.Person,
                                contentDescription = "Cambiar vista",
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
                currentRoute = "resultados",
                onNavigate = onNavigate
            )
        }
    ) { innerPadding ->
        when {
            state.isLoading && state.resultados.isEmpty() && state.resultadosPorPartido.isEmpty() -> {
                LoadingView(isDarkMode, innerPadding)
            }
            state.errorMessage != null && state.resultados.isEmpty() && state.resultadosPorPartido.isEmpty() -> {
                ErrorView(
                    errorMessage = state.errorMessage,
                    isDarkMode = isDarkMode,
                    innerPadding = innerPadding,
                    onRetry = { viewModel.loadResultados() }
                )
            }
            else -> {
                ResultsContent(
                    state = state,
                    viewModel = viewModel,
                    isDarkMode = isDarkMode,
                    innerPadding = innerPadding
                )
            }
        }
    }
}

@Composable
fun LoadingView(isDarkMode: Boolean, innerPadding: PaddingValues) {
    Box(
        modifier = Modifier
            .fillMaxSize()
            .padding(innerPadding)
            .background(MaterialTheme.colorScheme.background),
        contentAlignment = Alignment.Center
    ) {
        Column(
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.spacedBy(20.dp)
        ) {
            CircularProgressIndicator(
                color = if (isDarkMode) InstitutionalBlueLight else InstitutionalBlue,
                modifier = Modifier.size(56.dp),
                strokeWidth = 5.dp
            )
            Text(
                text = "Cargando resultados electorales...",
                style = MaterialTheme.typography.titleMedium.copy(
                    fontWeight = FontWeight.Medium
                ),
                color = if (isDarkMode) NeutralGray else NeutralMedium
            )
        }
    }
}

@Composable
fun ErrorView(
    errorMessage: String?,
    isDarkMode: Boolean,
    innerPadding: PaddingValues,
    onRetry: () -> Unit
) {
    Box(
        modifier = Modifier
            .fillMaxSize()
            .padding(innerPadding)
            .background(MaterialTheme.colorScheme.background),
        contentAlignment = Alignment.Center
    ) {
        Card(
            modifier = Modifier
                .fillMaxWidth()
                .padding(24.dp),
            shape = RoundedCornerShape(20.dp),
            colors = CardDefaults.cardColors(
                containerColor = if (isDarkMode) DarkSurfVar else NeutralWhite
            ),
            elevation = CardDefaults.cardElevation(defaultElevation = 4.dp)
        ) {
            Column(
                horizontalAlignment = Alignment.CenterHorizontally,
                verticalArrangement = Arrangement.spacedBy(20.dp),
                modifier = Modifier.padding(32.dp)
            ) {
                Icon(
                    imageVector = Icons.Default.CloudOff,
                    contentDescription = null,
                    tint = ErrorRed,
                    modifier = Modifier.size(64.dp)
                )
                Text(
                    text = "Error al cargar",
                    style = MaterialTheme.typography.titleLarge.copy(
                        fontWeight = FontWeight.Bold
                    ),
                    color = ErrorRed
                )
                Text(
                    text = errorMessage ?: "No se pudieron obtener los resultados",
                    style = MaterialTheme.typography.bodyMedium,
                    color = if (isDarkMode) NeutralGray else NeutralMedium,
                    textAlign = androidx.compose.ui.text.style.TextAlign.Center
                )
                Button(
                    onClick = onRetry,
                    colors = ButtonDefaults.buttonColors(
                        containerColor = if (isDarkMode) InstitutionalBlueLight else InstitutionalBlue
                    ),
                    shape = RoundedCornerShape(12.dp),
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Icon(Icons.Default.Refresh, contentDescription = null)
                    Spacer(modifier = Modifier.width(8.dp))
                    Text("Reintentar", fontWeight = FontWeight.Bold)
                }
            }
        }
    }
}

@Composable
fun ResultsContent(
    state: ResultadosState,
    viewModel: ResultadosViewModel,
    isDarkMode: Boolean,
    innerPadding: PaddingValues
) {
    LazyColumn(
        modifier = Modifier
            .fillMaxSize()
            .padding(innerPadding)
            .background(MaterialTheme.colorScheme.background),
        contentPadding = PaddingValues(horizontal = 16.dp, vertical = 20.dp),
        verticalArrangement = Arrangement.spacedBy(20.dp)
    ) {
        // Estadísticas generales con animación
        state.estadisticas?.let { estadisticas ->
            item {
                AnimatedVisibility(
                    visible = true,
                    enter = fadeIn() + slideInVertically()
                ) {
                    EstadisticasGeneralesCard(
                        estadisticas = estadisticas,
                        isDarkMode = isDarkMode
                    )
                }
            }
        }

        // Filtros mejorados
        item {
            FiltrosCargoMejorado(
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

        // Gráfico circular de distribución
        if (!state.mostrarPorPartido && state.resultados.isNotEmpty()) {
            item {
                GraficoDistribucionVotos(
                    resultados = state.resultados.take(5),
                    isDarkMode = isDarkMode
                )
            }
        } else if (state.mostrarPorPartido && state.resultadosPorPartido.isNotEmpty()) {
            item {
                GraficoDistribucionPartidos(
                    resultados = state.resultadosPorPartido.take(5),
                    isDarkMode = isDarkMode
                )
            }
        }

        // Header con título y contador
        item {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Column {
                    Text(
                        text = if (state.mostrarPorPartido) "Por Partido Político" else "Por Candidato",
                        style = MaterialTheme.typography.titleLarge.copy(
                            fontWeight = FontWeight.Bold,
                            color = if (isDarkMode) InstitutionalBlueLight else InstitutionalBlue
                        )
                    )
                    Text(
                        text = "${if (state.mostrarPorPartido) state.resultadosPorPartido.size else state.resultados.size} resultados",
                        style = MaterialTheme.typography.bodyMedium,
                        color = if (isDarkMode) NeutralGray else NeutralMedium
                    )
                }
            }
        }

        // Lista de resultados con posiciones
        if (!state.mostrarPorPartido) {
            if (state.resultados.isEmpty()) {
                item {
                    EmptyResultsCard(isDarkMode = isDarkMode)
                }
            } else {
                itemsIndexed(state.resultados) { index, resultado ->
                    ResultadoCandidatoCardMejorado(
                        resultado = resultado,
                        posicion = index + 1,
                        maxVotos = state.resultados.maxOfOrNull { it.totalVotos } ?: 1,
                        totalVotos = state.resultados.sumOf { it.totalVotos },
                        isDarkMode = isDarkMode
                    )
                }
            }
        } else {
            if (state.resultadosPorPartido.isEmpty()) {
                item {
                    EmptyResultsCard(isDarkMode = isDarkMode)
                }
            } else {
                itemsIndexed(state.resultadosPorPartido) { index, resultado ->
                    ResultadoPartidoCardMejorado(
                        resultado = resultado,
                        posicion = index + 1,
                        maxVotos = state.resultadosPorPartido.maxOfOrNull { it.totalVotos } ?: 1,
                        totalVotos = state.resultadosPorPartido.sumOf { it.totalVotos },
                        isDarkMode = isDarkMode
                    )
                }
            }
        }

        // Espaciado final
        item {
            Spacer(modifier = Modifier.height(20.dp))
        }
    }
}

@Composable
fun EstadisticasGeneralesCard(
    estadisticas: com.rivera.votainformado.data.model.votos.Estadisticas,
    isDarkMode: Boolean
) {
    Card(
        modifier = Modifier.fillMaxWidth(),
        shape = RoundedCornerShape(20.dp),
        colors = CardDefaults.cardColors(
            containerColor = if (isDarkMode) DarkSurfVar else NeutralWhite
        ),
        elevation = CardDefaults.cardElevation(defaultElevation = 6.dp)
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(20.dp),
            verticalArrangement = Arrangement.spacedBy(20.dp)
        ) {
            Row(
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.spacedBy(12.dp)
            ) {
                Surface(
                    shape = RoundedCornerShape(12.dp),
                    color = if (isDarkMode) InstitutionalBlueDark else InstitutionalBlue.copy(alpha = 0.1f)
                ) {
                    Icon(
                        imageVector = Icons.Default.Assessment,
                        contentDescription = null,
                        tint = if (isDarkMode) InstitutionalBlueLight else InstitutionalBlue,
                        modifier = Modifier
                            .padding(12.dp)
                            .size(24.dp)
                    )
                }
                Text(
                    text = "Estadísticas Generales",
                    style = MaterialTheme.typography.titleLarge.copy(
                        fontWeight = FontWeight.Bold,
                        color = if (isDarkMode) InstitutionalBlueLight else InstitutionalBlue
                    )
                )
            }

            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceEvenly
            ) {
                StatBoxMejorado(
                    icon = Icons.Default.HowToVote,
                    label = "Total Votos",
                    value = formatNumber(estadisticas.totalVotos),
                    color = if (isDarkMode) CivicGreenLight else CivicGreen,
                    isDarkMode = isDarkMode
                )
                StatBoxMejorado(
                    icon = Icons.Default.People,
                    label = "Votantes",
                    value = formatNumber(estadisticas.totalVotantes),
                    color = if (isDarkMode) InstitutionalBlueLight else InstitutionalBlue,
                    isDarkMode = isDarkMode
                )
                StatBoxMejorado(
                    icon = Icons.Default.Person,
                    label = "Candidatos",
                    value = estadisticas.totalCandidatos.toString(),
                    color = if (isDarkMode) WarningAmber else AccentAmber,
                    isDarkMode = isDarkMode
                )
            }
        }
    }
}

@Composable
fun StatBoxMejorado(
    icon: androidx.compose.ui.graphics.vector.ImageVector,
    label: String,
    value: String,
    color: Color,
    isDarkMode: Boolean
) {
    Column(
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.spacedBy(8.dp)
    ) {
        Surface(
            shape = CircleShape,
            color = color.copy(alpha = 0.15f),
            modifier = Modifier.size(56.dp)
        ) {
            Box(contentAlignment = Alignment.Center) {
                Icon(
                    imageVector = icon,
                    contentDescription = null,
                    modifier = Modifier.size(28.dp),
                    tint = color
                )
            }
        }
        Text(
            text = value,
            style = MaterialTheme.typography.headlineMedium.copy(
                fontWeight = FontWeight.Bold,
                fontSize = 24.sp
            ),
            color = color
        )
        Text(
            text = label,
            style = MaterialTheme.typography.bodySmall,
            color = if (isDarkMode) NeutralGray else NeutralMedium
        )
    }
}

@Composable
fun FiltrosCargoMejorado(
    cargoSeleccionado: String?,
    onCargoSeleccionado: (String?) -> Unit,
    isDarkMode: Boolean
) {
    val cargos = listOf(
        Triple("Todos", null, Icons.Default.GridView),
        Triple("Presidente", "Presidente", Icons.Default.AccountBalance),
        Triple("Senador", "Senador", Icons.Default.Gavel),
        Triple("Diputado", "Diputado", Icons.Default.Groups)
    )

    Column(verticalArrangement = Arrangement.spacedBy(12.dp)) {
        Text(
            text = "Filtrar por cargo",
            style = MaterialTheme.typography.titleMedium.copy(
                fontWeight = FontWeight.SemiBold
            ),
            color = if (isDarkMode) NeutralGray else NeutralMedium
        )

        LazyRow(
            horizontalArrangement = Arrangement.spacedBy(12.dp),
            modifier = Modifier.fillMaxWidth()
        ) {
            items(cargos) { (label, cargo, icon) ->
                FilterChipMejorado(
                    label = label,
                    icon = icon,
                    selected = cargoSeleccionado == cargo,
                    onClick = { onCargoSeleccionado(cargo) },
                    isDarkMode = isDarkMode
                )
            }
        }
    }
}

@Composable
fun FilterChipMejorado(
    label: String,
    icon: androidx.compose.ui.graphics.vector.ImageVector,
    selected: Boolean,
    onClick: () -> Unit,
    isDarkMode: Boolean
) {
    Surface(
        onClick = onClick,
        shape = RoundedCornerShape(16.dp),
        color = if (selected) {
            if (isDarkMode) InstitutionalBlueDark else InstitutionalBlue
        } else {
            if (isDarkMode) DarkSurf else NeutralLight
        },
        modifier = Modifier.height(48.dp)
    ) {
        Row(
            modifier = Modifier.padding(horizontal = 16.dp, vertical = 12.dp),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            Icon(
                imageVector = icon,
                contentDescription = null,
                tint = if (selected) NeutralWhite else {
                    if (isDarkMode) NeutralGray else NeutralMedium
                },
                modifier = Modifier.size(20.dp)
            )
            Text(
                text = label,
                style = MaterialTheme.typography.bodyMedium.copy(
                    fontWeight = if (selected) FontWeight.Bold else FontWeight.Medium
                ),
                color = if (selected) NeutralWhite else {
                    if (isDarkMode) NeutralGray else NeutralDark
                }
            )
        }
    }
}

@Composable
fun GraficoDistribucionVotos(
    resultados: List<com.rivera.votainformado.data.model.votos.ResultadoGeneral>,
    isDarkMode: Boolean
) {
    val totalVotos = resultados.sumOf { it.totalVotos }
    val colores = listOf(
        if (isDarkMode) InstitutionalBlueLight else InstitutionalBlue,
        if (isDarkMode) CivicGreenLight else CivicGreen,
        if (isDarkMode) WarningAmber else AccentAmber,
        ErrorRed,
        if (isDarkMode) NeutralGray else NeutralMedium
    )

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
                .padding(20.dp),
            verticalArrangement = Arrangement.spacedBy(20.dp)
        ) {
            Text(
                text = "Top 5 - Distribución de Votos",
                style = MaterialTheme.typography.titleMedium.copy(
                    fontWeight = FontWeight.Bold,
                    color = if (isDarkMode) InstitutionalBlueLight else InstitutionalBlue
                )
            )

            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceAround,
                verticalAlignment = Alignment.CenterVertically
            ) {
                // Gráfico circular
                Box(
                    modifier = Modifier.size(160.dp),
                    contentAlignment = Alignment.Center
                ) {
                    PieChart(
                        data = resultados.map { it.totalVotos.toFloat() },
                        colors = colores,
                        modifier = Modifier.fillMaxSize()
                    )
                }

                // Leyenda
                Column(
                    verticalArrangement = Arrangement.spacedBy(12.dp)
                ) {
                    resultados.take(5).forEachIndexed { index, resultado ->
                        LeyendaItem(
                            color = colores[index],
                            label = resultado.nombreCompleto.split(" ").take(2).joinToString(" "),
                            porcentaje = if (totalVotos > 0) (resultado.totalVotos.toFloat() / totalVotos * 100) else 0f,
                            isDarkMode = isDarkMode
                        )
                    }
                }
            }
        }
    }
}

@Composable
fun GraficoDistribucionPartidos(
    resultados: List<com.rivera.votainformado.data.model.votos.ResultadoPorPartido>,
    isDarkMode: Boolean
) {
    val totalVotos = resultados.sumOf { it.totalVotos }
    val colores = listOf(
        if (isDarkMode) InstitutionalBlueLight else InstitutionalBlue,
        if (isDarkMode) CivicGreenLight else CivicGreen,
        if (isDarkMode) WarningAmber else AccentAmber,
        ErrorRed,
        if (isDarkMode) NeutralGray else NeutralMedium
    )

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
                .padding(20.dp),
            verticalArrangement = Arrangement.spacedBy(20.dp)
        ) {
            Text(
                text = "Top 5 - Distribución por Partido",
                style = MaterialTheme.typography.titleMedium.copy(
                    fontWeight = FontWeight.Bold,
                    color = if (isDarkMode) InstitutionalBlueLight else InstitutionalBlue
                )
            )

            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceAround,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Box(
                    modifier = Modifier.size(160.dp),
                    contentAlignment = Alignment.Center
                ) {
                    PieChart(
                        data = resultados.map { it.totalVotos.toFloat() },
                        colors = colores,
                        modifier = Modifier.fillMaxSize()
                    )
                }

                Column(
                    verticalArrangement = Arrangement.spacedBy(12.dp)
                ) {
                    resultados.take(5).forEachIndexed { index, resultado ->
                        LeyendaItem(
                            color = colores[index],
                            label = resultado.sigla,
                            porcentaje = if (totalVotos > 0) (resultado.totalVotos.toFloat() / totalVotos * 100) else 0f,
                            isDarkMode = isDarkMode
                        )
                    }
                }
            }
        }
    }
}

@Composable
fun PieChart(
    data: List<Float>,
    colors: List<Color>,
    modifier: Modifier = Modifier
) {
    val total = data.sum()
    val proportions = data.map { it / total }
    val sweepAngles = proportions.map { it * 360f }

    var animatedProgress by remember { mutableStateOf(0f) }

    LaunchedEffect(Unit) {
        animatedProgress = 1f
    }

    val animatedAngles = sweepAngles.map { angle ->
        animateFloatAsState(
            targetValue = angle * animatedProgress,
            animationSpec = tween(durationMillis = 1000, easing = FastOutSlowInEasing)
        ).value
    }

    Canvas(modifier = modifier) {
        val size = size.minDimension
        val radius = size / 2f
        val strokeWidth = radius * 0.4f

        var startAngle = -90f

        animatedAngles.forEachIndexed { index, sweepAngle ->
            drawArc(
                color = colors[index % colors.size],
                startAngle = startAngle,
                sweepAngle = sweepAngle,
                useCenter = false,
                style = Stroke(width = strokeWidth, cap = StrokeCap.Butt),
                size = Size(size, size),
                topLeft = Offset(
                    (this.size.width - size) / 2,
                    (this.size.height - size) / 2
                )
            )
            startAngle += sweepAngle
        }
    }
}

@Composable
fun LeyendaItem(
    color: Color,
    label: String,
    porcentaje: Float,
    isDarkMode: Boolean
) {
    Row(
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.spacedBy(8.dp)
    ) {
        Box(
            modifier = Modifier
                .size(12.dp)
                .clip(CircleShape)
                .background(color)
        )
        Column {
            Text(
                text = label,
                style = MaterialTheme.typography.bodySmall.copy(
                    fontWeight = FontWeight.Medium
                ),
                maxLines = 1,
                overflow = TextOverflow.Ellipsis,
                color = MaterialTheme.colorScheme.onSurface
            )
            Text(
                text = "${String.format("%.1f", porcentaje)}%",
                style = MaterialTheme.typography.bodySmall,
                color = if (isDarkMode) NeutralGray else NeutralMedium
            )
        }
    }
}

@Composable
fun ResultadoCandidatoCardMejorado(
    resultado: com.rivera.votainformado.data.model.votos.ResultadoGeneral,
    posicion: Int,
    maxVotos: Int,
    totalVotos: Int,
    isDarkMode: Boolean
) {
    val porcentajeRelativo = if (maxVotos > 0) (resultado.totalVotos.toFloat() / maxVotos.toFloat()) else 0f
    val porcentajeTotal = if (totalVotos > 0) (resultado.totalVotos.toFloat() / totalVotos * 100f) else 0f

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
            // Posición
            MedalPosicion(posicion, isDarkMode)

            // Foto
            Box(
                modifier = Modifier
                    .size(64.dp)
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

            // Info
            Column(
                modifier = Modifier.weight(1f),
                verticalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                Text(
                    text = resultado.nombreCompleto,
                    style = MaterialTheme.typography.titleMedium.copy(
                        fontWeight = FontWeight.Bold
                    ),
                    maxLines = 2,
                    overflow = TextOverflow.Ellipsis
                )

                Row(
                    horizontalArrangement = Arrangement.spacedBy(8.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Surface(
                        shape = RoundedCornerShape(8.dp),
                        color = if (isDarkMode) InstitutionalBlueDark else InstitutionalBlue.copy(alpha = 0.1f)
                    ) {
                        Text(
                            text = resultado.partido.nombre,
                            style = MaterialTheme.typography.bodySmall.copy(
                                fontWeight = FontWeight.Medium
                            ),
                            color = if (isDarkMode) InstitutionalBlueLight else InstitutionalBlue,
                            modifier = Modifier.padding(horizontal = 8.dp, vertical = 4.dp)
                        )
                    }
                }

                // Barra de progreso mejorada
                Column(verticalArrangement = Arrangement.spacedBy(4.dp)) {
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Text(
                            text = "${formatNumber(resultado.totalVotos)} votos",
                            style = MaterialTheme.typography.bodyMedium.copy(
                                fontWeight = FontWeight.Bold
                            ),
                            color = if (isDarkMode) InstitutionalBlueLight else InstitutionalBlue
                        )
                        Text(
                            text = "${String.format("%.1f", porcentajeTotal)}%",
                            style = MaterialTheme.typography.bodyMedium.copy(
                                fontWeight = FontWeight.Bold
                            ),
                            color = if (isDarkMode) CivicGreenLight else CivicGreen
                        )
                    }

                    Box(
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(8.dp)
                            .clip(RoundedCornerShape(4.dp))
                            .background(if (isDarkMode) DarkSurf else NeutralLight)
                    ) {
                        Box(
                            modifier = Modifier
                                .fillMaxHeight()
                                .fillMaxWidth(porcentajeRelativo)
                                .clip(RoundedCornerShape(4.dp))
                                .background(
                                    Brush.horizontalGradient(
                                        colors = listOf(
                                            if (isDarkMode) InstitutionalBlueLight else InstitutionalBlue,
                                            if (isDarkMode) CivicGreenLight else CivicGreen
                                        )
                                    )
                                )
                        )
                    }
                }
            }
        }
    }
}

@Composable
fun ResultadoPartidoCardMejorado(
    resultado: com.rivera.votainformado.data.model.votos.ResultadoPorPartido,
    posicion: Int,
    maxVotos: Int,
    totalVotos: Int,
    isDarkMode: Boolean
) {
    val porcentajeRelativo = if (maxVotos > 0) (resultado.totalVotos.toFloat() / maxVotos.toFloat()) else 0f
    val porcentajeTotal = if (totalVotos > 0) (resultado.totalVotos.toFloat() / totalVotos * 100f) else 0f

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
            // Posición
            MedalPosicion(posicion, isDarkMode)

            // Logo
            Box(
                modifier = Modifier
                    .size(64.dp)
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

            // Info
            Column(
                modifier = Modifier.weight(1f),
                verticalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                Text(
                    text = resultado.nombrePartido,
                    style = MaterialTheme.typography.titleMedium.copy(
                        fontWeight = FontWeight.Bold
                    ),
                    maxLines = 2,
                    overflow = TextOverflow.Ellipsis
                )

                Surface(
                    shape = RoundedCornerShape(8.dp),
                    color = if (isDarkMode) InstitutionalBlueDark else InstitutionalBlue.copy(alpha = 0.1f)
                ) {
                    Text(
                        text = resultado.sigla,
                        style = MaterialTheme.typography.bodySmall.copy(
                            fontWeight = FontWeight.Bold
                        ),
                        color = if (isDarkMode) InstitutionalBlueLight else InstitutionalBlue,
                        modifier = Modifier.padding(horizontal = 8.dp, vertical = 4.dp)
                    )
                }

                // Barra de progreso mejorada
                Column(verticalArrangement = Arrangement.spacedBy(4.dp)) {
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Text(
                            text = "${formatNumber(resultado.totalVotos)} votos",
                            style = MaterialTheme.typography.bodyMedium.copy(
                                fontWeight = FontWeight.Bold
                            ),
                            color = if (isDarkMode) InstitutionalBlueLight else InstitutionalBlue
                        )
                        Text(
                            text = "${String.format("%.1f", porcentajeTotal)}%",
                            style = MaterialTheme.typography.bodyMedium.copy(
                                fontWeight = FontWeight.Bold
                            ),
                            color = if (isDarkMode) CivicGreenLight else CivicGreen
                        )
                    }

                    Box(
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(8.dp)
                            .clip(RoundedCornerShape(4.dp))
                            .background(if (isDarkMode) DarkSurf else NeutralLight)
                    ) {
                        Box(
                            modifier = Modifier
                                .fillMaxHeight()
                                .fillMaxWidth(porcentajeRelativo)
                                .clip(RoundedCornerShape(4.dp))
                                .background(
                                    Brush.horizontalGradient(
                                        colors = listOf(
                                            if (isDarkMode) InstitutionalBlueLight else InstitutionalBlue,
                                            if (isDarkMode) CivicGreenLight else CivicGreen
                                        )
                                    )
                                )
                        )
                    }
                }
            }
        }
    }
}

@Composable
fun MedalPosicion(posicion: Int, isDarkMode: Boolean) {
    val (backgroundColor, textColor, icon) = when (posicion) {
        1 -> Triple(
            Color(0xFFFFD700),
            Color(0xFF8B6914),
            Icons.Default.EmojiEvents
        )
        2 -> Triple(
            Color(0xFFC0C0C0),
            Color(0xFF5A5A5A),
            Icons.Default.EmojiEvents
        )
        3 -> Triple(
            Color(0xFFCD7F32),
            Color(0xFF5C3A1F),
            Icons.Default.EmojiEvents
        )
        else -> Triple(
            if (isDarkMode) DarkSurf else NeutralLight,
            if (isDarkMode) NeutralGray else NeutralMedium,
            null
        )
    }

    Surface(
        shape = RoundedCornerShape(12.dp),
        color = backgroundColor,
        modifier = Modifier.size(48.dp)
    ) {
        Box(
            contentAlignment = Alignment.Center,
            modifier = Modifier.fillMaxSize()
        ) {
            if (icon != null && posicion <= 3) {
                Icon(
                    imageVector = icon,
                    contentDescription = null,
                    tint = textColor,
                    modifier = Modifier.size(28.dp)
                )
            } else {
                Text(
                    text = "$posicion",
                    style = MaterialTheme.typography.titleMedium.copy(
                        fontWeight = FontWeight.Bold
                    ),
                    color = textColor
                )
            }
        }
    }
}

@Composable
fun EmptyResultsCard(isDarkMode: Boolean) {
    Card(
        modifier = Modifier.fillMaxWidth(),
        shape = RoundedCornerShape(20.dp),
        colors = CardDefaults.cardColors(
            containerColor = if (isDarkMode) DarkSurfVar else NeutralWhite
        ),
        elevation = CardDefaults.cardElevation(defaultElevation = 2.dp)
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(40.dp),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.spacedBy(16.dp)
        ) {
            Surface(
                shape = CircleShape,
                color = if (isDarkMode) DarkSurf else NeutralLight,
                modifier = Modifier.size(80.dp)
            ) {
                Box(contentAlignment = Alignment.Center) {
                    Icon(
                        imageVector = Icons.Default.Inbox,
                        contentDescription = null,
                        modifier = Modifier.size(40.dp),
                        tint = if (isDarkMode) NeutralGray else NeutralMedium
                    )
                }
            }

            Text(
                text = "No hay resultados disponibles",
                style = MaterialTheme.typography.titleLarge.copy(
                    fontWeight = FontWeight.Bold
                ),
                color = MaterialTheme.colorScheme.onSurface,
                textAlign = androidx.compose.ui.text.style.TextAlign.Center
            )

            Text(
                text = "Los resultados aparecerán una vez que se registren los votos en el sistema",
                style = MaterialTheme.typography.bodyMedium,
                color = if (isDarkMode) NeutralGray else NeutralMedium,
                textAlign = androidx.compose.ui.text.style.TextAlign.Center
            )
        }
    }
}

// Función auxiliar para formatear números
fun formatNumber(number: Int): String {
    return when {
        number >= 1_000_000 -> "${String.format("%.1f", number / 1_000_000.0)}M"
        number >= 1_000 -> "${String.format("%.1f", number / 1_000.0)}K"
        else -> number.toString()
    }
}