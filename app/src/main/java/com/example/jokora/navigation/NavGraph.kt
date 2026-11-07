package com.example.jokora.navigation

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.navigation.NavHostController
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import com.example.jokora.data.database.DatabaseProvider
import com.example.jokora.model.Perfil

/** Ações de navegação entregues a cada tela. As telas não recebem o NavController. */
class AcoesNavegacao(
    val navegar: (Rota) -> Unit,
    val voltar: () -> Unit
)

/**
 * Ponto de encaixe das telas dos integrantes.
 * Enquanto a tela não existir, aparece o aviso neutro [TelaEmConstrucao].
 *
 * Para integrar sua tela (criada em ui/screens), troque o valor padrão do slot correspondente, ex.:
 *   val inicial: @Composable (AcoesNavegacao) -> Unit = { nav -> TelaInicial(nav) },
 */
class TelasDoApp(
    val inicial: @Composable (AcoesNavegacao) -> Unit = { TelaEmConstrucao(Rota.Inicial) },
    val regras: @Composable (AcoesNavegacao) -> Unit = { TelaEmConstrucao(Rota.Regras) },
    val jokenpo: @Composable (AcoesNavegacao) -> Unit = { TelaEmConstrucao(Rota.Jokenpo) },
    val jokenpoAvancado: @Composable (AcoesNavegacao) -> Unit = { TelaEmConstrucao(Rota.JokenpoAvancado) },
    val historico: @Composable (AcoesNavegacao) -> Unit = { TelaEmConstrucao(Rota.Historico) },
    val admin: @Composable (AcoesNavegacao) -> Unit = { TelaEmConstrucao(Rota.Admin) }
)

/** Raiz do app: Scaffold + Bottom Navigation + NavHost. Chame em MainActivity.setContent. */
@Composable
fun JokoraApp(
    telas: TelasDoApp = TelasDoApp(),
    navController: NavHostController = rememberNavController()
) {
    val context = LocalContext.current
    val jogadorRepository = remember { DatabaseProvider.jogadorRepository(context) }
    val jogadorAtual by jogadorRepository.jogadorAtual.collectAsState()
    val perfil = jogadorAtual?.perfil ?: Perfil.JOGADOR

    Scaffold(
        bottomBar = { BarraNavegacaoInferior(navController, perfil) }
    ) { paddingInterno ->
        JokenpoNavGraph(
            navController = navController,
            perfil = perfil,
            telas = telas,
            modifier = Modifier.padding(paddingInterno)
        )
    }
}

@Composable
fun JokenpoNavGraph(
    navController: NavHostController,
    perfil: Perfil,
    telas: TelasDoApp,
    modifier: Modifier = Modifier
) {
    val acoes = remember(navController, perfil) {
        AcoesNavegacao(
            navegar = { rota -> navController.navegarPara(rota, perfil) },
            voltar = { navController.popBackStack() }
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
        composable(Rota.Historico.route) { telas.historico(acoes) }
        composable(Rota.Admin.route) {
            if (Rota.Admin.permitidaPara(perfil)) {
                telas.admin(acoes)
            } else {
                // Proteção extra: mesmo que alguém navegue direto para a rota, volta ao início.
                LaunchedEffect(Unit) {
                    navController.navigate(Rota.Inicial.route) {
                        popUpTo(Rota.Admin.route) { inclusive = true }
                        launchSingleTop = true
                    }
                }
            }
        }
    }
}

/** Aviso neutro exibido enquanto a tela do integrante ainda não foi conectada. */
@Composable
fun TelaEmConstrucao(rota: Rota) {
    Box(Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
        Text("${rota.titulo}: tela ainda não integrada")
    }
}
