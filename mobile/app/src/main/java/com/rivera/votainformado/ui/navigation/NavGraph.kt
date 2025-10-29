package com.rivera.votainformado.ui.navigation

import androidx.compose.runtime.Composable
import androidx.navigation.NavHostController
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import com.rivera.votainformado.ui.HomeScreen
import com.rivera.votainformado.ui.auth.login.LoginScreen
import com.rivera.votainformado.ui.auth.register.RegisterScreen
import com.rivera.votainformado.ui.welcome.WelcomeScreen

@Composable
fun NavGraph(navController: NavHostController) {
    NavHost(
        navController = navController,
        startDestination = Screen.Welcome.route
    ) {
        composable(Screen.Welcome.route) {
            WelcomeScreen(
                onNavigateToHome = {
                    navController.navigate(Screen.Home.route) {
                        popUpTo(Screen.Welcome.route) { inclusive = true }
                    }
                },
                onNavigateToLogin = { navController.navigate(Screen.Login.route) },
                onNavigateToRegister = { navController.navigate(Screen.Register.route) }
            )
        }

        composable(Screen.Login.route) {
            LoginScreen(
                onNavigateToHome = {
                    navController.navigate(Screen.Home.route) {
                        // Limpia el backstack (para que no regrese a login/register)
                        popUpTo(Screen.Welcome.route) { inclusive = true }
                    }
                },
                onNavigateToRegister = { navController.navigate(Screen.Register.route) },
                onBack = { navController.popBackStack() }
            )
        }

        composable(Screen.Register.route) {
            RegisterScreen(
                onNavigateToHome = {
                    navController.navigate(Screen.Home.route) {
                        popUpTo(Screen.Welcome.route) { inclusive = true }
                    }
                },
                onNavigateToLogin = { navController.navigate(Screen.Login.route) },
                onBack = { navController.popBackStack() } // 🔙 vuelve a Welcome
            )
        }

        composable(Screen.Home.route) {
            HomeScreen()
        }
    }
}
