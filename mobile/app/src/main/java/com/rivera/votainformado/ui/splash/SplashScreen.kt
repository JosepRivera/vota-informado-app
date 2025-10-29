package com.rivera.votainformado.ui.splash

import androidx.compose.animation.core.*
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.foundation.layout.*
import androidx.compose.material3.Text
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.alpha
import androidx.compose.ui.draw.scale
import androidx.compose.ui.geometry.CornerRadius
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.geometry.Size
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.rivera.votainformado.R
import com.rivera.votainformado.ui.theme.*
import kotlinx.coroutines.delay

@Composable
fun SplashScreen(onTimeout: () -> Unit) {
    val isDarkMode = isSystemInDarkTheme()

    val scale = remember { Animatable(0.8f) }
    val alpha = remember { Animatable(0f) }
    val progress = remember { Animatable(0f) }
    val textAlpha = remember { Animatable(0f) }

    val splashDuration = 2500L

    LaunchedEffect(true) {
        // Animación del logo con efecto de rebote
        alpha.animateTo(
            targetValue = 1f,
            animationSpec = tween(durationMillis = 600)
        )
        scale.animateTo(
            targetValue = 1.05f,
            animationSpec = spring(
                dampingRatio = Spring.DampingRatioMediumBouncy,
                stiffness = Spring.StiffnessLow
            )
        )
        scale.animateTo(
            targetValue = 1f,
            animationSpec = tween(durationMillis = 300)
        )

        // Aparición del texto
        textAlpha.animateTo(
            targetValue = 1f,
            animationSpec = tween(durationMillis = 400)
        )

        // Progreso de carga
        progress.animateTo(
            targetValue = 1f,
            animationSpec = tween(
                durationMillis = 1800,
                easing = FastOutSlowInEasing
            )
        )

        delay(100)
        onTimeout()
    }

    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(
                if (isDarkMode) {
                    Brush.verticalGradient(
                        colors = listOf(DarkBg, DarkSurf)
                    )
                } else {
                    Brush.verticalGradient(
                        colors = listOf(NeutralLight, NeutralWhite)
                    )
                }
            ),
        contentAlignment = Alignment.Center
    ) {
        Column(
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.Center
        ) {
            // Logo con animación
            Image(
                painter = painterResource(id = R.drawable.logo_vota_informado_2),
                contentDescription = "Logo Vota Informado",
                modifier = Modifier
                    .size(180.dp)
                    .scale(scale.value)
                    .alpha(alpha.value)
            )

            Spacer(modifier = Modifier.height(24.dp))

            // Texto con fade in
            Text(
                text = "Vota Informado",
                fontSize = 28.sp,
                fontWeight = FontWeight.Bold,
                color = if (isDarkMode) InstitutionalBlueLight else InstitutionalBlue,
                modifier = Modifier.alpha(textAlpha.value)
            )

            Text(
                text = "Tu voz, tu decisión",
                fontSize = 14.sp,
                color = if (isDarkMode) NeutralGray else NeutralMedium,
                modifier = Modifier
                    .alpha(textAlpha.value)
                    .padding(top = 4.dp)
            )

            Spacer(modifier = Modifier.height(48.dp))

            // Barra de progreso mejorada con gradiente
            EnhancedProgressBar(
                progress = progress.value,
                modifier = Modifier
                    .width(220.dp)
                    .height(5.dp),
                isDarkMode = isDarkMode
            )
        }
    }
}

@Composable
fun EnhancedProgressBar(
    progress: Float,
    modifier: Modifier = Modifier,
    isDarkMode: Boolean
) {
    val infiniteTransition = rememberInfiniteTransition(label = "shimmer")
    val shimmerOffset by infiniteTransition.animateFloat(
        initialValue = 0f,
        targetValue = 1f,
        animationSpec = infiniteRepeatable(
            animation = tween(1500, easing = LinearEasing),
            repeatMode = RepeatMode.Restart
        ),
        label = "shimmer"
    )

    Canvas(modifier = modifier) {
        val canvasWidth = size.width
        val canvasHeight = size.height
        val cornerRadius = canvasHeight / 2

        // Track (fondo)
        drawRoundRect(
            color = if (isDarkMode) DarkSurfVar else NeutralGray,
            topLeft = Offset(0f, 0f),
            size = Size(canvasWidth, canvasHeight),
            cornerRadius = CornerRadius(cornerRadius, cornerRadius)
        )

        // Progress con gradiente
        if (progress > 0) {
            val progressWidth = canvasWidth * progress

            val gradient = Brush.horizontalGradient(
                colors = if (isDarkMode) {
                    listOf(InstitutionalBlueLight, CivicGreenLight)
                } else {
                    listOf(InstitutionalBlue, CivicGreen)
                },
                startX = 0f,
                endX = progressWidth
            )

            drawRoundRect(
                brush = gradient,
                topLeft = Offset(0f, 0f),
                size = Size(progressWidth, canvasHeight),
                cornerRadius = CornerRadius(cornerRadius, cornerRadius)
            )
        }
    }
}