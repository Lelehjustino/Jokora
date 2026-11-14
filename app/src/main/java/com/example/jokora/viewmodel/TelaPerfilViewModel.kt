package com.example.jokora.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.jokora.data.entity.PartidaEntity
import com.example.jokora.data.repository.JogadorRepository
import com.example.jokora.data.repository.PartidaRepository
import com.example.jokora.data.repository.Placar
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.flatMapLatest
import kotlinx.coroutines.flow.flowOf
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch

data class TelaPerfilUiState(
    val possuiPerfil: Boolean = false,
    val nome: String = "",
    val placar: Placar = Placar(),
    val partidas: List<PartidaEntity> = emptyList(),
    val mensagem: String? = null
)

class TelaPerfilViewModel(
    private val jogadorRepository: JogadorRepository,
    private val partidaRepository: PartidaRepository
) : ViewModel() {

    private val mensagem = MutableStateFlow<String?>(null)

    @OptIn(ExperimentalCoroutinesApi::class)
    private val placar: Flow<Placar> = jogadorRepository.jogadorAtual.flatMapLatest { jogador ->
        if (jogador == null) flowOf(Placar()) else partidaRepository.placar(jogador.id)
    }

    @OptIn(ExperimentalCoroutinesApi::class)
    private val partidas: Flow<List<PartidaEntity>> =
        jogadorRepository.jogadorAtual.flatMapLatest { jogador ->
            if (jogador == null) flowOf(emptyList())
            else partidaRepository.historicoDoJogador(jogador.id)
        }

    val uiState: StateFlow<TelaPerfilUiState> = combine(
        jogadorRepository.jogadorAtual, placar, partidas, mensagem
    ) { jogador, placarAtual, partidasDoJogador, mensagemAtual ->
        TelaPerfilUiState(
            possuiPerfil = jogador != null,
            nome = jogador?.nome.orEmpty(),
            placar = placarAtual,
            partidas = partidasDoJogador,
            mensagem = mensagemAtual
        )
    }.stateIn(
        scope = viewModelScope,
        started = SharingStarted.WhileSubscribed(5000),
        initialValue = TelaPerfilUiState()
    )

    /** Cria um perfil comum ou entra em um jogador já cadastrado pelo nome. */
    fun criarPerfil(nome: String) {
        viewModelScope.launch {
            jogadorRepository.entrarOuRegistrar(nome)
                .onSuccess { mensagem.value = null }
                .onFailure { mensagem.value = it.message }
        }
    }

    /** Sai do perfil atual para criar ou escolher outro. */
    fun trocarPerfil() {
        mensagem.value = null
        jogadorRepository.sair()
    }
}
