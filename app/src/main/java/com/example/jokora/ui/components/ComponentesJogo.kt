package com.example.jokora.ui.components

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedCard
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.jokora.model.Jogada
import com.example.jokora.model.Resultado

/**
 * Componentes visuais das telas de jogo.
 * Servem tanto para o Jokenpô tradicional quanto para o avançado.
 */

/** Emoji que representa cada jogada. */
fun Jogada.emoji(): String = when (this) {
    Jogada.PEDRA -> "✊"
    Jogada.PAPEL -> "✋"
    Jogada.TESOURA -> "✌️"
    Jogada.LAGARTO -> "🦎"
    Jogada.SPOCK -> "🖖"
}

/** Botão grande de escolha de jogada (emoji + nome). */
@Composable
fun BotaoJogada(
    jogada: Jogada,
    selecionada: Boolean,
    habilitado: Boolean,
    onClick: () -> Unit,
    modifier: Modifier = Modifier
) {
    OutlinedCard(
        onClick = onClick,
        enabled = habilitado,
        modifier = modifier,
        border = BorderStroke(
            width = if (selecionada) 3.dp else 1.dp,
            color = if (selecionada) MaterialTheme.colorScheme.primary
            else MaterialTheme.colorScheme.outlineVariant
        )
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(vertical = 18.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Text(text = jogada.emoji(), fontSize = 40.sp)
            Text(
                text = jogada.rotulo,
                style = MaterialTheme.typography.titleSmall,
                fontWeight = FontWeight.SemiBold
            )
        }
    }
}

/** Placar com três números: vitórias do jogador, empates e vitórias do computador. */
@Composable
fun PlacarJogo(
    vitoriasJogador: Int,
    empates: Int,
    vitoriasComputador: Int,
    modifier: Modifier = Modifier
) {
    Card(
        modifier = modifier.fillMaxWidth(),
        colors = CardDefaults.cardColors(
            containerColor = MaterialTheme.colorScheme.surfaceVariant
        )
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(vertical = 14.dp),
            horizontalArrangement = Arrangement.SpaceEvenly
        ) {
            ItemPlacar(valor = vitoriasJogador, legenda = "Você")
            ItemPlacar(valor = empates, legenda = "Empates")
            ItemPlacar(valor = vitoriasComputador, legenda = "Computador")
        }
    }
}

@Composable
private fun ItemPlacar(valor: Int, legenda: String) {
    Column(horizontalAlignment = Alignment.CenterHorizontally) {
        Text(
            text = valor.toString(),
            style = MaterialTheme.typography.headlineMedium,
            fontWeight = FontWeight.Bold
        )
        Text(text = legenda, style = MaterialTheme.typography.bodySmall)
    }
}

/** Card "Resultado da rodada": as duas jogadas lado a lado e o selo do resultado. */
@Composable
fun ResultadoRodada(
    jogadaJogador: Jogada,
    jogadaComputador: Jogada,
    resultado: Resultado,
    descricao: String,
    modifier: Modifier = Modifier
) {
    Card(
        modifier = modifier.fillMaxWidth(),
        elevation = CardDefaults.cardElevation(defaultElevation = 2.dp)
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.spacedBy(12.dp)
        ) {
            Text(
                text = "Resultado da rodada",
                style = MaterialTheme.typography.titleMedium,
                fontWeight = FontWeight.Bold
            )

            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceEvenly
            ) {
                LadoDaJogada(titulo = "Você", jogada = jogadaJogador)
                LadoDaJogada(titulo = "Computador", jogada = jogadaComputador)
            }

            SeloResultado(resultado)

            if (descricao.isNotBlank()) {
                Text(
                    text = descricao,
                    style = MaterialTheme.typography.bodyMedium,
                    textAlign = TextAlign.Center
                )
            }
        }
    }
}

@Composable
private fun LadoDaJogada(titulo: String, jogada: Jogada) {
    Column(horizontalAlignment = Alignment.CenterHorizontally) {
        Text(text = jogada.emoji(), fontSize = 44.sp)
        Text(text = titulo, fontWeight = FontWeight.Bold)
        Text(
            text = jogada.rotulo,
            style = MaterialTheme.typography.bodySmall,
            color = MaterialTheme.colorScheme.onSurfaceVariant
        )
    }
}

/** Selo colorido: "Você venceu!", "Você perdeu!" ou "Empate!". */
@Composable
private fun SeloResultado(resultado: Resultado) {
    val (texto, fundo: Color, cor: Color) = when (resultado) {
        Resultado.VITORIA -> Triple(
            "Você venceu!",
            MaterialTheme.colorScheme.primaryContainer,
            MaterialTheme.colorScheme.onPrimaryContainer
        )
        Resultado.DERROTA -> Triple(
            "Você perdeu!",
            MaterialTheme.colorScheme.errorContainer,
            MaterialTheme.colorScheme.onErrorContainer
        )
        Resultado.EMPATE -> Triple(
            "Empate!",
            MaterialTheme.colorScheme.secondaryContainer,
            MaterialTheme.colorScheme.onSecondaryContainer
        )
    }

    Text(
        text = texto,
        color = cor,
        fontWeight = FontWeight.Bold,
        modifier = Modifier
            .clip(RoundedCornerShape(50))
            .background(fundo)
            .padding(horizontal = 14.dp, vertical = 6.dp)
    )
}
