package com.rivera.votainformado.ui.welcome

import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.rivera.votainformado.R
import com.rivera.votainformado.ui.theme.VotaInformadoTheme

@Composable
fun WelcomeScreen(
    onNavigateToHome: () -> Unit,
    onNavigateToRegister: () -> Unit,
    onNavigateToLogin: () -> Unit
) {
    Box(
        modifier = Modifier.fillMaxSize()
    ) {
        Image(
            painter = painterResource(id = R.drawable.palacio_de_gobierno),
            contentDescription = null,
            contentScale = ContentScale.Crop,
            modifier = Modifier.fillMaxSize()
        )

        Box(
            modifier = Modifier
                .fillMaxSize()
                .background(Color.Black.copy(alpha = 0.3f))
        )

        Column(
            modifier = Modifier
                .align(Alignment.Center)
                .padding(bottom = 200.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Text(
                text = "Vota Informado",
                style = MaterialTheme.typography.displayMedium.copy(
                    color = Color.White,
                    fontWeight = FontWeight.ExtraBold,
                    letterSpacing = 1.5.sp,
                    shadow = androidx.compose.ui.graphics.Shadow(
                        color = Color.Black.copy(alpha = 0.6f),
                        blurRadius = 16f
                    )
                )
            )

            Spacer(modifier = Modifier.height(12.dp))

            Text(
                text = "Tu decisión informada fortalece la democracia",
                style = MaterialTheme.typography.titleMedium.copy(
                    color = Color.White,
                    fontWeight = FontWeight.Medium,
                    textAlign = TextAlign.Center,
                    letterSpacing = 0.5.sp,
                    shadow = androidx.compose.ui.graphics.Shadow(
                        color = Color.Black.copy(alpha = 0.7f),
                        blurRadius = 10f
                    )
                ),
                modifier = Modifier.padding(horizontal = 32.dp)
            )
        }

        Column(
            modifier = Modifier
                .fillMaxWidth()
                .align(Alignment.BottomCenter)
                .background(
                    color = MaterialTheme.colorScheme.surface,
                    shape = RoundedCornerShape(topStart = 32.dp, topEnd = 32.dp)
                )
                .padding(24.dp),
            verticalArrangement = Arrangement.spacedBy(12.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Text(
                text = "Comienza Ahora",
                style = MaterialTheme.typography.headlineSmall.copy(
                    color = MaterialTheme.colorScheme.onSurface,
                    fontWeight = FontWeight.Bold
                ),
                modifier = Modifier.padding(bottom = 4.dp)
            )

            Button(
                onClick = onNavigateToHome,
                modifier = Modifier
                    .fillMaxWidth()
                    .height(56.dp),
                colors = ButtonDefaults.buttonColors(
                    containerColor = MaterialTheme.colorScheme.secondaryContainer,
                    contentColor = MaterialTheme.colorScheme.onSecondaryContainer
                ),
                shape = RoundedCornerShape(28.dp),
                elevation = ButtonDefaults.buttonElevation(
                    defaultElevation = 2.dp,
                    pressedElevation = 6.dp
                )
            ) {
                Text(
                    text = "Explorar sin registrarme",
                    style = MaterialTheme.typography.labelLarge.copy(
                        fontWeight = FontWeight.SemiBold
                    )
                )
            }

            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(vertical = 4.dp),
                verticalAlignment = Alignment.CenterVertically
            ) {
                HorizontalDivider(
                    modifier = Modifier.weight(1f),
                    thickness = 1.dp,
                    color = MaterialTheme.colorScheme.onSurfaceVariant.copy(alpha = 0.3f)
                )
                Text(
                    text = "o",
                    style = MaterialTheme.typography.bodyMedium.copy(
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    ),
                    modifier = Modifier.padding(horizontal = 16.dp)
                )
                HorizontalDivider(
                    modifier = Modifier.weight(1f),
                    thickness = 1.dp,
                    color = MaterialTheme.colorScheme.onSurfaceVariant.copy(alpha = 0.3f)
                )
            }

            Button(
                onClick = onNavigateToLogin,
                modifier = Modifier
                    .fillMaxWidth()
                    .height(56.dp),
                colors = ButtonDefaults.buttonColors(
                    containerColor = MaterialTheme.colorScheme.primary
                ),
                shape = RoundedCornerShape(28.dp),
                elevation = ButtonDefaults.buttonElevation(
                    defaultElevation = 2.dp,
                    pressedElevation = 6.dp
                )
            ) {
                Text(
                    text = "Iniciar Sesión",
                    style = MaterialTheme.typography.labelLarge.copy(
                        color = MaterialTheme.colorScheme.onPrimary,
                        fontWeight = FontWeight.SemiBold
                    )
                )
            }

            OutlinedButton(
                onClick = onNavigateToRegister,
                modifier = Modifier
                    .fillMaxWidth()
                    .height(56.dp),
                colors = ButtonDefaults.outlinedButtonColors(
                    contentColor = MaterialTheme.colorScheme.primary
                ),
                border = androidx.compose.foundation.BorderStroke(
                    2.dp,
                    MaterialTheme.colorScheme.primary
                ),
                shape = RoundedCornerShape(28.dp)
            ) {
                Text(
                    text = "Registrarme",
                    style = MaterialTheme.typography.labelLarge.copy(
                        fontWeight = FontWeight.SemiBold
                    )
                )
            }

            Text(
                text = "Al continuar, aceptas nuestros Términos de Servicio\ny Política de Privacidad",
                style = MaterialTheme.typography.bodySmall.copy(
                    color = MaterialTheme.colorScheme.onSurfaceVariant,
                    textAlign = TextAlign.Center,
                    lineHeight = 16.sp
                ),
                modifier = Modifier.padding(top = 8.dp)
            )
        }
    }
}

@Preview(showBackground = true)
@Composable
fun WelcomeScreenPreview() {
    VotaInformadoTheme {
        WelcomeScreen(
            onNavigateToHome = {},
            onNavigateToRegister = {},
            onNavigateToLogin = {}
        )
    }
}
