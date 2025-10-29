package com.rivera.votainformado.ui.navigation

import androidx.compose.animation.*
import androidx.compose.animation.core.*
import androidx.compose.runtime.Composable
import androidx.navigation.NavHostController
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import com.rivera.votainformado.ui.HomeScreen
import com.rivera.votainformado.ui.auth.login.LoginScreen
import com.rivera.votainformado.ui.auth.register.RegisterScreen
import com.rivera.votainformado.ui.splash.SplashScreen
import com.rivera.votainformado.ui.welcome.WelcomeScreen

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
            HomeScreen()
        }
    }
}