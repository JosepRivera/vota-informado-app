package com.rivera.votainformado.ui.home

import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.AccountCircle
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.pager.HorizontalPager
import androidx.compose.foundation.pager.rememberPagerState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.filled.*
import androidx.compose.material.icons.filled.LocationOn
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.sp
import coil.compose.rememberAsyncImagePainter
import com.rivera.votainformado.ui.theme.*
import com.rivera.votainformado.data.model.candidatos.CandidatoItem
import com.google.gson.JsonElement
import com.google.gson.JsonObject
import com.google.gson.JsonPrimitive
import androidx.annotation.DrawableRes
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
    val isDarkMode = isSystemInDarkTheme()
    var searchQuery by remember { mutableStateOf("") }
    var menuExpanded by remember { mutableStateOf(false) }

    val viewModel: HomeViewModel = androidx.lifecycle.viewmodel.compose.viewModel()
    val state by viewModel.state

    val pagerState = rememberPagerState(
        initialPage = 0,
        pageCount = { carouselItems.size }
    )

    // Auto-scroll para el carrusel
    LaunchedEffect(key1 = pagerState) {
        while (true) {
            kotlinx.coroutines.delay(4000)
            val nextPage = (pagerState.currentPage + 1) % pagerState.pageCount
            pagerState.animateScrollToPage(nextPage)
        }
    }

    Scaffold(
        topBar = {
            TopAppBar(
                title = {
                    Text(
                        text = "Vota Informado",
                        style = MaterialTheme.typography.titleLarge.copy(
                            fontWeight = FontWeight.Bold,
                            color = if (isDarkMode) InstitutionalBlueLight else InstitutionalBlue
                        )
                    )
                },
                navigationIcon = {
                    Box {
                        IconButton(onClick = { menuExpanded = true }) {
                            Icon(
                                imageVector = Icons.Default.Menu,
                                contentDescription = "Menú",
                                tint = if (isDarkMode) InstitutionalBlueLight else InstitutionalBlue,
                                modifier = Modifier.size(28.dp)
                            )
                        }

                        DropdownMenu(
                            expanded = menuExpanded,
                            onDismissRequest = { menuExpanded = false },
                            modifier = Modifier
                                .background(
                                    color = if (isDarkMode) DarkSurf else NeutralWhite,
                                    shape = RoundedCornerShape(12.dp)
                                )
                        ) {
                            DropdownMenuItem(
                                text = { Text("Inicio") },
                                onClick = {
                                    menuExpanded = false
                                    onNavigate("home")
                                },
                                leadingIcon = {
                                    Icon(Icons.Default.Home, contentDescription = null)
                                }
                            )
                            DropdownMenuItem(
                                text = { Text("Resultados") },
                                onClick = {
                                    menuExpanded = false
                                    onNavigate("resultados")
                                },
                                leadingIcon = {
                                    Icon(Icons.Default.Leaderboard, contentDescription = null)
                                }
                            )
                            DropdownMenuItem(
                                text = { Text("Votar") },
                                onClick = {
                                    menuExpanded = false
                                    onNavigate("votar")
                                },
                                leadingIcon = {
                                    Icon(Icons.Default.HowToVote, contentDescription = null)
                                }
                            )
                            DropdownMenuItem(
                                text = { Text("Comparar") },
                                onClick = {
                                    menuExpanded = false
                                    onNavigate("comparar")
                                },
                                leadingIcon = {
                                    Icon(Icons.Default.Compare, contentDescription = null)
                                }
                            )
                            DropdownMenuItem(
                                text = { Text("Configuración") },
                                onClick = {
                                    menuExpanded = false
                                },
                                leadingIcon = {
                                    Icon(Icons.Default.Settings, contentDescription = null)
                                }
                            )
                            DropdownMenuItem(
                                text = { Text("Acerca de") },
                                onClick = {
                                    menuExpanded = false
                                },
                                leadingIcon = {
                                    Icon(Icons.Default.Info, contentDescription = null)
                                }
                            )
                        }
                    }
                },
                actions = {
                    IconButton(onClick = onProfileClick) {
                        Icon(
                            imageVector = Icons.Default.AccountCircle,
                            contentDescription = "Perfil",
                            tint = if (isDarkMode) InstitutionalBlueLight else InstitutionalBlue,
                            modifier = Modifier.size(28.dp)
                        )
                    }
                },
                colors = TopAppBarDefaults.topAppBarColors(
                    containerColor = MaterialTheme.colorScheme.surface
                )
            )
        },
        bottomBar = {
            NavigationBar(
                containerColor = MaterialTheme.colorScheme.surface,
                tonalElevation = 8.dp,
                modifier = Modifier.shadow(4.dp, RoundedCornerShape(topStart = 16.dp, topEnd = 16.dp))
            ) {
                NavigationBarItem(
                    selected = true,
                    onClick = { onNavigate("home") },
                    icon = { Icon(Icons.Default.Home, contentDescription = "Inicio") },
                    label = { Text("Inicio", style = MaterialTheme.typography.labelMedium) },
                    colors = NavigationBarItemDefaults.colors(
                        selectedIconColor = InstitutionalBlue,
                        selectedTextColor = InstitutionalBlue,
                        indicatorColor = InstitutionalBlue.copy(alpha = 0.15f),
                        unselectedIconColor = NeutralMedium,
                        unselectedTextColor = NeutralMedium
                    )
                )
                NavigationBarItem(
                    selected = false,
                    onClick = { onNavigate("resultados") },
                    icon = { Icon(Icons.Default.Leaderboard, contentDescription = "Resultados") },
                    label = { Text("Resultados", style = MaterialTheme.typography.labelMedium) },
                    colors = NavigationBarItemDefaults.colors(
                        selectedIconColor = InstitutionalBlue,
                        selectedTextColor = InstitutionalBlue,
                        indicatorColor = InstitutionalBlue.copy(alpha = 0.15f),
                        unselectedIconColor = NeutralMedium,
                        unselectedTextColor = NeutralMedium
                    )
                )
                NavigationBarItem(
                    selected = false,
                    onClick = { onNavigate("votar") },
                    icon = { Icon(Icons.Default.HowToVote, contentDescription = "Votar") },
                    label = { Text("Votar", style = MaterialTheme.typography.labelMedium) },
                    colors = NavigationBarItemDefaults.colors(
                        selectedIconColor = InstitutionalBlue,
                        selectedTextColor = InstitutionalBlue,
                        indicatorColor = InstitutionalBlue.copy(alpha = 0.15f),
                        unselectedIconColor = NeutralMedium,
                        unselectedTextColor = NeutralMedium
                    )
                )
                NavigationBarItem(
                    selected = false,
                    onClick = { onNavigate("comparar") },
                    icon = { Icon(Icons.Default.Compare, contentDescription = "Comparar") },
                    label = { Text("Comparar", style = MaterialTheme.typography.labelMedium) },
                    colors = NavigationBarItemDefaults.colors(
                        selectedIconColor = InstitutionalBlue,
                        selectedTextColor = InstitutionalBlue,
                        indicatorColor = InstitutionalBlue.copy(alpha = 0.15f),
                        unselectedIconColor = NeutralMedium,
                        unselectedTextColor = NeutralMedium
                    )
                )
            }
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
                    .background(MaterialTheme.colorScheme.background)
            ) {
                // Carrusel con imágenes redondeadas y desplazamiento en bloques
                item {
                    Box(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(start = 16.dp, top = 8.dp, end = 16.dp, bottom = 20.dp)
                            .height(210.dp),
                        contentAlignment = Alignment.Center
                    ) {
                        HorizontalPager(
                            state = pagerState,
                            pageSpacing = 16.dp,
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(horizontal = 16.dp)
                        ) { page ->
                            CarouselCard(
                                item = carouselItems[page],
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .clip(RoundedCornerShape(20.dp))
                                    .shadow(6.dp, RoundedCornerShape(20.dp))
                            )
                        }
                    }
                }

                // Barra de búsqueda con mejor espaciado
                item {
                    SearchBar(
                        query = searchQuery,
                        onQueryChange = { searchQuery = it },
                        isDarkMode = isDarkMode,
                        modifier = Modifier.padding(horizontal = 20.dp, vertical = 8.dp)
                    )
                }

                // Estado de carga o error con mejor diseño
                if (state.isLoading && state.candidatos.isEmpty()) {
                    item {
                        Box(
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(32.dp),
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
                }

                if (state.errorMessage != null) {
                    item {
                        Card(
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(horizontal = 20.dp, vertical = 12.dp),
                            colors = CardDefaults.cardColors(
                                containerColor = ErrorRed.copy(alpha = 0.1f)
                            ),
                            shape = RoundedCornerShape(12.dp)
                        ) {
                            Row(
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .padding(16.dp),
                                verticalAlignment = Alignment.CenterVertically,
                                horizontalArrangement = Arrangement.spacedBy(12.dp)
                            ) {
                                Icon(
                                    imageVector = Icons.Default.Error,
                                    contentDescription = null,
                                    tint = ErrorRed,
                                    modifier = Modifier.size(24.dp)
                                )
                                Text(
                                    text = state.errorMessage!!,
                                    color = ErrorRed,
                                    style = MaterialTheme.typography.bodyMedium
                                )
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
                        Column(
                            modifier = Modifier.padding(horizontal = 20.dp, vertical = 12.dp)
                        ) {
                            Text(
                                text = if (filteredCandidates.isEmpty()) {
                                    "No se encontraron resultados"
                                } else {
                                    "${filteredCandidates.size} resultado${if (filteredCandidates.size > 1) "s" else ""} encontrado${if (filteredCandidates.size > 1) "s" else ""}"
                                },
                                style = MaterialTheme.typography.titleMedium.copy(
                                    fontWeight = FontWeight.Bold,
                                    color = if (isDarkMode) InstitutionalBlueLight else InstitutionalBlue
                                )
                            )
                            if (filteredCandidates.isNotEmpty()) {
                                Spacer(modifier = Modifier.height(4.dp))
                                Text(
                                    text = "Toca un candidato para ver más detalles",
                                    style = MaterialTheme.typography.bodySmall,
                                    color = if (isDarkMode) NeutralGray else NeutralMedium
                                )
                            }
                        }
                    }

                    items(filteredCandidates) { candidato ->
                        SearchResultCard(
                            candidato = candidato,
                            isDarkMode = isDarkMode,
                            onClick = { onNavigateToDetail(candidato.id) },
                            modifier = Modifier.padding(horizontal = 20.dp, vertical = 6.dp)
                        )
                    }
                }

                // Candidatos Presidenciales
                item {
                    CandidateSection(
                        title = "Candidatos Presidenciales",
                        candidates = state.candidatos.filter { it.cargo.nombreCargo.equals("Presidente", ignoreCase = true) },
                        isDarkMode = isDarkMode,
                        onCandidateClick = onNavigateToDetail
                    )
                }

                // Candidatos al Senado
                item {
                    CandidateSection(
                        title = "Candidatos al Senado",
                        candidates = state.candidatos.filter { it.cargo.nombreCargo.equals("Senador", ignoreCase = true) },
                        isDarkMode = isDarkMode,
                        onCandidateClick = onNavigateToDetail
                    )
                }

                // Candidatos a Diputados
                item {
                    CandidateSection(
                        title = "Candidatos a Diputados",
                        candidates = state.candidatos.filter { it.cargo.nombreCargo.equals("Diputado", ignoreCase = true) || it.cargo.nombreCargo.equals("Diputada", ignoreCase = true) },
                        isDarkMode = isDarkMode,
                        onCandidateClick = onNavigateToDetail
                    )
                }

                // Espaciado final
                item {
                    Spacer(modifier = Modifier.height(16.dp))
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
    Box(
        modifier = modifier
            .aspectRatio(1.8f)
            .clip(RoundedCornerShape(20.dp))
            .background(Color.Gray.copy(alpha = 0.1f))
    ) {
        // Imagen de fondo
        Image(
            painter = painterResource(id = item.imageRes),
            contentDescription = null,
            modifier = Modifier.fillMaxSize(),
            contentScale = ContentScale.Crop
        )

        // Gradiente inferior
        Box(
            modifier = Modifier
                .fillMaxSize()
                .background(
                    Brush.verticalGradient(
                        colors = listOf(Color.Transparent, Color.Black.copy(alpha = 0.7f)),
                        startY = 250f
                    )
                )
        )

        // Texto del carrusel
        Column(
            modifier = Modifier
                .align(Alignment.BottomStart)
                .padding(16.dp)
        ) {
            Text(
                text = item.title,
                style = MaterialTheme.typography.titleLarge.copy(
                    fontWeight = FontWeight.Bold,
                    color = Color.White
                )
            )
            Spacer(modifier = Modifier.height(4.dp))
            Text(
                text = item.description,
                style = MaterialTheme.typography.bodyMedium.copy(color = Color.White.copy(alpha = 0.9f)),
                maxLines = 2,
                overflow = TextOverflow.Ellipsis
            )
        }
    }
}

@Composable
fun SearchBar(
    query: String,
    onQueryChange: (String) -> Unit,
    isDarkMode: Boolean,
    modifier: Modifier = Modifier
) {
    OutlinedTextField(
        value = query,
        onValueChange = onQueryChange,
        modifier = modifier.fillMaxWidth(),
        placeholder = {
            Text(
                "Buscar candidato, partido o cargo...",
                style = MaterialTheme.typography.bodyMedium,
                color = if (isDarkMode) NeutralGray else NeutralMedium
            )
        },
        leadingIcon = {
            Icon(
                Icons.Default.Search,
                contentDescription = "Buscar",
                tint = if (isDarkMode) InstitutionalBlueLight else InstitutionalBlue
            )
        },
        trailingIcon = {
            if (query.isNotEmpty()) {
                IconButton(onClick = { onQueryChange("") }) {
                    Icon(
                        Icons.Default.Clear,
                        contentDescription = "Limpiar",
                        tint = if (isDarkMode) NeutralGray else NeutralMedium
                    )
                }
            }
        },
        colors = TextFieldDefaults.colors(
            focusedContainerColor = if (isDarkMode) DarkSurfVar else NeutralWhite,
            unfocusedContainerColor = if (isDarkMode) DarkSurfVar else NeutralWhite,
            focusedIndicatorColor = if (isDarkMode) InstitutionalBlueLight else InstitutionalBlue,
            unfocusedIndicatorColor = if (isDarkMode) DarkSurfVar else NeutralGray,
            cursorColor = if (isDarkMode) InstitutionalBlueLight else InstitutionalBlue,
            focusedTextColor = MaterialTheme.colorScheme.onSurface,
            unfocusedTextColor = MaterialTheme.colorScheme.onSurface
        ),
        shape = RoundedCornerShape(16.dp),
        singleLine = true
    )
}

@Composable
fun CandidateSection(
    title: String,
    candidates: List<CandidatoItem>,
    isDarkMode: Boolean,
    onCandidateClick: (Int) -> Unit = {}
) {
    Column(
        modifier = Modifier
            .fillMaxWidth()
            .padding(top = 32.dp, bottom = 16.dp)
    ) {
        // Encabezado de sección con mejor diseño
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 20.dp, vertical = 12.dp),
            horizontalArrangement = Arrangement.Start,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Text(
                text = title,
                style = MaterialTheme.typography.titleLarge.copy(
                    fontWeight = FontWeight.Bold,
                    color = if (isDarkMode) InstitutionalBlueLight else InstitutionalBlue
                )
            )
        }

        // Restaurar estructura original con LazyRow y 3 candidatos por columna
        val columns = remember(candidates) { candidates.chunked(3) }
        val listState = androidx.compose.foundation.lazy.rememberLazyListState()

        LazyRow(
            state = listState,
            contentPadding = PaddingValues(horizontal = 16.dp),
            horizontalArrangement = Arrangement.spacedBy(12.dp),
            modifier = Modifier.fillMaxWidth(),
            flingBehavior = androidx.compose.foundation.gestures.snapping.rememberSnapFlingBehavior(
                lazyListState = listState
            )
        ) {
            items(columns) { trio ->
                Column(
                    verticalArrangement = Arrangement.spacedBy(12.dp),
                    modifier = Modifier.width(300.dp)
                ) {
                    trio.forEach { candidatoItem ->
                        CandidatoCard(
                            candidatoItem,
                            isDarkMode,
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
    isDarkMode: Boolean,
    onClick: () -> Unit = {},
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
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .height(100.dp)
                .padding(12.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            // Foto del candidato
            Box(
                modifier = Modifier
                    .size(76.dp)
                    .clip(RoundedCornerShape(12.dp))
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

            Spacer(modifier = Modifier.width(12.dp))

            Column(
                modifier = Modifier
                    .weight(1f)
                    .fillMaxHeight(),
                verticalArrangement = Arrangement.Center
            ) {
                Text(
                    text = candidato.nombreCompleto,
                    style = MaterialTheme.typography.titleMedium.copy(
                        fontWeight = FontWeight.Bold
                    ),
                    maxLines = 2,
                    overflow = TextOverflow.Ellipsis
                )
                Spacer(modifier = Modifier.height(6.dp))
                Row(
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Box(
                        modifier = Modifier
                            .size(8.dp)
                            .clip(CircleShape)
                            .background(if (isDarkMode) InstitutionalBlueLight else InstitutionalBlue)
                    )
                    Spacer(modifier = Modifier.width(6.dp))
                    Text(
                        text = candidato.partido.nombrePartido,
                        style = MaterialTheme.typography.bodyMedium.copy(
                            fontWeight = FontWeight.SemiBold,
                            color = if (isDarkMode) InstitutionalBlueLight else InstitutionalBlue
                        ),
                        maxLines = 1,
                        overflow = TextOverflow.Ellipsis
                    )
                }
                Spacer(modifier = Modifier.height(4.dp))
                Text(
                    text = "${candidato.cargo.nombreCargo} • ${regionToText(candidato.region)}",
                    style = MaterialTheme.typography.bodySmall,
                    color = if (isDarkMode) NeutralGray else NeutralMedium,
                    maxLines = 1,
                    overflow = TextOverflow.Ellipsis
                )
            }
        }
    }
}

@Composable
fun CandidatoCard(
    candidato: CandidatoItem,
    isDarkMode: Boolean,
    onClick: () -> Unit = {},
    modifier: Modifier = Modifier,
    isFullWidth: Boolean = false
) {
    Card(
        modifier = modifier
            .then(if (isFullWidth) Modifier.fillMaxWidth() else Modifier.width(300.dp))
            .height(130.dp),
        shape = RoundedCornerShape(16.dp),
        colors = CardDefaults.cardColors(
            containerColor = if (isDarkMode) DarkSurfVar else NeutralWhite
        ),
        elevation = CardDefaults.cardElevation(defaultElevation = 2.dp),
        onClick = onClick
    ) {
        Row(
            modifier = Modifier
                .fillMaxSize()
                .padding(16.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            // Foto del candidato con mejor diseño
            Box(
                modifier = Modifier
                    .size(80.dp)
                    .clip(RoundedCornerShape(14.dp))
                    .shadow(4.dp, RoundedCornerShape(14.dp))
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

            Spacer(modifier = Modifier.width(14.dp))

            Column(
                modifier = Modifier
                    .weight(1f)
                    .fillMaxHeight(),
                verticalArrangement = Arrangement.Center
            ) {
                Text(
                    text = candidato.nombreCompleto,
                    style = MaterialTheme.typography.titleMedium.copy(
                        fontWeight = FontWeight.Bold,
                        letterSpacing = (-0.5).sp
                    ),
                    maxLines = 2,
                    overflow = TextOverflow.Ellipsis,
                    color = MaterialTheme.colorScheme.onSurface
                )
                Spacer(modifier = Modifier.height(8.dp))
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    modifier = Modifier.padding(end = 4.dp)
                ) {
                    Box(
                        modifier = Modifier
                            .size(8.dp)
                            .clip(CircleShape)
                            .background(if (isDarkMode) InstitutionalBlueLight else InstitutionalBlue)
                    )
                    Spacer(modifier = Modifier.width(8.dp))
                    Text(
                        text = candidato.partido.nombrePartido,
                        style = MaterialTheme.typography.bodyMedium.copy(
                            fontWeight = FontWeight.SemiBold,
                            color = if (isDarkMode) InstitutionalBlueLight else InstitutionalBlue
                        ),
                        maxLines = 1,
                        overflow = TextOverflow.Ellipsis
                    )
                }
                Spacer(modifier = Modifier.height(6.dp))
                Row(
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Icon(
                        imageVector = Icons.Default.LocationOn,
                        contentDescription = null,
                        modifier = Modifier.size(14.dp),
                        tint = if (isDarkMode) NeutralGray else NeutralMedium
                    )
                    Spacer(modifier = Modifier.width(4.dp))
                    Text(
                        text = regionToText(candidato.region),
                        style = MaterialTheme.typography.bodySmall,
                        color = if (isDarkMode) NeutralGray else NeutralMedium,
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

fun regionToText(region: JsonElement?): String {
    if (region == null || region.isJsonNull) return "-"
    return when (region) {
        is JsonPrimitive -> if (region.isString) region.asString else "-"
        is JsonObject -> {
            // Intentamos leer llaves comunes
            val candidates = listOf("nombre", "nombre_region", "name", "region")
            for (key in candidates) {
                if (region.has(key) && region.get(key).isJsonPrimitive) {
                    val prim = region.get(key).asJsonPrimitive
                    if (prim.isString) return prim.asString
                }
            }
            // Si no hay una llave conocida, devolvemos el id o "-"
            if (region.has("id") && region.get("id").isJsonPrimitive) {
                return "Región #" + region.get("id").asInt
            }
            "-"
        }
        else -> "-"
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