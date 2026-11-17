package com.example.jokora.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.jokora.data.repository.FraseRepository
import com.example.jokora.data.repository.JogadorRepository
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch

data class TelaInicialUiState(
    val nomeJogador: String = "Visitante",
    val perfil: String = "Não identificado",
    val possuiJogador: Boolean = false,
    val isAdmin: Boolean = false,
    // Frase do dia (API externa via Retrofit)
    val frase: String? = null,
    val carregandoFrase: Boolean = false,
    val erroFrase: String? = null
)

class TelaInicialViewModel(
    jogadorRepository: JogadorRepository,
    private val fraseRepository: FraseRepository
) : ViewModel() {

    /** Estado interno da consulta à API. */
    private data class EstadoFrase(
        val texto: String? = null,
        val carregando: Boolean = false,
        val erro: String? = null
    )

    private val estadoFrase = MutableStateFlow(EstadoFrase())

    val uiState: StateFlow<TelaInicialUiState> = combine(
        jogadorRepository.jogadorAtual, estadoFrase
    ) { jogador, frase ->
        TelaInicialUiState(
            nomeJogador = jogador?.nome ?: "Visitante",
            perfil = jogador?.perfil?.rotulo ?: "Não identificado",
            possuiJogador = jogador != null,
            isAdmin = jogador?.perfil?.podeAcessarAdmin == true,
            frase = frase.texto,
            carregandoFrase = frase.carregando,
            erroFrase = frase.erro
        )
    }.stateIn(
        scope = viewModelScope,
        started = SharingStarted.WhileSubscribed(5000),
        initialValue = TelaInicialUiState()
    )

    init {
        carregarFrase()
    }

    /** Busca uma nova frase na API. Falhas chegam como Result.failure (tratadas no Repository). */
    fun carregarFrase() {
        viewModelScope.launch {
            estadoFrase.update { it.copy(carregando = true, erro = null) }
            fraseRepository.buscarFraseDoDia()
                .onSuccess { texto ->
                    estadoFrase.update { it.copy(texto = texto, carregando = false) }
                }
                .onFailure { erro ->
                    estadoFrase.update { it.copy(carregando = false, erro = erro.message) }
                }
        }
    }
}
