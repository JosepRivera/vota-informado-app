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
import androidx.compose.material.icons.automirrored.filled.ArrowForward
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.text.style.TextOverflow
import coil.compose.rememberAsyncImagePainter
import com.rivera.votainformado.ui.theme.*
import com.rivera.votainformado.data.model.candidatos.CandidatoItem
import com.google.gson.JsonElement
import com.google.gson.JsonObject
import com.google.gson.JsonPrimitive
import androidx.annotation.DrawableRes
import com.rivera.votainformado.R



@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun HomeScreen(
    onProfileClick: () -> Unit = {},
    onNavigate: (String) -> Unit = {}
) {
    val isDarkMode = isSystemInDarkTheme()
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
            kotlinx.coroutines.delay(4000)
            val nextPage = (pagerState.currentPage + 1) % pagerState.pageCount
            pagerState.animateScrollToPage(nextPage)
        }
    }

    Scaffold(
        topBar = {
            TopAppBar(
                title = {
                    Image(
                        painter = painterResource(R.drawable.logo_vota_informado_2),
                        contentDescription = "Logo Vota Informado",
                        modifier = Modifier
                            .height(50.dp)
                            .padding(vertical = 4.dp),
                        contentScale = ContentScale.Fit
                    )
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
                tonalElevation = 3.dp
            ) {
                NavigationBarItem(
                    selected = true,
                    onClick = { onNavigate("home") },
                    icon = { Icon(Icons.Default.Home, contentDescription = "Inicio") },
                    label = { Text("Inicio", style = MaterialTheme.typography.labelMedium) }
                )
                NavigationBarItem(
                    selected = false,
                    onClick = { onNavigate("resultados") },
                    icon = { Icon(Icons.Default.Leaderboard, contentDescription = "Resultados") },
                    label = { Text("Resultados", style = MaterialTheme.typography.labelMedium) }
                )
                NavigationBarItem(
                    selected = false,
                    onClick = { onNavigate("votar") },
                    icon = { Icon(Icons.Default.HowToVote, contentDescription = "Votar") },
                    label = { Text("Votar", style = MaterialTheme.typography.labelMedium) }
                )
                NavigationBarItem(
                    selected = false,
                    onClick = { onNavigate("comparar") },
                    icon = { Icon(Icons.Default.Compare, contentDescription = "Comparar") },
                    label = { Text("Comparar", style = MaterialTheme.typography.labelMedium) }
                )
            }
        }
    ) { innerPadding ->
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
                        .padding(top = 12.dp, bottom = 12.dp)
                        .height(200.dp)
                ) {
                    HorizontalPager(
                        state = pagerState,
                        pageSpacing = 16.dp,
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(horizontal = 22.dp) // margen simétrico para no pegar a la izquierda
                    ) { page ->
                        CarouselCard(
                            item = carouselItems[page],
                            modifier = Modifier
                                .fillMaxWidth(0.88f) // ancho menor para que se vea el siguiente (peek)
                                .align(Alignment.Center)
                                .clip(RoundedCornerShape(20.dp))
                                .shadow(6.dp, RoundedCornerShape(20.dp))
                        )
                    }
                }
            }


            // Barra de búsqueda
            item {
                SearchBar(
                    query = searchQuery,
                    onQueryChange = { searchQuery = it },
                    isDarkMode = isDarkMode,
                    modifier = Modifier.padding(horizontal = 16.dp, vertical = 16.dp)
                )
            }

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

            // Estado de carga o error
            if (state.isLoading) {
                item {
                    Box(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(16.dp),
                        contentAlignment = Alignment.Center
                    ) {
                        CircularProgressIndicator()
                    }
                }
            }
            if (state.errorMessage != null) {
                item {
                    Box(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(horizontal = 16.dp)
                    ) {
                        Text(text = state.errorMessage!!, color = ErrorRed)
                    }
                }
            }

            // Candidatos Presidenciales
            item {
                CandidateSection(
                    title = "Candidatos Presidenciales",
                    candidates = state.candidatos.filter { it.cargo.nombreCargo.equals("Presidente", ignoreCase = true) },
                    isDarkMode = isDarkMode
                )
            }

            // Candidatos al Senado
            item {
                CandidateSection(
                    title = "Candidatos al Senado",
                    candidates = state.candidatos.filter { it.cargo.nombreCargo.equals("Senador", ignoreCase = true) },
                    isDarkMode = isDarkMode
                )
            }

            // Candidatos a Diputados
            item {
                CandidateSection(
                    title = "Candidatos a Diputados",
                    candidates = state.candidatos.filter { it.cargo.nombreCargo.equals("Diputado", ignoreCase = true) || it.cargo.nombreCargo.equals("Diputada", ignoreCase = true) },
                    isDarkMode = isDarkMode
                )
            }

            // Espaciado final
            item {
                Spacer(modifier = Modifier.height(16.dp))
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


@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun SearchBar(
    query: String,
    onQueryChange: (String) -> Unit,
    isDarkMode: Boolean,
    modifier: Modifier = Modifier
) {
    TextField(
        value = query,
        onValueChange = onQueryChange,
        modifier = modifier.fillMaxWidth(),
        placeholder = {
            Text(
                "Buscar candidato...",
                style = MaterialTheme.typography.bodyMedium
            )
        },
        leadingIcon = {
            Icon(
                Icons.Default.Search,
                contentDescription = "Buscar",
                tint = if (isDarkMode) NeutralGray else NeutralMedium
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
            focusedContainerColor = if (isDarkMode) DarkSurfVar else NeutralLight,
            unfocusedContainerColor = if (isDarkMode) DarkSurfVar else NeutralLight,
            focusedIndicatorColor = Color.Transparent,
            unfocusedIndicatorColor = Color.Transparent,
            cursorColor = if (isDarkMode) InstitutionalBlueLight else InstitutionalBlue
        ),

        shape = RoundedCornerShape(12.dp),
        singleLine = true
    )
}

@Composable
fun CandidateSection(
    title: String,
    candidates: List<CandidatoItem>,
    isDarkMode: Boolean
) {
    Column(
        modifier = Modifier
            .fillMaxWidth()
            .padding(top = 24.dp)
    ) {
        // Encabezado de sección
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 16.dp, vertical = 8.dp),
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
            IconButton(onClick = { /* Ver todos */ }) {
                Icon(
                    imageVector = Icons.AutoMirrored.Filled.ArrowForward,
                    contentDescription = "Ver todos",
                    tint = if (isDarkMode) CivicGreenLight else CivicGreen
                )
            }
        }

        val columns = remember(candidates) { candidates.chunked(3) }
        val listState = androidx.compose.foundation.lazy.rememberLazyListState()
        LazyRow(
            state = listState,
            contentPadding = PaddingValues(horizontal = 12.dp),
            horizontalArrangement = Arrangement.spacedBy(12.dp),
            modifier = Modifier.fillMaxWidth(),
            flingBehavior = androidx.compose.foundation.gestures.snapping.rememberSnapFlingBehavior(
                lazyListState = listState
            )
        ) {
            items(columns) { trio ->
                Column(
                    verticalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    trio.forEach { candidatoItem ->
                        CandidatoCard(candidatoItem, isDarkMode)
                    }
                }
            }
        }
    }
}

@Composable
fun CandidatoCard(candidato: CandidatoItem, isDarkMode: Boolean) {
    Row(
        modifier = Modifier
            .width(300.dp)
            .height(120.dp)
            .clickable { }
            .padding(vertical = 4.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        // Foto del candidato
        Box(
            modifier = Modifier
                .size(72.dp)
                .clip(RoundedCornerShape(12.dp))
                .background(if (isDarkMode) DarkSurfVar else NeutralLight)
        ) {
            Image(
                painter = rememberAsyncImagePainter(candidato.fotoUrl ?: candidato.partido.logoUrl.orEmpty()),
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
                        .background(if (isDarkMode) CivicGreenLight else CivicGreen)
                )
                Spacer(modifier = Modifier.width(6.dp))
                Text(
                    text = candidato.partido.sigla.ifBlank { candidato.partido.nombrePartido },
                    style = MaterialTheme.typography.bodyMedium.copy(
                        fontWeight = FontWeight.SemiBold,
                        color = if (isDarkMode) CivicGreenLight else CivicGreen
                    ),
                    maxLines = 2,
                    overflow = TextOverflow.Ellipsis
                )
            }
            Spacer(modifier = Modifier.height(6.dp))
            Text(
                text = regionToText(candidato.region),
                style = MaterialTheme.typography.bodyMedium,
                color = if (isDarkMode) NeutralGray else NeutralMedium,
                maxLines = 1,
                overflow = TextOverflow.Ellipsis
            )
        }
    }
}

// ==========================================
// DATOS DE EJEMPLO
// ==========================================

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