package com.example.jokora.ui.screens

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.Button
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.lifecycle.viewmodel.compose.viewModel
import com.example.jokora.navigation.AcoesNavegacao
import com.example.jokora.navigation.Rota
import com.example.jokora.ui.components.BotaoJogada
import com.example.jokora.ui.components.PlacarJogo
import com.example.jokora.ui.components.ResultadoRodada
import com.example.jokora.viewmodel.TelaJokenPoViewModel
import com.example.jokora.viewmodel.TelaJokenPoViewModelFactory

/** Tela do Jokenpô tradicional: Pedra, Papel e Tesoura contra o computador. */
@Composable
fun TelaJokenPo(
    nav: AcoesNavegacao,
    modifier: Modifier = Modifier
) {
    val context = LocalContext.current
    val viewModel: TelaJokenPoViewModel = viewModel(
        factory = TelaJokenPoViewModelFactory(context)
    )
    val uiState by viewModel.uiState.collectAsStateWithLifecycle()

    Column(
        modifier = modifier
            .fillMaxSize()
            .verticalScroll(rememberScrollState())
            .padding(horizontal = 20.dp, vertical = 16.dp),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.spacedBy(16.dp)
    ) {
        // 1. Título
        Text(
            text = "Jokenpô tradicional",
            style = MaterialTheme.typography.headlineMedium,
            fontWeight = FontWeight.Bold
        )
        Text(
            text = "Pedra, Papel e Tesoura contra o computador",
            style = MaterialTheme.typography.bodyMedium,
            color = MaterialTheme.colorScheme.onSurfaceVariant,
            textAlign = TextAlign.Center
        )

        // 2. Placar
        PlacarJogo(
            vitoriasJogador = uiState.vitoriasJogador,
            empates = uiState.empates,
            vitoriasComputador = uiState.vitoriasComputador
        )

        // 3 e 4. Área de escolha com os três botões grandes
        Text(
            text = if (uiState.rodadaFinalizada) "Sua escolha" else "Faça sua escolha",
            style = MaterialTheme.typography.titleMedium,
            fontWeight = FontWeight.SemiBold
        )
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.spacedBy(12.dp)
        ) {
            uiState.opcoes.forEach { jogada ->
                BotaoJogada(
                    jogada = jogada,
                    selecionada = uiState.jogadaJogador == jogada,
                    habilitado = !uiState.rodadaFinalizada,
                    onClick = { viewModel.jogar(jogada) },
                    modifier = Modifier.weight(1f)
                )
            }
        }

        // 5 e 6. Escolha do computador e resultado da rodada
        val jogadaJogador = uiState.jogadaJogador
        val jogadaComputador = uiState.jogadaComputador
        val resultado = uiState.resultado

        if (jogadaJogador != null && jogadaComputador != null && resultado != null) {
            ResultadoRodada(
                jogadaJogador = jogadaJogador,
                jogadaComputador = jogadaComputador,
                resultado = resultado,
                descricao = uiState.descricao
            )
        } else {
            Text(
                text = "O computador está esperando a sua jogada...",
                style = MaterialTheme.typography.bodyMedium,
                color = MaterialTheme.colorScheme.onSurfaceVariant
            )
        }

        uiState.erro?.let { mensagem ->
            Text(
                text = mensagem,
                color = MaterialTheme.colorScheme.error,
                style = MaterialTheme.typography.bodySmall,
                textAlign = TextAlign.Center
            )
        }

        // 7. Jogar novamente
        Button(
            onClick = { viewModel.jogarNovamente() },
            enabled = uiState.rodadaFinalizada,
            modifier = Modifier.fillMaxWidth()
        ) {
            Text("Jogar novamente")
        }

        // 8. Reiniciar pontuação
        OutlinedButton(
            onClick = { viewModel.reiniciarPontuacao() },
            modifier = Modifier.fillMaxWidth()
        ) {
            Text("Reiniciar pontuação")
        }

        TextButton(onClick = { nav.navegar(Rota.Regras) }) {
            Text("Ver as regras")
        }
    }
}
