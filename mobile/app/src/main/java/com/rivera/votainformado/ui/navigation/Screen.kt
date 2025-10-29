package com.rivera.votainformado.ui.navigation

sealed class Screen(val route: String) {
    object Splash : Screen("splash")
    object Login : Screen("login")
    object Register : Screen("register")
    object Welcome : Screen("welcome")
    object Home : Screen("home")
}