package com.rivera.votainformado.ui.home

import android.annotation.SuppressLint
import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.material.icons.filled.AccountCircle
import androidx.compose.material.icons.outlined.*
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.gestures.snapping.rememberSnapFlingBehavior
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.foundation.pager.HorizontalPager
import androidx.compose.foundation.pager.rememberPagerState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.filled.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.sp
import coil.compose.rememberAsyncImagePainter
import com.rivera.votainformado.ui.theme.*
import com.rivera.votainformado.data.model.candidatos.CandidatoItem
import com.rivera.votainformado.ui.navigation.BottomNavigationBar
import com.google.gson.JsonElement
import com.google.gson.JsonObject
import com.google.gson.JsonPrimitive
import androidx.annotation.DrawableRes
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.outlined.CompareArrows
import com.rivera.votainformado.R
import com.google.accompanist.swiperefresh.SwipeRefresh
import com.google.accompanist.swiperefresh.rememberSwipeRefreshState

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun HomeScreen(
    onProfileClick: () -> Unit = {},
    onNavigate: (String) -> Unit = {},
    onNavigateToDetail: (Int) -> Unit = {}
) {
    var searchQuery by remember { mutableStateOf("") }

    val viewModel: HomeViewModel = androidx.lifecycle.viewmodel.compose.viewModel()
    val state by viewModel.state

    val pagerState = rememberPagerState(
        initialPage = 0,
        pageCount = { carouselItems.size }
    )

    // Auto-scroll para el carrusel
    LaunchedEffect(key1 = pagerState) {
        while (true) {
            kotlinx.coroutines.delay(5000)
            val nextPage = (pagerState.currentPage + 1) % pagerState.pageCount
            pagerState.animateScrollToPage(nextPage)
        }
    }

    Scaffold(
        topBar = {
            TopAppBar(
                title = {
                    Row(
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.spacedBy(12.dp)
                    ) {
                        Box(
                            modifier = Modifier
                                .size(42.dp)
                                .clip(RoundedCornerShape(12.dp))
                                .background(
                                    brush = Brush.linearGradient(
                                        listOf(InstitutionalBlue, InstitutionalBlue.copy(alpha = 0.8f))
                                    )
                                ),
                            contentAlignment = Alignment.Center
                        ) {
                            Icon(
                                imageVector = Icons.Filled.HowToVote,
                                contentDescription = null,
                                tint = Color.White,
                                modifier = Modifier.size(24.dp)
                            )
                        }
                        Column(modifier = Modifier.padding(top = 2.dp)) {
                            Text(
                                text = "Vota Informado",
                                style = MaterialTheme.typography.titleLarge.copy(
                                    fontWeight = FontWeight.ExtraBold,
                                    color = InstitutionalBlue,
                                    letterSpacing = (-0.5).sp
                                )
                            )
                            Text(
                                text = "Tu guía electoral",
                                style = MaterialTheme.typography.bodySmall.copy(
                                    color = NeutralMedium,
                                    fontWeight = FontWeight.Medium
                                )
                            )
                        }
                    }
                },
                actions = {
                    Box(
                        modifier = Modifier
                            .size(44.dp)
                            .padding(end = 8.dp)
                            .clip(CircleShape)
                            .background(InstitutionalBlue.copy(alpha = 0.1f))
                            .clickable { onProfileClick() },
                        contentAlignment = Alignment.Center
                    ) {
                        Icon(
                            imageVector = Icons.Filled.AccountCircle,
                            contentDescription = "Perfil",
                            tint = InstitutionalBlue,
                            modifier = Modifier.size(28.dp)
                        )
                    }
                },
                colors = TopAppBarDefaults.topAppBarColors(
                    containerColor = Color.White
                )
            )
        },
        bottomBar = {
            BottomNavigationBar(
                currentRoute = "home",
                onNavigate = onNavigate
            )
        }
    ) { innerPadding ->
        val swipeRefreshState = rememberSwipeRefreshState(isRefreshing = state.isLoading)

        SwipeRefresh(
            state = swipeRefreshState,
            onRefresh = { viewModel.getCandidatos() }
        ) {
            LazyColumn(
                modifier = Modifier
                    .padding(innerPadding)
                    .fillMaxSize()
                    .background(NeutralLight)
            ) {
                // Carrusel mejorado
                item {
                    Column(modifier = Modifier.padding(top = 20.dp, bottom = 24.dp)) {
                        Box(
                            modifier = Modifier
                                .fillMaxWidth()
                                .height(220.dp)
                                .padding(horizontal = 20.dp)
                        ) {
                            HorizontalPager(
                                state = pagerState,
                                pageSpacing = 0.dp,
                                modifier = Modifier.fillMaxWidth()
                            ) { page ->
                                CarouselCard(
                                    item = carouselItems[page],
                                    modifier = Modifier.fillMaxWidth()
                                )
                            }
                        }

                        // Indicadores del carrusel
                        Row(
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(top = 16.dp),
                            horizontalArrangement = Arrangement.Center,
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            repeat(carouselItems.size) { index ->
                                Box(
                                    modifier = Modifier
                                        .padding(horizontal = 4.dp)
                                        .size(
                                            width = if (pagerState.currentPage == index) 24.dp else 8.dp,
                                            height = 8.dp
                                        )
                                        .clip(RoundedCornerShape(4.dp))
                                        .background(
                                            if (pagerState.currentPage == index)
                                                InstitutionalBlue
                                            else
                                                NeutralMedium.copy(alpha = 0.3f)
                                        )
                                )
                            }
                        }
                    }
                }

                // Barra de búsqueda mejorada
                item {
                    SearchBar(
                        query = searchQuery,
                        onQueryChange = { searchQuery = it },
                        modifier = Modifier.padding(horizontal = 20.dp, vertical = 12.dp)
                    )
                }

                // Estado de carga
                if (state.isLoading && state.candidatos.isEmpty()) {
                    item {
                        Box(
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(vertical = 48.dp),
                            contentAlignment = Alignment.Center
                        ) {
                            Column(
                                horizontalAlignment = Alignment.CenterHorizontally,
                                verticalArrangement = Arrangement.spacedBy(16.dp)
                            ) {
                                CircularProgressIndicator(
                                    color = InstitutionalBlue,
                                    strokeWidth = 3.dp,
                                    modifier = Modifier.size(48.dp)
                                )
                                Text(
                                    text = "Cargando candidatos...",
                                    style = MaterialTheme.typography.bodyMedium.copy(
                                        fontWeight = FontWeight.Medium,
                                        color = NeutralMedium
                                    )
                                )
                            }
                        }
                    }
                }

                // Mensaje de error mejorado
                if (state.errorMessage != null) {
                    item {
                        Card(
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(horizontal = 20.dp, vertical = 12.dp),
                            colors = CardDefaults.cardColors(
                                containerColor = ErrorRed.copy(alpha = 0.08f)
                            ),
                            shape = RoundedCornerShape(16.dp),
                            border = androidx.compose.foundation.BorderStroke(
                                1.dp,
                                ErrorRed.copy(alpha = 0.2f)
                            )
                        ) {
                            Row(
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .padding(20.dp),
                                verticalAlignment = Alignment.CenterVertically,
                                horizontalArrangement = Arrangement.spacedBy(16.dp)
                            ) {
                                Box(
                                    modifier = Modifier
                                        .size(40.dp)
                                        .clip(CircleShape)
                                        .background(ErrorRed.copy(alpha = 0.15f)),
                                    contentAlignment = Alignment.Center
                                ) {
                                    Icon(
                                        imageVector = Icons.Outlined.ErrorOutline,
                                        contentDescription = null,
                                        tint = ErrorRed,
                                        modifier = Modifier.size(24.dp)
                                    )
                                }
                                Column(modifier = Modifier.weight(1f)) {
                                    Text(
                                        text = "Error al cargar",
                                        style = MaterialTheme.typography.titleSmall.copy(
                                            fontWeight = FontWeight.Bold,
                                            color = ErrorRed
                                        )
                                    )
                                    Text(
                                        text = state.errorMessage!!,
                                        style = MaterialTheme.typography.bodySmall.copy(
                                            color = ErrorRed.copy(alpha = 0.8f)
                                        )
                                    )
                                }
                            }
                        }
                    }
                }

                // Resultados de búsqueda
                val filteredCandidates = if (searchQuery.isNotEmpty()) {
                    state.candidatos.filter {
                        it.nombreCompleto.contains(searchQuery, ignoreCase = true) ||
                                it.partido.nombrePartido.contains(searchQuery, ignoreCase = true) ||
                                it.cargo.nombreCargo.contains(searchQuery, ignoreCase = true)
                    }
                } else {
                    emptyList()
                }

                if (searchQuery.isNotEmpty()) {
                    item {
                        Surface(
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(horizontal = 20.dp, vertical = 12.dp),
                            color = Color.White,
                            shape = RoundedCornerShape(16.dp),
                            shadowElevation = 2.dp
                        ) {
                            Column(
                                modifier = Modifier.padding(16.dp)
                            ) {
                                Row(
                                    verticalAlignment = Alignment.CenterVertically,
                                    horizontalArrangement = Arrangement.spacedBy(12.dp)
                                ) {
                                    Icon(
                                        imageVector = Icons.Outlined.Search,
                                        contentDescription = null,
                                        tint = InstitutionalBlue,
                                        modifier = Modifier.size(24.dp)
                                    )
                                    Text(
                                        text = if (filteredCandidates.isEmpty()) {
                                            "No se encontraron resultados"
                                        } else {
                                            "${filteredCandidates.size} candidato${if (filteredCandidates.size > 1) "s" else ""} encontrado${if (filteredCandidates.size > 1) "s" else ""}"
                                        },
                                        style = MaterialTheme.typography.titleMedium.copy(
                                            fontWeight = FontWeight.Bold,
                                            color = InstitutionalBlue
                                        )
                                    )
                                }
                                if (filteredCandidates.isNotEmpty()) {
                                    Spacer(modifier = Modifier.height(6.dp))
                                    Text(
                                        text = "Selecciona un candidato para ver su perfil completo",
                                        style = MaterialTheme.typography.bodySmall.copy(
                                            color = NeutralMedium
                                        )
                                    )
                                }
                            }
                        }
                    }

                    items(filteredCandidates) { candidato ->
                        SearchResultCard(
                            candidato = candidato,
                            onClick = { onNavigateToDetail(candidato.id) },
                            modifier = Modifier.padding(horizontal = 20.dp, vertical = 6.dp)
                        )
                    }

                    item {
                        Spacer(modifier = Modifier.height(8.dp))
                    }
                }

                // Secciones de candidatos por cargo
                val cargoSections = listOf(
                    Triple(
                        "Candidatos Presidenciales",
                        listOf("Presidente"),
                        Icons.Outlined.Gavel
                    ),
                    Triple("Candidatos al Senado", listOf("Senador"), Icons.Outlined.AccountBalance),
                    Triple(
                        "Candidatos a Diputados",
                        listOf("Diputado", "Diputada"),
                        Icons.Outlined.Groups
                    )
                )

                cargoSections.forEach { (title, cargos, icon) ->
                    val candidates = state.candidatos.filter {
                        cargos.any { cargo ->
                            it.cargo.nombreCargo.equals(
                                cargo,
                                ignoreCase = true
                            )
                        }
                    }

                    if (candidates.isNotEmpty()) {
                        item {
                            CandidateSection(
                                title = title,
                                icon = icon,
                                candidates = candidates,
                                onCandidateClick = onNavigateToDetail
                            )
                        }
                    }
                }

                // Espaciado final
                item {
                    Spacer(modifier = Modifier.height(24.dp))
                }
            }
        }
    }
}

@Composable
fun CarouselCard(
    item: CarouselItem,
    modifier: Modifier = Modifier
) {
    Card(
        modifier = modifier,
        shape = RoundedCornerShape(24.dp),
        elevation = CardDefaults.cardElevation(defaultElevation = 8.dp)
    ) {
        Box(
            modifier = Modifier
                .fillMaxSize()
                .background(Color.White)
        ) {
            // Imagen de fondo
            Image(
                painter = painterResource(id = item.imageRes),
                contentDescription = null,
                modifier = Modifier.fillMaxSize(),
                contentScale = ContentScale.Crop
            )

            // Gradiente mejorado
            Box(
                modifier = Modifier
                    .fillMaxSize()
                    .background(
                        Brush.verticalGradient(
                            colors = listOf(
                                Color.Transparent,
                                Color.Black.copy(alpha = 0.3f),
                                Color.Black.copy(alpha = 0.8f)
                            ),
                            startY = 100f
                        )
                    )
            )

            // Contenido del carrusel
            Column(
                modifier = Modifier
                    .align(Alignment.BottomStart)
                    .fillMaxWidth()
                    .padding(24.dp)
            ) {
                Text(
                    text = item.title,
                    style = MaterialTheme.typography.headlineSmall.copy(
                        fontWeight = FontWeight.ExtraBold,
                        color = Color.White,
                        letterSpacing = (-0.5).sp
                    )
                )
                Spacer(modifier = Modifier.height(8.dp))
                Text(
                    text = item.description,
                    style = MaterialTheme.typography.bodyMedium.copy(
                        color = Color.White.copy(alpha = 0.95f),
                        lineHeight = 20.sp
                    ),
                    maxLines = 2,
                    overflow = TextOverflow.Ellipsis
                )
            }
        }
    }
}

@Composable
fun SearchBar(
    query: String,
    onQueryChange: (String) -> Unit,
    modifier: Modifier = Modifier
) {
    Surface(
        modifier = modifier,
        shape = RoundedCornerShape(16.dp),
        color = Color.White,
        shadowElevation = 4.dp
    ) {
        OutlinedTextField(
            value = query,
            onValueChange = onQueryChange,
            modifier = Modifier.fillMaxWidth(),
            placeholder = {
                Text(
                    "Buscar candidato, partido o cargo...",
                    style = MaterialTheme.typography.bodyMedium,
                    color = NeutralMedium
                )
            },
            leadingIcon = {
                Icon(
                    Icons.Outlined.Search,
                    contentDescription = "Buscar",
                    tint = InstitutionalBlue,
                    modifier = Modifier.size(24.dp)
                )
            },
            trailingIcon = {
                if (query.isNotEmpty()) {
                    IconButton(onClick = { onQueryChange("") }) {
                        Icon(
                            Icons.Outlined.Cancel,
                            contentDescription = "Limpiar",
                            tint = NeutralMedium,
                            modifier = Modifier.size(22.dp)
                        )
                    }
                }
            },
            colors = TextFieldDefaults.colors(
                focusedContainerColor = Color.White,
                unfocusedContainerColor = Color.White,
                focusedIndicatorColor = InstitutionalBlue,
                unfocusedIndicatorColor = Color.Transparent,
                cursorColor = InstitutionalBlue,
                focusedTextColor = Color.Black,
                unfocusedTextColor = Color.Black
            ),
            shape = RoundedCornerShape(16.dp),
            singleLine = true
        )
    }
}

@Composable
fun CandidateSection(
    title: String,
    icon: androidx.compose.ui.graphics.vector.ImageVector,
    candidates: List<CandidatoItem>,
    onCandidateClick: (Int) -> Unit = {}
) {
    Column(
        modifier = Modifier
            .fillMaxWidth()
            .padding(top = 32.dp, bottom = 16.dp)
    ) {
        // Encabezado de sección mejorado
        Surface(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 20.dp, vertical = 8.dp),
            color = Color.White,
            shape = RoundedCornerShape(16.dp),
            shadowElevation = 2.dp
        ) {
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(16.dp),
                horizontalArrangement = Arrangement.spacedBy(12.dp),
                verticalAlignment = Alignment.CenterVertically
            ) {
                Box(
                    modifier = Modifier
                        .size(48.dp)
                        .clip(RoundedCornerShape(12.dp))
                        .background(
                            Brush.linearGradient(
                                listOf(InstitutionalBlue, InstitutionalBlue.copy(alpha = 0.8f))
                            )
                        ),
                    contentAlignment = Alignment.Center
                ) {
                    Icon(
                        imageVector = icon,
                        contentDescription = null,
                        tint = Color.White,
                        modifier = Modifier.size(24.dp)
                    )
                }
                Column {
                    Text(
                        text = title,
                        style = MaterialTheme.typography.titleLarge.copy(
                            fontWeight = FontWeight.ExtraBold,
                            color = InstitutionalBlue,
                            letterSpacing = (-0.5).sp
                        )
                    )
                    Text(
                        text = "${candidates.size} candidato${if (candidates.size > 1) "s" else ""}",
                        style = MaterialTheme.typography.bodySmall.copy(
                            color = NeutralMedium,
                            fontWeight = FontWeight.Medium
                        )
                    )
                }
            }
        }

        Spacer(modifier = Modifier.height(12.dp))

        // Lista horizontal de candidatos con scroll por columnas completas
        val columns = remember(candidates) { candidates.chunked(3) }
        val listState = rememberLazyListState()

        LazyRow(
            state = listState,
            contentPadding = PaddingValues(horizontal = 20.dp),
            horizontalArrangement = Arrangement.spacedBy(16.dp),
            modifier = Modifier.fillMaxWidth(),
            flingBehavior = rememberSnapFlingBehavior(lazyListState = listState)
        ) {
            items(columns.size) { columnIndex ->
                Column(
                    verticalArrangement = Arrangement.spacedBy(16.dp),
                    modifier = Modifier.width(320.dp)
                ) {
                    columns[columnIndex].forEach { candidatoItem ->
                        CandidatoCard(
                            candidato = candidatoItem,
                            onClick = { onCandidateClick(candidatoItem.id) },
                            modifier = Modifier.fillMaxWidth()
                        )
                    }
                }
            }
        }
    }
}

@Composable
fun SearchResultCard(
    candidato: CandidatoItem,
    onClick: () -> Unit = {},
    @SuppressLint("ModifierParameter") modifier: Modifier = Modifier
) {
    Card(
        modifier = modifier
            .fillMaxWidth()
            .clickable { onClick() },
        shape = RoundedCornerShape(20.dp),
        colors = CardDefaults.cardColors(containerColor = Color.White),
        elevation = CardDefaults.cardElevation(defaultElevation = 4.dp)
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
                    .size(80.dp)
                    .clip(RoundedCornerShape(16.dp))
                    .background(NeutralLight)
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
                        fontWeight = FontWeight.Bold,
                        letterSpacing = (-0.3).sp
                    ),
                    maxLines = 2,
                    overflow = TextOverflow.Ellipsis,
                    color = Color.Black
                )

                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    Box(
                        modifier = Modifier
                            .size(10.dp)
                            .clip(CircleShape)
                            .background(InstitutionalBlue)
                    )
                    Text(
                        text = candidato.partido.nombrePartido,
                        style = MaterialTheme.typography.bodyMedium.copy(
                            fontWeight = FontWeight.SemiBold,
                            color = InstitutionalBlue
                        ),
                        maxLines = 1,
                        overflow = TextOverflow.Ellipsis
                    )
                }

                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.spacedBy(6.dp)
                ) {
                    Icon(
                        imageVector = Icons.Outlined.LocationOn,
                        contentDescription = null,
                        modifier = Modifier.size(16.dp),
                        tint = NeutralMedium
                    )
                    Text(
                        text = "${candidato.cargo.nombreCargo} • ${regionToText(candidato.region!!)}",
                        style = MaterialTheme.typography.bodySmall.copy(
                            color = NeutralMedium,
                            fontWeight = FontWeight.Medium
                        ),
                        maxLines = 1,
                        overflow = TextOverflow.Ellipsis
                    )
                }
            }

            Icon(
                imageVector = Icons.Outlined.ChevronRight,
                contentDescription = null,
                tint = NeutralMedium,
                modifier = Modifier.size(24.dp)
            )
        }
    }
}

@Composable
fun CandidatoCard(
    candidato: CandidatoItem,
    onClick: () -> Unit = {},
    @SuppressLint("ModifierParameter") modifier: Modifier = Modifier
) {
    Card(
        modifier = modifier.height(135.dp),
        shape = RoundedCornerShape(20.dp),
        colors = CardDefaults.cardColors(containerColor = Color.White),
        elevation = CardDefaults.cardElevation(defaultElevation = 4.dp),
        onClick = onClick
    ) {
        Row(
            modifier = Modifier
                .fillMaxSize()
                .padding(16.dp),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.spacedBy(16.dp)
        ) {
            // Foto del candidato con diseño mejorado
            Box(
                modifier = Modifier
                    .size(85.dp)
                    .clip(RoundedCornerShape(18.dp))
                    .background(NeutralLight)
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
                        fontWeight = FontWeight.Bold,
                        letterSpacing = (-0.4).sp
                    ),
                    maxLines = 2,
                    overflow = TextOverflow.Ellipsis,
                    color = Color.Black
                )

                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    Box(
                        modifier = Modifier
                            .size(10.dp)
                            .clip(CircleShape)
                            .background(InstitutionalBlue)
                    )
                    Text(
                        text = candidato.partido.nombrePartido,
                        style = MaterialTheme.typography.bodyMedium.copy(
                            fontWeight = FontWeight.SemiBold,
                            color = InstitutionalBlue
                        ),
                        maxLines = 1,
                        overflow = TextOverflow.Ellipsis
                    )
                }

                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.spacedBy(6.dp)
                ) {
                    Icon(
                        imageVector = Icons.Outlined.LocationOn,
                        contentDescription = null,
                        modifier = Modifier.size(16.dp),
                        tint = NeutralMedium
                    )
                    Text(
                        text = regionToText(candidato.region!!),
                        style = MaterialTheme.typography.bodySmall.copy(
                            color = NeutralMedium,
                            fontWeight = FontWeight.Medium
                        ),
                        maxLines = 1,
                        overflow = TextOverflow.Ellipsis
                    )
                }
            }
        }
    }
}

data class CarouselItem(
    @DrawableRes val imageRes: Int,
    val title: String,
    val description: String
)

fun regionToText(region: JsonElement): String {
    return when (region) {
        is JsonPrimitive -> if (region.isString) region.asString else ""
        is JsonObject -> {
            val keys = listOf("nombre_region", "nombre", "name", "region")
            keys.firstOrNull { region.has(it) && region.get(it).isJsonPrimitive && region.get(it).asJsonPrimitive.isString }
                ?.let { region.get(it).asJsonPrimitive.asString }
                ?: ""
        }
        else -> ""
    }
}

val carouselItems = listOf(
    CarouselItem(
        imageRes = R.drawable.carrusel_foto_1,
        title = "Vota Informado",
        description = "Conoce las propuestas y el historial de cada candidato antes de decidir tu voto"
    ),
    CarouselItem(
        imageRes = R.drawable.carrusel_foto_2,
        title = "Transparencia Electoral",
        description = "Accede a información verificada sobre denuncias, proyectos de ley y trayectoria política"
    ),
    CarouselItem(
        imageRes = R.drawable.carrusel_foto_3,
        title = "Simulador de Votación",
        description = "Participa en nuestra encuesta y conoce las tendencias electorales en tiempo real"
    )
)