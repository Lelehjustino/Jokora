package com.example.jokora.navigation

import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Home
import androidx.compose.material.icons.filled.Info
import androidx.compose.material.icons.filled.List
import androidx.compose.material.icons.filled.PlayArrow
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
import com.example.jokora.model.Perfil

/** Barra de navegação inferior padrão (Material 3), sem estilização própria. */
@Composable
fun BarraNavegacaoInferior(
    navController: NavHostController,
    perfil: Perfil
) {
    val entradaAtual by navController.currentBackStackEntryAsState()
    val destinoAtual = entradaAtual?.destination

    NavigationBar {
        Rota.itensBarraInferior(perfil).forEach { rota ->
            NavigationBarItem(
                selected = destinoAtual?.hierarchy?.any { it.route == rota.route } == true,
                onClick = { navController.navegarPelaBarra(rota, perfil) },
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
    Rota.Historico -> Icons.Filled.List
    Rota.Admin -> Icons.Filled.Settings
}

/** Navegação de abas: não empilha telas repetidas e preserva o estado de cada aba. */
fun NavHostController.navegarPelaBarra(rota: Rota, perfil: Perfil) {
    if (!rota.permitidaPara(perfil)) return
    navigate(rota.route) {
        popUpTo(graph.findStartDestination().id) { saveState = true }
        launchSingleTop = true
        restoreState = true
    }
}

/** Navegação comum entre telas (ex.: botão "Ver regras" na TelaInicial). */
fun NavHostController.navegarPara(rota: Rota, perfil: Perfil) {
    if (!rota.permitidaPara(perfil)) return
    navigate(rota.route) { launchSingleTop = true }
}
