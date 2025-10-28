package com.rivera.votainformado

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.navigation.compose.rememberNavController
import com.rivera.votainformado.ui.navigation.NavGraph
import com.rivera.votainformado.ui.theme.VotaInformadoTheme

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContent {
            VotaInformadoTheme {
                // Aquí recordamos el controlador de navegación
                val navController = rememberNavController()

                // Y llamamos a nuestro NavGraph principal
                NavGraph(navController = navController)
            }
        }
    }
}