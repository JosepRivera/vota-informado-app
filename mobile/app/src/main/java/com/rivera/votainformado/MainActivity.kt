package com.rivera.votainformado

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.core.splashscreen.SplashScreen.Companion.installSplashScreen
import androidx.navigation.compose.rememberNavController
import com.rivera.votainformado.ui.navigation.NavGraph
import com.rivera.votainformado.ui.theme.VotaInformadoTheme
import com.rivera.votainformado.util.RetrofitInstance

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        
        // Inicializar RetrofitInstance con el contexto de la aplicación
        RetrofitInstance.init(this)
        
        enableEdgeToEdge()
        setContent {
            VotaInformadoTheme {
                val splashScreen = installSplashScreen()

                val navController = rememberNavController()
                NavGraph(navController = navController)
            }
        }
    }
}