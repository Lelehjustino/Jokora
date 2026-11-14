package com.example.jokora.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.jokora.data.repository.JogadorRepository
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.stateIn

data class TelaInicialUiState(
    val nomeJogador: String = "Visitante",
    val perfil: String = "Não identificado",
    val possuiJogador: Boolean = false
)

class TelaInicialViewModel(
    jogadorRepository: JogadorRepository
) : ViewModel() {

    val uiState = jogadorRepository.jogadorAtual
        .map { jogador ->
            TelaInicialUiState(
                nomeJogador = jogador?.nome ?: "Visitante",
                perfil = if (jogador != null) "Jogador" else "Não identificado",
                possuiJogador = jogador != null
            )
        }
        .stateIn(
            scope = viewModelScope,
            started = SharingStarted.WhileSubscribed(5000),
            initialValue = TelaInicialUiState()
        )
}