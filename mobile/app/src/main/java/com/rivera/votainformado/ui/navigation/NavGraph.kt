package com.rivera.votainformado.ui.navigation

import androidx.compose.animation.*
import androidx.compose.animation.core.*
import androidx.compose.runtime.Composable
import androidx.navigation.NavHostController
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import com.rivera.votainformado.ui.home.HomeScreen
import com.rivera.votainformado.ui.auth.login.LoginScreen
import com.rivera.votainformado.ui.auth.register.RegisterScreen
import com.rivera.votainformado.ui.splash.SplashScreen
import com.rivera.votainformado.ui.welcome.WelcomeScreen
import com.rivera.votainformado.ui.candidatos.CandidatoDetailScreen
import com.rivera.votainformado.ui.perfil.PerfilScreen
import com.rivera.votainformado.ui.votar.VotarScreen
import com.rivera.votainformado.ui.resultados.ResultadosScreen
import com.rivera.votainformado.ui.comparar.CompararScreen

// Animaciones Scale + Fade
private fun scaleEnter() = scaleIn(
    initialScale = 0.9f,
    animationSpec = tween(400, easing = FastOutSlowInEasing)
) + fadeIn(animationSpec = tween(400))

private fun scaleExit() = scaleOut(
    targetScale = 1.1f,
    animationSpec = tween(400, easing = FastOutSlowInEasing)
) + fadeOut(animationSpec = tween(400))

private fun fadeExit() = fadeOut(
    animationSpec = tween(300, easing = FastOutSlowInEasing)
)

@Composable
fun NavGraph(navController: NavHostController) {
    NavHost(
        navController = navController,
        startDestination = Screen.Splash.route,
        enterTransition = { scaleEnter() },
        exitTransition = { scaleExit() },
        popEnterTransition = { scaleEnter() },
        popExitTransition = { scaleExit() }
    ) {
        composable(
            route = Screen.Splash.route,
            enterTransition = { EnterTransition.None },
            exitTransition = { fadeExit() }
        ) {
            SplashScreen(onTimeout = {
                navController.navigate(Screen.Welcome.route) {
                    popUpTo(Screen.Splash.route) { inclusive = true }
                }
            })
        }

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
                        popUpTo(Screen.Welcome.route) { inclusive = true }
                    }
                },
                onNavigateToRegister = {
                    navController.navigate(Screen.Register.route) {
                        popUpTo(Screen.Login.route) { inclusive = true }
                    }
                },
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
                onNavigateToLogin = {
                    navController.navigate(Screen.Login.route) {
                        popUpTo(Screen.Register.route) { inclusive = true }
                    }
                },
                onBack = { navController.popBackStack() }
            )
        }

        composable(Screen.Home.route) {
            HomeScreen(
                onProfileClick = {
                    navController.navigate(Screen.Perfil.route)
                },
                onNavigate = { route ->
                    when (route) {
                        "home" -> {
                            // Ya estamos en home, no hacer nada
                        }
                        "resultados" -> navController.navigate(Screen.Resultados.route) {
                            popUpTo(Screen.Home.route) { inclusive = true }
                        }
                        "votar" -> navController.navigate(Screen.Votar.route) {
                            popUpTo(Screen.Home.route) { inclusive = true }
                        }
                        "comparar" -> navController.navigate(Screen.Comparar.route) {
                            popUpTo(Screen.Home.route) { inclusive = true }
                        }
                    }
                },
                onNavigateToDetail = { id ->
                    navController.navigate(Screen.CandidatoDetail.createRoute(id))
                }
            )
        }
        
        composable(
            route = Screen.CandidatoDetail.route
        ) { backStackEntry ->
            val candidateId = backStackEntry.arguments?.getString("id")?.toIntOrNull() ?: 0
            CandidatoDetailScreen(
                candidateId = candidateId,
                onBack = { navController.popBackStack() }
            )
        }

        composable(Screen.Perfil.route) {
            PerfilScreen(
                onBack = { navController.popBackStack() },
                onNavigateToDetail = { id ->
                    navController.navigate(Screen.CandidatoDetail.createRoute(id))
                },
                onLogout = {
                    navController.navigate(Screen.Welcome.route) {
                        popUpTo(0) { inclusive = true }
                    }
                },
                onNavigateToWelcome = {
                    navController.navigate(Screen.Welcome.route) {
                        popUpTo(0) { inclusive = true }
                    }
                }
            )
        }

        composable(Screen.Votar.route) {
            VotarScreen(
                onNavigate = { route ->
                    when (route) {
                        "home" -> navController.navigate(Screen.Home.route) {
                            popUpTo(Screen.Votar.route) { inclusive = true }
                        }
                        "resultados" -> navController.navigate(Screen.Resultados.route) {
                            popUpTo(Screen.Votar.route) { inclusive = true }
                        }
                        "comparar" -> navController.navigate(Screen.Comparar.route) {
                            popUpTo(Screen.Votar.route) { inclusive = true }
                        }
                    }
                }
            )
        }

        composable(Screen.Resultados.route) {
            ResultadosScreen(
                onNavigate = { route ->
                    when (route) {
                        "home" -> navController.navigate(Screen.Home.route) {
                            popUpTo(Screen.Resultados.route) { inclusive = true }
                        }
                        "votar" -> navController.navigate(Screen.Votar.route) {
                            popUpTo(Screen.Resultados.route) { inclusive = true }
                        }
                        "comparar" -> navController.navigate(Screen.Comparar.route) {
                            popUpTo(Screen.Resultados.route) { inclusive = true }
                        }
                    }
                }
            )
        }

        composable(Screen.Comparar.route) {
            CompararScreen(
                onNavigate = { route ->
                    when (route) {
                        "home" -> navController.navigate(Screen.Home.route) {
                            popUpTo(Screen.Comparar.route) { inclusive = true }
                        }
                        "resultados" -> navController.navigate(Screen.Resultados.route) {
                            popUpTo(Screen.Comparar.route) { inclusive = true }
                        }
                        "votar" -> navController.navigate(Screen.Votar.route) {
                            popUpTo(Screen.Comparar.route) { inclusive = true }
                        }
                    }
                },
                onNavigateToDetail = { id ->
                    navController.navigate(Screen.CandidatoDetail.createRoute(id))
                }
            )
        }
    }
}