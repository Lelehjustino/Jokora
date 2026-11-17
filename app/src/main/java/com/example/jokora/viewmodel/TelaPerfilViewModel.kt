package com.example.jokora.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.jokora.data.entity.PartidaEntity
import com.example.jokora.data.repository.JogadorRepository
import com.example.jokora.data.repository.PartidaRepository
import com.example.jokora.data.repository.Placar
import com.example.jokora.model.Modalidade
import com.example.jokora.model.Resultado
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
    val tipoPerfil: String = "",
    val isAdmin: Boolean = false,
    /** true = estatísticas só do modo avançado (botão de estrela). */
    val apenasAvancado: Boolean = false,
    val placar: Placar = Placar(),
    val partidas: List<PartidaEntity> = emptyList(),
    val mensagem: String? = null
)

class TelaPerfilViewModel(
    private val jogadorRepository: JogadorRepository,
    private val partidaRepository: PartidaRepository
) : ViewModel() {

    private val mensagem = MutableStateFlow<String?>(null)
    private val apenasAvancado = MutableStateFlow(false)

    /** Partidas do jogador logado (Room via Flow); troca sozinho quando o perfil muda. */
    @OptIn(ExperimentalCoroutinesApi::class)
    private val partidas: Flow<List<PartidaEntity>> =
        jogadorRepository.jogadorAtual.flatMapLatest { jogador ->
            if (jogador == null) flowOf(emptyList())
            else partidaRepository.historicoDoJogador(jogador.id)
        }

    val uiState: StateFlow<TelaPerfilUiState> = combine(
        jogadorRepository.jogadorAtual, partidas, apenasAvancado, mensagem
    ) { jogador, todasPartidas, soAvancado, mensagemAtual ->

        // Lógica de lista no ViewModel: filter (modalidade) + sortedByDescending (mais recentes primeiro)
        val partidasDoPlacar = if (soAvancado) {
            todasPartidas.filter { it.modalidade == Modalidade.AVANCADO }
        } else {
            todasPartidas
        }

        TelaPerfilUiState(
            possuiPerfil = jogador != null,
            nome = jogador?.nome.orEmpty(),
            tipoPerfil = jogador?.perfil?.rotulo.orEmpty(),
            isAdmin = jogador?.perfil?.podeAcessarAdmin == true,
            apenasAvancado = soAvancado,
            placar = Placar(
                vitorias = partidasDoPlacar.count { it.resultado == Resultado.VITORIA },
                derrotas = partidasDoPlacar.count { it.resultado == Resultado.DERROTA },
                empates = partidasDoPlacar.count { it.resultado == Resultado.EMPATE }
            ),
            partidas = todasPartidas.sortedByDescending { it.dataHora },
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

    /** Login simulado do administrador (senha conferida no Repository). */
    fun entrarComoAdmin(senha: String) {
        viewModelScope.launch {
            jogadorRepository.entrarComoAdmin(senha)
                .onSuccess { mensagem.value = null }
                .onFailure { mensagem.value = it.message }
        }
    }

    /** Alterna as estatísticas entre "todas as partidas" e "só modo avançado". */
    fun alternarEstatisticas() {
        apenasAvancado.value = !apenasAvancado.value
    }

    /** Sai do perfil atual para criar ou escolher outro. */
    fun trocarPerfil() {
        mensagem.value = null
        apenasAvancado.value = false
        jogadorRepository.sair()
    }
}
