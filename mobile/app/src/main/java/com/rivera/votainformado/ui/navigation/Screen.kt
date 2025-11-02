package com.rivera.votainformado.ui.navigation

sealed class Screen(val route: String) {
    object Splash : Screen("splash")
    object Login : Screen("login")
    object Register : Screen("register")
    object Welcome : Screen("welcome")
    object Home : Screen("home")
    
    // Pantallas de candidatos
    object CandidatoDetail : Screen("candidato_detail/{id}") {
        fun createRoute(id: Int) = "candidato_detail/$id"
    }
    
    // Pantallas de votación
    object Resultados : Screen("resultados")
    object Votar : Screen("votar")
    object Comparar : Screen("comparar")
    object Perfil : Screen("perfil")
}