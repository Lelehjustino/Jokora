package com.example.jokora.viewmodel

import androidx.lifecycle.ViewModel
import com.example.jokora.model.RegraJogo

data class TelaRegrasUiState(
    val regrasTradicionais: List<RegraJogo> = emptyList(),
    val regrasAvancadas: List<RegraJogo> = emptyList()
)

class TelaRegrasViewModel : ViewModel() {

    val uiState = TelaRegrasUiState(

        regrasTradicionais = listOf(
            RegraJogo(
                jogada = "Pedra",
                venceDe = listOf("Tesoura"),
                explicacao = "A pedra quebra a tesoura.",
                emoji = "🪨"
            ),
            RegraJogo(
                jogada = "Papel",
                venceDe = listOf("Pedra"),
                explicacao = "O papel cobre a pedra.",
                emoji = "📄"
            ),
            RegraJogo(
                jogada = "Tesoura",
                venceDe = listOf("Papel"),
                explicacao = "A tesoura corta o papel.",
                emoji = "✂️"
            )
        ),

        regrasAvancadas = listOf(
            RegraJogo(
                jogada = "Pedra",
                venceDe = listOf("Tesoura", "Lagarto"),
                explicacao = "A pedra esmaga a tesoura e o lagarto.",
                emoji = "🪨"
            ),
            RegraJogo(
                jogada = "Papel",
                venceDe = listOf("Pedra", "Spock"),
                explicacao = "O papel cobre a pedra e refuta Spock.",
                emoji = "📄"
            ),
            RegraJogo(
                jogada = "Tesoura",
                venceDe = listOf("Papel", "Lagarto"),
                explicacao = "A tesoura corta o papel e decapita o lagarto.",
                emoji = "✂️"
            ),
            RegraJogo(
                jogada = "Lagarto",
                venceDe = listOf("Spock", "Papel"),
                explicacao = "O lagarto envenena Spock e come o papel.",
                emoji = "🦎"
            ),
            RegraJogo(
                jogada = "Spock",
                venceDe = listOf("Tesoura", "Pedra"),
                explicacao = "Spock quebra a tesoura e vaporiza a pedra.",
                emoji = "🖖"
            )
        )
    )
}