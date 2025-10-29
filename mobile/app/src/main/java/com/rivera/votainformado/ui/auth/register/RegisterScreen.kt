package com.rivera.votainformado.ui.auth.register

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
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.lifecycle.ViewModelProvider
import com.rivera.votainformado.ui.components.ErrorMessage
import com.rivera.votainformado.ui.components.SuccessMessage
import com.rivera.votainformado.ui.theme.*
import com.rivera.votainformado.util.TokenManager

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun RegisterScreen(
    onNavigateToHome: () -> Unit,
    onNavigateToLogin: () -> Unit,
    onBack: () -> Unit
) {
    val context = LocalContext.current
    val tokenManager = remember { TokenManager(context) }

    val viewModel: RegisterViewModel = viewModel(
        factory = object : ViewModelProvider.Factory {
            override fun <T : ViewModel> create(modelClass: Class<T>): T {
                @Suppress("UNCHECKED_CAST")
                return RegisterViewModel(tokenManager) as T
            }
        }
    )

    val state by viewModel.registerState.collectAsState()

    var dni by remember { mutableStateOf("") }
    var password by remember { mutableStateOf("") }
    var confirmPassword by remember { mutableStateOf("") }
    var passwordVisible by remember { mutableStateOf(false) }
    var confirmPasswordVisible by remember { mutableStateOf(false) }
    var selectedRegion by remember { mutableStateOf<Int?>(null) }
    var expanded by remember { mutableStateOf(false) }
    var showConfirmDialog by remember { mutableStateOf(false) }

    val colorScheme = MaterialTheme.colorScheme

    // Navegar al home cuando el registro sea exitoso
    LaunchedEffect(state.successMessage) {
        if (state.successMessage != null) {
            onNavigateToHome()
        }
    }

    Scaffold(
        topBar = {
            TopAppBar(
                navigationIcon = {
                    IconButton(onClick = onBack) {
                        Icon(
                            imageVector = Icons.Default.Close,
                            contentDescription = "Close",
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
                // --- Encabezado Moderno ---
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
                        text = "Tu voz cuenta. Accede a información verificada sobre candidatos y vota con conocimiento.",
                        fontSize = 14.sp,
                        lineHeight = 18.sp,
                        color = colorScheme.onSurfaceVariant.copy(alpha = 0.8f),
                        modifier = Modifier.padding(horizontal = 12.dp),
                        textAlign = androidx.compose.ui.text.style.TextAlign.Center
                    )
                }

                // --- Campo DNI ---
                OutlinedTextField(
                    value = dni,
                    onValueChange = {
                        if (it.length <= 8 && it.all { char -> char.isDigit() }) {
                            dni = it
                            if (it.length == 8) {
                                viewModel.validateDni(it)
                            } else {
                                viewModel.clearDniValidation()
                            }
                        }
                    },
                    label = { Text("DNI") },
                    leadingIcon = { Icon(Icons.Default.Badge, null) },
                    trailingIcon = {
                        when {
                            state.isValidatingDni -> CircularProgressIndicator(
                                modifier = Modifier.size(20.dp),
                                strokeWidth = 2.dp,
                                color = colorScheme.primary
                            )
                            state.dniValidated != null -> Icon(
                                Icons.Default.CheckCircle,
                                contentDescription = null,
                                tint = SuccessTeal
                            )
                        }
                    },
                    modifier = Modifier.fillMaxWidth(),
                    singleLine = true,
                    keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
                    colors = OutlinedTextFieldDefaults.colors(
                        focusedBorderColor = colorScheme.primary,
                        focusedLabelColor = colorScheme.primary
                    ),
                    enabled = !state.isLoading,
                    supportingText = {
                        Text(
                            text = "Ingresa tu DNI para verificar tus datos con RENIEC",
                            fontSize = 12.sp,
                            color = colorScheme.onSurfaceVariant
                        )
                    }
                )

                Spacer(modifier = Modifier.height(16.dp))

                // --- Datos RENIEC ---
                state.dniValidated?.let { data ->
                    Card(
                        modifier = Modifier.fillMaxWidth(),
                        colors = CardDefaults.cardColors(
                            containerColor = colorScheme.secondaryContainer.copy(alpha = 0.3f)
                        ),
                        shape = RoundedCornerShape(12.dp)
                    ) {
                        Column(
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(16.dp)
                        ) {
                            Text(
                                text = "Datos encontrados:",
                                fontSize = 14.sp,
                                fontWeight = FontWeight.Bold,
                                color = colorScheme.onSurface
                            )
                            Spacer(modifier = Modifier.height(8.dp))
                            Text(
                                text = "${data.nombre} ${data.apellidoPaterno} ${data.apellidoMaterno}",
                                fontSize = 16.sp,
                                fontWeight = FontWeight.SemiBold,
                                color = colorScheme.primary
                            )
                            Spacer(modifier = Modifier.height(4.dp))
                            Text(
                                text = "DNI: ${data.dni}",
                                fontSize = 14.sp,
                                color = colorScheme.onSurfaceVariant
                            )
                        }
                    }
                    Spacer(modifier = Modifier.height(16.dp))
                }

                // --- Región ---
                ExposedDropdownMenuBox(
                    expanded = expanded,
                    onExpandedChange = {
                        if (state.dniValidated != null && !state.isLoading) {
                            expanded = !expanded
                        }
                    }
                ) {
                    OutlinedTextField(
                        value = state.regiones.find { it.id == selectedRegion }?.nombreRegion ?: "",
                        onValueChange = {},
                        readOnly = true,
                        label = { Text("Región") },
                        leadingIcon = { Icon(Icons.Default.LocationOn, null) },
                        trailingIcon = {
                            if (state.isLoadingRegiones) {
                                CircularProgressIndicator(
                                    modifier = Modifier.size(20.dp),
                                    strokeWidth = 2.dp,
                                    color = colorScheme.primary
                                )
                            } else {
                                ExposedDropdownMenuDefaults.TrailingIcon(expanded = expanded)
                            }
                        },
                        modifier = Modifier
                            .fillMaxWidth()
                            .menuAnchor(),
                        colors = OutlinedTextFieldDefaults.colors(
                            focusedBorderColor = colorScheme.primary,
                            focusedLabelColor = colorScheme.primary
                        ),
                        enabled = state.dniValidated != null && !state.isLoading,
                        placeholder = { Text("Selecciona tu región") }
                    )

                    ExposedDropdownMenu(
                        expanded = expanded,
                        onDismissRequest = { expanded = false }
                    ) {
                        state.regiones.forEach { region ->
                            DropdownMenuItem(
                                text = { Text(region.nombreRegion, color = colorScheme.onSurface) },
                                onClick = {
                                    selectedRegion = region.id
                                    expanded = false
                                },
                                leadingIcon = {
                                    Icon(Icons.Default.Place, null, tint = colorScheme.primary)
                                }
                            )
                        }
                    }
                }

                Spacer(modifier = Modifier.height(16.dp))

                // --- Contraseña ---
                OutlinedTextField(
                    value = password,
                    onValueChange = { password = it },
                    label = { Text("Contraseña") },
                    leadingIcon = { Icon(Icons.Default.Lock, null) },
                    trailingIcon = {
                        IconButton(onClick = { passwordVisible = !passwordVisible }) {
                            Icon(
                                imageVector = if (passwordVisible) Icons.Default.Visibility else Icons.Default.VisibilityOff,
                                contentDescription = null
                            )
                        }
                    },
                    visualTransformation = if (passwordVisible) VisualTransformation.None else PasswordVisualTransformation(),
                    modifier = Modifier.fillMaxWidth(),
                    singleLine = true,
                    keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Password),
                    colors = OutlinedTextFieldDefaults.colors(
                        focusedBorderColor = colorScheme.primary,
                        focusedLabelColor = colorScheme.primary
                    ),
                    enabled = state.dniValidated != null && !state.isLoading
                )

                Spacer(modifier = Modifier.height(12.dp))

                // --- Confirmar Contraseña ---
                OutlinedTextField(
                    value = confirmPassword,
                    onValueChange = { confirmPassword = it },
                    label = { Text("Confirmar contraseña") },
                    leadingIcon = { Icon(Icons.Default.Lock, null) },
                    trailingIcon = {
                        IconButton(onClick = { confirmPasswordVisible = !confirmPasswordVisible }) {
                            Icon(
                                imageVector = if (confirmPasswordVisible) Icons.Default.Visibility else Icons.Default.VisibilityOff,
                                contentDescription = null
                            )
                        }
                    },
                    visualTransformation = if (confirmPasswordVisible) VisualTransformation.None else PasswordVisualTransformation(),
                    modifier = Modifier.fillMaxWidth(),
                    singleLine = true,
                    keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Password),
                    colors = OutlinedTextFieldDefaults.colors(
                        focusedBorderColor = colorScheme.primary,
                        focusedLabelColor = colorScheme.primary
                    ),
                    enabled = state.dniValidated != null && !state.isLoading,
                    supportingText = {
                        if (confirmPassword.isNotEmpty() && confirmPassword != password) {
                            Text("Las contraseñas no coinciden", color = colorScheme.error, fontSize = 12.sp)
                        }
                    }
                )

                Spacer(modifier = Modifier.height(8.dp))

                // --- Mensajes ---
                state.errorMessage?.let { ErrorMessage(it) }
                state.successMessage?.let { SuccessMessage(it) }

                Spacer(modifier = Modifier.height(16.dp))

                // --- Botón ---
                Button(
                    onClick = { showConfirmDialog = true },
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(56.dp),
                    colors = ButtonDefaults.buttonColors(
                        containerColor = colorScheme.primary
                    ),
                    shape = RoundedCornerShape(12.dp),
                    enabled = !state.isLoading &&
                            state.dniValidated != null &&
                            selectedRegion != null &&
                            password.length >= 8 &&
                            password == confirmPassword
                ) {
                    if (state.isLoading) {
                        CircularProgressIndicator(
                            color = colorScheme.onPrimary,
                            modifier = Modifier.size(24.dp)
                        )
                    } else {
                        Text(
                            "Registrarse",
                            fontSize = 16.sp,
                            fontWeight = FontWeight.Bold,
                            color = colorScheme.onPrimary
                        )
                    }
                }

                Spacer(modifier = Modifier.height(20.dp))

                TextButton(onClick = onNavigateToLogin, enabled = !state.isLoading) {
                    Text(
                        text = "¿Ya tienes una cuenta? Inicia sesión",
                        color = colorScheme.primary,
                        fontSize = 14.sp
                    )
                }
            }
        }
    }

    // --- Diálogo de confirmación ---
    if (showConfirmDialog) {
        state.dniValidated?.let { data ->
            AlertDialog(
                onDismissRequest = { showConfirmDialog = false },
                containerColor = colorScheme.surface,
                title = {
                    Text("Confirma tus datos", fontWeight = FontWeight.Bold, color = colorScheme.onSurface)
                },
                text = {
                    Column {
                        Text("¿Estos son tus datos correctos?", color = colorScheme.onSurfaceVariant)
                        Spacer(modifier = Modifier.height(12.dp))
                        Text("Nombre: ${data.nombre} ${data.apellidoPaterno} ${data.apellidoMaterno}", fontWeight = FontWeight.SemiBold)
                        Text("DNI: ${data.dni}")
                        Text("Región: ${state.regiones.find { it.id == selectedRegion }?.nombreRegion}")
                    }
                },
                confirmButton = {
                    Button(
                        onClick = {
                            showConfirmDialog = false
                            selectedRegion?.let { regionId ->
                                viewModel.register(dni, regionId.toString(), password)
                            }
                        },
                        colors = ButtonDefaults.buttonColors(containerColor = colorScheme.primary)
                    ) {
                        Text("Sí, registrar", color = colorScheme.onPrimary)
                    }
                },
                dismissButton = {
                    TextButton(onClick = { showConfirmDialog = false }) {
                        Text("Cancelar", color = colorScheme.primary)
                    }
                }
            )
        }
    }
}
