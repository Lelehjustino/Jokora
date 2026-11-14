package com.example.jokora.navigation

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.navigation.NavHostController
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import com.example.jokora.ui.screens.TelaInicial
import com.example.jokora.ui.screens.TelaJokenPo
import com.example.jokora.ui.screens.TelaJokenPoAva
import com.example.jokora.ui.screens.TelaRegras
import com.example.jokora.ui.screens.TelaPerfil

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

    val historico: @Composable (AcoesNavegacao) -> Unit = { nav ->
        TelaPerfil(nav = nav)
    }
)

/** Raiz do app: Scaffold + Bottom Navigation + NavHost. Chame em MainActivity.setContent. */
@Composable
fun JokoraApp(
    telas: TelasDoApp = TelasDoApp(),
    navController: NavHostController = rememberNavController()
) {
    Scaffold(
        bottomBar = { BarraNavegacaoInferior(navController) }
    ) { paddingInterno ->
        JokenpoNavGraph(
            navController = navController,
            telas = telas,
            modifier = Modifier.padding(paddingInterno)
        )
    }
}

@Composable
fun JokenpoNavGraph(
    navController: NavHostController,
    telas: TelasDoApp,
    modifier: Modifier = Modifier
) {
    val acoes = remember(navController) {
        AcoesNavegacao(
            // Todas as telas são abas da barra inferior, então qualquer navegação
            // (pela barra ou por um botão dentro da tela) usa a mesma regra.
            // Misturar as duas formas bagunçava as pilhas salvas de cada aba.
            navegar = { rota -> navController.navegarPelaBarra(rota) },
            // navigateUp não remove a tela inicial (popBackStack deixaria a tela em branco).
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
        composable(Rota.Historico.route) { telas.historico(acoes) }
    }
}

/** Aviso neutro exibido enquanto a tela do integrante ainda não foi conectada. */
@Composable
fun TelaEmConstrucao(rota: Rota) {
    Box(Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
        Text("${rota.titulo}: tela ainda não integrada")
    }
}
