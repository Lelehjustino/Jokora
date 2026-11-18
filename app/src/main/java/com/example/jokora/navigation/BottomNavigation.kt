package com.example.jokora.navigation

import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Home
import androidx.compose.material.icons.filled.Info
import androidx.compose.material.icons.filled.PlayArrow
import androidx.compose.material.icons.filled.Person
import androidx.compose.material.icons.filled.Settings
import androidx.compose.material.icons.filled.Star
import androidx.compose.material3.Icon
import androidx.compose.material3.NavigationBar
import androidx.compose.material3.NavigationBarItem
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.navigation.NavDestination.Companion.hierarchy
import androidx.navigation.NavGraph.Companion.findStartDestination
import androidx.navigation.NavHostController
import androidx.navigation.compose.currentBackStackEntryAsState

@Composable
fun BarraNavegacaoInferior(
    navController: NavHostController,
    abas: List<Rota>
) {
    val entradaAtual by navController.currentBackStackEntryAsState()
    val destinoAtual = entradaAtual?.destination

    NavigationBar {
        abas.forEach { rota ->
            NavigationBarItem(
                selected = destinoAtual?.hierarchy?.any { it.route == rota.route } == true,
                onClick = { navController.navegarPelaBarra(rota) },
                icon = { Icon(iconeDa(rota), contentDescription = rota.titulo) },
                label = { Text(rota.titulo) }
            )
        }
    }
}

private fun iconeDa(rota: Rota): ImageVector = when (rota) {
    Rota.Inicial -> Icons.Filled.Home
    Rota.Regras -> Icons.Filled.Info
    Rota.Jokenpo -> Icons.Filled.PlayArrow
    Rota.JokenpoAvancado -> Icons.Filled.Star
    Rota.Perfil -> Icons.Filled.Person
    Rota.Admin -> Icons.Filled.Settings
}

fun NavHostController.navegarPelaBarra(rota: Rota) {
    navigate(rota.route) {
        popUpTo(graph.findStartDestination().id) { saveState = true }
        launchSingleTop = true
        restoreState = true
    }
}

fun NavHostController.navegarPara(rota: Rota) {
    navigate(rota.route) { launchSingleTop = true }
}
