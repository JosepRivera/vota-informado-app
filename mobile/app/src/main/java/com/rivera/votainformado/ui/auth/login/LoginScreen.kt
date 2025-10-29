package com.rivera.votainformado.ui.auth.login

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.text.input.VisualTransformation
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.lifecycle.ViewModelProvider
import com.rivera.votainformado.ui.components.ErrorMessage
import com.rivera.votainformado.ui.components.SuccessMessage
import com.rivera.votainformado.util.TokenManager

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun LoginScreen(
    onNavigateToHome: () -> Unit,
    onNavigateToRegister: () -> Unit,
    onBack: () -> Unit
) {
    val context = LocalContext.current
    val tokenManager = remember { TokenManager(context) }

    val viewModel: LoginViewModel = viewModel(
        factory = object : ViewModelProvider.Factory {
            override fun <T : ViewModel> create(modelClass: Class<T>): T {
                @Suppress("UNCHECKED_CAST")
                return LoginViewModel(tokenManager) as T
            }
        }
    )

    val state by viewModel.loginState.collectAsState()
    val colorScheme = MaterialTheme.colorScheme

    var dni by remember { mutableStateOf("") }
    var password by remember { mutableStateOf("") }
    var passwordVisible by remember { mutableStateOf(false) }

    LaunchedEffect(state.successMessage) {
        if (state.successMessage != null) onNavigateToHome()
    }

    Scaffold(
        topBar = {
            TopAppBar(
                navigationIcon = {
                    IconButton(onClick = onBack) {
                        Icon(
                            imageVector = Icons.Default.Close,
                            contentDescription = "Cerrar",
                            tint = colorScheme.primary
                        )
                    }
                },
                title = {},
                colors = TopAppBarDefaults.topAppBarColors(
                    containerColor = colorScheme.background
                )
            )
        }
    ) { innerPadding ->
        Box(
            modifier = Modifier
                .fillMaxSize()
                .background(colorScheme.background)
                .padding(innerPadding)
        ) {
            Column(
                modifier = Modifier
                    .fillMaxSize()
                    .verticalScroll(rememberScrollState())
                    .padding(horizontal = 24.dp, vertical = 12.dp),
                horizontalAlignment = Alignment.CenterHorizontally,
                verticalArrangement = Arrangement.Center
            ) {
                // 🏛️ Encabezado institucional
                Column(
                    horizontalAlignment = Alignment.CenterHorizontally,
                    modifier = Modifier.padding(bottom = 32.dp)
                ) {
                    Text(
                        text = "VotaInformado",
                        fontSize = 42.sp,
                        fontWeight = FontWeight.ExtraBold,
                        fontFamily = FontFamily.Serif,
                        color = colorScheme.primary
                    )
                    Spacer(modifier = Modifier.height(4.dp))
                    Text(
                        text = "Ingresa con tu DNI para continuar informándote sobre tus candidatos y ejercer un voto consciente.",
                        fontSize = 14.sp,
                        lineHeight = 18.sp,
                        color = colorScheme.onSurfaceVariant.copy(alpha = 0.8f),
                        modifier = Modifier.padding(horizontal = 12.dp),
                        textAlign = TextAlign.Center
                    )
                }

                OutlinedTextField(
                    value = dni,
                    onValueChange = { if (it.length <= 8 && it.all { c -> c.isDigit() }) dni = it },
                    label = { Text("DNI") },
                    leadingIcon = { Icon(Icons.Default.Badge, contentDescription = null) },
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(bottom = 16.dp),
                    singleLine = true,
                    keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
                    colors = OutlinedTextFieldDefaults.colors(
                        focusedBorderColor = colorScheme.primary,
                        focusedLabelColor = colorScheme.primary,
                        cursorColor = colorScheme.primary
                    ),
                    enabled = !state.isLoading
                )

                OutlinedTextField(
                    value = password,
                    onValueChange = { password = it },
                    label = { Text("Contraseña") },
                    leadingIcon = { Icon(Icons.Default.Lock, contentDescription = null) },
                    trailingIcon = {
                        IconButton(onClick = { passwordVisible = !passwordVisible }) {
                            Icon(
                                imageVector = if (passwordVisible)
                                    Icons.Default.Visibility
                                else
                                    Icons.Default.VisibilityOff,
                                contentDescription = if (passwordVisible) "Ocultar contraseña" else "Mostrar contraseña"
                            )
                        }
                    },
                    visualTransformation = if (passwordVisible) VisualTransformation.None else PasswordVisualTransformation(),
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(bottom = 24.dp),
                    singleLine = true,
                    keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Password),
                    colors = OutlinedTextFieldDefaults.colors(
                        focusedBorderColor = colorScheme.primary,
                        focusedLabelColor = colorScheme.primary,
                        cursorColor = colorScheme.primary
                    ),
                    enabled = !state.isLoading
                )

                state.errorMessage?.let { ErrorMessage(it) }
                state.successMessage?.let { SuccessMessage(it) }

                Spacer(modifier = Modifier.height(20.dp))

                Button(
                    onClick = {
                        if (dni.isNotBlank() && password.isNotBlank()) {
                            viewModel.login(dni, password)
                        }
                    },
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(56.dp),
                    colors = ButtonDefaults.buttonColors(
                        containerColor = colorScheme.primary
                    ),
                    shape = RoundedCornerShape(12.dp),
                    enabled = !state.isLoading && dni.isNotBlank() && password.isNotBlank()
                ) {
                    if (state.isLoading) {
                        CircularProgressIndicator(
                            color = colorScheme.onPrimary,
                            modifier = Modifier.size(24.dp)
                        )
                    } else {
                        Text(
                            text = "Iniciar Sesión",
                            fontSize = 16.sp,
                            fontWeight = FontWeight.Bold,
                            color = colorScheme.onPrimary
                        )
                    }
                }

                Spacer(modifier = Modifier.height(24.dp))

                TextButton(
                    onClick = onNavigateToRegister,
                    enabled = !state.isLoading
                ) {
                    Text(
                        text = "¿No tienes cuenta? Regístrate aquí",
                        color = colorScheme.primary,
                        fontSize = 14.sp
                    )
                }
            }
        }
    }
}