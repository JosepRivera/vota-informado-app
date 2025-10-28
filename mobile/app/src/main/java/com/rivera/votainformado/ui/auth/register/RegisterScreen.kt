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
    var passwordVisible by remember { mutableStateOf(false) }
    var selectedRegion by remember { mutableStateOf<Int?>(null) }
    var expanded by remember { mutableStateOf(false) }
    var showConfirmDialog by remember { mutableStateOf(false) }

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
                            tint = InstitutionalBlue
                        )
                    }
                },
                title = {},
                colors = TopAppBarDefaults.topAppBarColors(
                    containerColor = NeutralLight
                )
            )
        }
    ) { innerPadding ->
        Box(
            modifier = Modifier
                .fillMaxSize()
                .background(NeutralLight)
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
                Text(
                    text = "Fabulae",
                    fontSize = 40.sp,
                    fontWeight = FontWeight.ExtraBold,
                    fontFamily = FontFamily.Serif,
                    color = InstitutionalBlue
                )
                Text(
                    text = "Create your account",
                    fontSize = 16.sp,
                    color = NeutralMedium,
                    modifier = Modifier.padding(top = 4.dp, bottom = 32.dp)
                )

                // Campo DNI
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
                    leadingIcon = {
                        Icon(
                            imageVector = Icons.Default.Badge,
                            contentDescription = null
                        )
                    },
                    trailingIcon = {
                        when {
                            state.isValidatingDni -> CircularProgressIndicator(
                                modifier = Modifier.size(20.dp),
                                strokeWidth = 2.dp
                            )
                            state.dniValidated != null -> Icon(
                                imageVector = Icons.Default.CheckCircle,
                                contentDescription = "DNI válido",
                                tint = SuccessTeal
                            )
                        }
                    },
                    modifier = Modifier.fillMaxWidth(),
                    singleLine = true,
                    keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
                    colors = OutlinedTextFieldDefaults.colors(
                        focusedBorderColor = InstitutionalBlue,
                        focusedLabelColor = InstitutionalBlue,
                        focusedLeadingIconColor = InstitutionalBlue
                    ),
                    enabled = !state.isLoading,
                    supportingText = {
                        Text(
                            text = "Ingresa tu DNI para verificar tus datos con RENIEC",
                            fontSize = 12.sp,
                            color = NeutralMedium
                        )
                    }
                )

                Spacer(modifier = Modifier.height(16.dp))

                // Mostrar datos de RENIEC
                state.dniValidated?.let { data ->
                    Card(
                        modifier = Modifier.fillMaxWidth(),
                        colors = CardDefaults.cardColors(
                            containerColor = CivicGreenLight.copy(alpha = 0.1f)
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
                                color = NeutralDark
                            )
                            Spacer(modifier = Modifier.height(8.dp))
                            Text(
                                text = "${data.nombre} ${data.apellidoPaterno} ${data.apellidoMaterno}",
                                fontSize = 16.sp,
                                fontWeight = FontWeight.SemiBold,
                                color = InstitutionalBlue
                            )
                            Spacer(modifier = Modifier.height(4.dp))
                            Text(
                                text = "DNI: ${data.dni}",
                                fontSize = 14.sp,
                                color = NeutralMedium
                            )
                        }
                    }
                    Spacer(modifier = Modifier.height(16.dp))
                }

                // Dropdown de Regiones
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
                        leadingIcon = {
                            Icon(
                                imageVector = Icons.Default.LocationOn,
                                contentDescription = null
                            )
                        },
                        trailingIcon = {
                            if (state.isLoadingRegiones) {
                                CircularProgressIndicator(
                                    modifier = Modifier.size(20.dp),
                                    strokeWidth = 2.dp
                                )
                            } else {
                                ExposedDropdownMenuDefaults.TrailingIcon(expanded = expanded)
                            }
                        },
                        modifier = Modifier
                            .fillMaxWidth()
                            .menuAnchor(),
                        colors = OutlinedTextFieldDefaults.colors(
                            focusedBorderColor = InstitutionalBlue,
                            focusedLabelColor = InstitutionalBlue,
                            focusedLeadingIconColor = InstitutionalBlue
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
                                text = {
                                    Text(
                                        text = region.nombreRegion,
                                        color = NeutralDark
                                    )
                                },
                                onClick = {
                                    selectedRegion = region.id
                                    expanded = false
                                },
                                leadingIcon = {
                                    Icon(
                                        imageVector = Icons.Default.Place,
                                        contentDescription = null,
                                        tint = InstitutionalBlue
                                    )
                                }
                            )
                        }
                    }
                }

                Spacer(modifier = Modifier.height(16.dp))

                // Campo Password
                OutlinedTextField(
                    value = password,
                    onValueChange = { password = it },
                    label = { Text("Contraseña") },
                    leadingIcon = {
                        Icon(
                            imageVector = Icons.Default.Lock,
                            contentDescription = null
                        )
                    },
                    trailingIcon = {
                        IconButton(onClick = { passwordVisible = !passwordVisible }) {
                            Icon(
                                imageVector = if (passwordVisible) Icons.Default.Visibility else Icons.Default.VisibilityOff,
                                contentDescription = if (passwordVisible) "Ocultar" else "Mostrar"
                            )
                        }
                    },
                    visualTransformation = if (passwordVisible) VisualTransformation.None else PasswordVisualTransformation(),
                    modifier = Modifier.fillMaxWidth(),
                    singleLine = true,
                    keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Password),
                    colors = OutlinedTextFieldDefaults.colors(
                        focusedBorderColor = InstitutionalBlue,
                        focusedLabelColor = InstitutionalBlue,
                        focusedLeadingIconColor = InstitutionalBlue
                    ),
                    enabled = state.dniValidated != null && !state.isLoading,
                    supportingText = {
                        Text(
                            text = "Mínimo 8 caracteres",
                            fontSize = 12.sp,
                            color = NeutralMedium
                        )
                    }
                )

                Spacer(modifier = Modifier.height(8.dp))

                // Mensajes
                state.errorMessage?.let { error ->
                    ErrorMessage(error)
                    Spacer(modifier = Modifier.height(8.dp))
                }

                state.successMessage?.let { success ->
                    SuccessMessage(success)
                    Spacer(modifier = Modifier.height(8.dp))
                }

                Spacer(modifier = Modifier.height(16.dp))

                // Botón de Registro
                Button(
                    onClick = { showConfirmDialog = true },
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(56.dp),
                    colors = ButtonDefaults.buttonColors(
                        containerColor = InstitutionalBlue
                    ),
                    shape = RoundedCornerShape(12.dp),
                    enabled = !state.isLoading &&
                            state.dniValidated != null &&
                            selectedRegion != null &&
                            password.length >= 8
                ) {
                    if (state.isLoading) {
                        CircularProgressIndicator(
                            color = NeutralWhite,
                            modifier = Modifier.size(24.dp)
                        )
                    } else {
                        Text(
                            text = "Registrarse",
                            fontSize = 16.sp,
                            fontWeight = FontWeight.Bold,
                            color = NeutralWhite
                        )
                    }
                }

                Spacer(modifier = Modifier.height(20.dp))

                TextButton(
                    onClick = onNavigateToLogin,
                    enabled = !state.isLoading
                ) {
                    Text(
                        text = "¿Ya tienes una cuenta? Inicia sesión",
                        color = InstitutionalBlue,
                        fontSize = 14.sp
                    )
                }
            }
        }
    }

    // Diálogo de confirmación
    if (showConfirmDialog) {
        state.dniValidated?.let { data ->
            AlertDialog(
                onDismissRequest = { showConfirmDialog = false },
                title = {
                    Text(
                        text = "Confirma tus datos",
                        fontWeight = FontWeight.Bold
                    )
                },
                text = {
                    Column {
                        Text("¿Estos son tus datos correctos?")
                        Spacer(modifier = Modifier.height(12.dp))
                        Text(
                            text = "Nombre: ${data.nombre} ${data.apellidoPaterno} ${data.apellidoMaterno}",
                            fontWeight = FontWeight.SemiBold
                        )
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
                        colors = ButtonDefaults.buttonColors(
                            containerColor = InstitutionalBlue
                        )
                    ) {
                        Text("Sí, registrar", color = NeutralWhite)
                    }
                },
                dismissButton = {
                    TextButton(onClick = { showConfirmDialog = false }) {
                        Text("Cancelar", color = InstitutionalBlue)
                    }
                }
            )
        }
    }
}