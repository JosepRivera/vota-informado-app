package com.rivera.votainformado

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.core.splashscreen.SplashScreen.Companion.installSplashScreen
import androidx.navigation.compose.rememberNavController
import com.rivera.votainformado.ui.navigation.NavGraph
import com.rivera.votainformado.ui.theme.VotaInformadoTheme

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
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