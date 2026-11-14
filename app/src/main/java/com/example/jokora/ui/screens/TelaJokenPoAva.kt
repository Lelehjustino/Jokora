package com.example.jokora.ui.screens

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
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
import com.example.jokora.viewmodel.TelaJokenPoAvaViewModel
import com.example.jokora.viewmodel.TelaJokenPoAvaViewModelFactory

/** Tela do Jokenpô avançado: Pedra, Papel, Tesoura, Lagarto e Spock contra o computador. */
@Composable
fun TelaJokenPoAva(
    nav: AcoesNavegacao,
    modifier: Modifier = Modifier
) {
    val context = LocalContext.current
    val viewModel: TelaJokenPoAvaViewModel = viewModel(
        factory = TelaJokenPoAvaViewModelFactory(context)
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
        Text(
            text = "Jokenpô avançado",
            style = MaterialTheme.typography.headlineMedium,
            fontWeight = FontWeight.Bold
        )
        Text(
            text = "Pedra, Papel, Tesoura, Lagarto e Spock contra o computador",
            style = MaterialTheme.typography.bodyMedium,
            color = MaterialTheme.colorScheme.onSurfaceVariant,
            textAlign = TextAlign.Center
        )

        PlacarJogo(
            vitoriasJogador = uiState.vitoriasJogador,
            empates = uiState.empates,
            vitoriasComputador = uiState.vitoriasComputador
        )

        Text(
            text = if (uiState.rodadaFinalizada) "Sua escolha" else "Faça sua escolha",
            style = MaterialTheme.typography.titleMedium,
            fontWeight = FontWeight.SemiBold
        )
        Column(
            modifier = Modifier.fillMaxWidth(),
            verticalArrangement = Arrangement.spacedBy(12.dp)
        ) {
            uiState.opcoes.chunked(BOTOES_POR_LINHA).forEach { linha ->
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(12.dp)
                ) {
                    val vagas = BOTOES_POR_LINHA - linha.size
                    if (vagas > 0) Spacer(Modifier.weight(vagas / 2f))

                    linha.forEach { jogada ->
                        BotaoJogada(
                            jogada = jogada,
                            selecionada = uiState.jogadaJogador == jogada,
                            habilitado = !uiState.rodadaFinalizada,
                            onClick = { viewModel.jogar(jogada) },
                            modifier = Modifier.weight(1f)
                        )
                    }

                    if (vagas > 0) Spacer(Modifier.weight(vagas / 2f))
                }
            }
        }

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

        Button(
            onClick = { viewModel.jogarNovamente() },
            enabled = uiState.rodadaFinalizada,
            modifier = Modifier.fillMaxWidth()
        ) {
            Text("Jogar novamente")
        }

        OutlinedButton(
            onClick = { viewModel.reiniciarPontuacao() },
            modifier = Modifier.fillMaxWidth()
        ) {
            Text("Reiniciar pontuação")
        }

        TextButton(
            onClick = {
                AbaRegras.proxima = AbaRegras.AVANCADO
                nav.navegar(Rota.Regras)
            }
        ) {
            Text("Ver as regras")
        }
    }
}
private const val BOTOES_POR_LINHA = 3