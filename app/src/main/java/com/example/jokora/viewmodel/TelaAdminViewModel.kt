package com.example.jokora.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.jokora.data.repository.JogadorRepository
import com.example.jokora.data.repository.PartidaRepository
import com.example.jokora.model.Perfil
import com.example.jokora.model.Resultado
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch

/** Uma linha do ranking de jogadores da área administrativa. */
data class JogadorResumo(
    val id: Long,
    val nome: String,
    val vitorias: Int,
    val partidas: Int
)

data class TelaAdminUiState(
    val isAdmin: Boolean = false,
    val busca: String = "",
    val jogadores: List<JogadorResumo> = emptyList(),
    val totalJogadores: Int = 0,
    val mensagem: String? = null,
    val erro: String? = null
)

/**
 * Área administrativa: ranking de jogadores + Update (renomear) e Delete (excluir) do CRUD.
 * Create = criar perfil/registrar partida; Read = listas vindas do Room via Flow.
 */
class TelaAdminViewModel(
    private val jogadorRepository: JogadorRepository,
    private val partidaRepository: PartidaRepository
) : ViewModel() {

    private val busca = MutableStateFlow("")
    private val mensagem = MutableStateFlow<String?>(null)
    private val erro = MutableStateFlow<String?>(null)

    /**
     * Ranking reativo: o Room emite a lista sempre que algo muda.
     * filter  -> só perfis de jogador (o administrador não entra no ranking)
     * map     -> converte JogadorComPartidas em JogadorResumo (contando vitórias)
     * sortedByDescending -> quem mais venceu primeiro
     */
    private val ranking: Flow<List<JogadorResumo>> =
        jogadorRepository.listarJogadoresComPartidas().map { lista ->
            lista
                .filter { item -> item.jogador.perfil == Perfil.JOGADOR }
                .map { item ->
                    JogadorResumo(
                        id = item.jogador.id,
                        nome = item.jogador.nome,
                        vitorias = item.partidas.count { it.resultado == Resultado.VITORIA },
                        partidas = item.partidas.size
                    )
                }
                .sortedByDescending { it.vitorias }
        }

    val uiState: StateFlow<TelaAdminUiState> = combine(
        jogadorRepository.jogadorAtual, ranking, busca, mensagem, erro
    ) { jogador, listaCompleta, termo, mensagemAtual, erroAtual ->
        // Filtro da busca por nome (também no ViewModel)
        val filtrada = if (termo.isBlank()) {
            listaCompleta
        } else {
            listaCompleta.filter { it.nome.contains(termo.trim(), ignoreCase = true) }
        }

        TelaAdminUiState(
            isAdmin = jogador?.perfil?.podeAcessarAdmin == true,
            busca = termo,
            jogadores = filtrada,
            totalJogadores = listaCompleta.size,
            mensagem = mensagemAtual,
            erro = erroAtual
        )
    }.stateIn(
        scope = viewModelScope,
        started = SharingStarted.WhileSubscribed(5000),
        initialValue = TelaAdminUiState()
    )

    fun atualizarBusca(texto: String) {
        busca.value = texto
    }

    /** Update do CRUD. */
    fun renomearJogador(jogadorId: Long, novoNome: String) {
        viewModelScope.launch {
            jogadorRepository.renomearJogador(jogadorId, novoNome)
                .onSuccess { mostrarMensagem("Jogador renomeado para \"${novoNome.trim()}\".") }
                .onFailure { mostrarErro(it.message) }
        }
    }

    /** Delete do CRUD (as partidas do jogador são apagadas junto, por CASCADE). */
    fun excluirJogador(jogadorId: Long) {
        viewModelScope.launch {
            jogadorRepository.excluirJogador(jogadorId)
                .onSuccess { mostrarMensagem("Jogador excluído.") }
                .onFailure { mostrarErro(it.message) }
        }
    }

    /** Apaga só o histórico de partidas de um jogador, mantendo o cadastro. */
    fun zerarHistorico(jogadorId: Long) {
        viewModelScope.launch {
            partidaRepository.limparHistoricoDoJogador(jogadorId)
                .onSuccess { total -> mostrarMensagem("$total partida(s) apagada(s).") }
                .onFailure { mostrarErro(it.message) }
        }
    }

    fun limparAvisos() {
        mensagem.value = null
        erro.value = null
    }

    private fun mostrarMensagem(texto: String) {
        erro.value = null
        mensagem.value = texto
    }

    private fun mostrarErro(texto: String?) {
        mensagem.value = null
        erro.value = texto ?: "Erro inesperado."
    }
}
