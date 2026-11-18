package com.example.jokora.navigation

import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Scaffold
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavHostController
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import com.example.jokora.ui.screens.TelaAdmin
import com.example.jokora.ui.screens.TelaInicial
import com.example.jokora.ui.screens.TelaJokenPo
import com.example.jokora.ui.screens.TelaJokenPoAva
import com.example.jokora.ui.screens.TelaPerfil
import com.example.jokora.ui.screens.TelaRegras
import com.example.jokora.viewmodel.SessaoViewModel
import com.example.jokora.viewmodel.SessaoViewModelFactory

class AcoesNavegacao(
    val navegar: (Rota) -> Unit,
    val voltar: () -> Unit
)

class TelasDoApp(
    val inicial: @Composable (AcoesNavegacao) -> Unit = { nav ->
        TelaInicial(nav = nav)
    },

    val regras: @Composable (AcoesNavegacao) -> Unit = { nav ->
        TelaRegras(nav = nav)
    },

    val jokenpo: @Composable (AcoesNavegacao) -> Unit = { nav ->
        TelaJokenPo(nav = nav)
    },

    val jokenpoAvancado: @Composable (AcoesNavegacao) -> Unit = { nav ->
        TelaJokenPoAva(nav = nav)
    },

    val perfil: @Composable (AcoesNavegacao) -> Unit = { nav ->
        TelaPerfil(nav = nav)
    },

    val admin: @Composable (AcoesNavegacao) -> Unit = { nav ->
        TelaAdmin(nav = nav)
    }
)

@Composable
fun JokoraApp(
    telas: TelasDoApp = TelasDoApp(),
    navController: NavHostController = rememberNavController()
) {
    val context = LocalContext.current
    val sessaoViewModel: SessaoViewModel = viewModel(
        factory = SessaoViewModelFactory(context)
    )
    val sessao by sessaoViewModel.uiState.collectAsStateWithLifecycle()

    Scaffold(
        bottomBar = { BarraNavegacaoInferior(navController, sessao.abasVisiveis) }
    ) { paddingInterno ->
        JokenpoNavGraph(
            navController = navController,
            telas = telas,
            isAdmin = sessao.isAdmin,
            modifier = Modifier.padding(paddingInterno)
        )
    }
}

@Composable
fun JokenpoNavGraph(
    navController: NavHostController,
    telas: TelasDoApp,
    isAdmin: Boolean,
    modifier: Modifier = Modifier
) {
    val acoes = remember(navController) {
        AcoesNavegacao(
            navegar = { rota -> navController.navegarPelaBarra(rota) },
            voltar = { navController.navigateUp() }
        )
    }

    NavHost(
        navController = navController,
        startDestination = Rota.inicial.route,
        modifier = modifier
    ) {
        composable(Rota.Inicial.route) { telas.inicial(acoes) }
        composable(Rota.Regras.route) { telas.regras(acoes) }
        composable(Rota.Jokenpo.route) { telas.jokenpo(acoes) }
        composable(Rota.JokenpoAvancado.route) { telas.jokenpoAvancado(acoes) }
        composable(Rota.Perfil.route) { telas.perfil(acoes) }
        composable(Rota.Admin.route) {
            if (isAdmin) {
                telas.admin(acoes)
            } else {
                LaunchedEffect(Unit) {
                    navController.navigate(Rota.Perfil.route) {
                        popUpTo(Rota.Admin.route) { inclusive = true }
                        launchSingleTop = true
                    }
                }
            }
        }
    }
}
