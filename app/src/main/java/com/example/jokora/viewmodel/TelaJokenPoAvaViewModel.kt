package com.example.jokora.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.jokora.data.repository.JogadorRepository
import com.example.jokora.data.repository.PartidaRepository
import com.example.jokora.model.Jogada
import com.example.jokora.model.Modalidade
import com.example.jokora.model.Resultado
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch

/**
 * Tudo o que a TelaJokenPoAva precisa mostrar.
 * A tela só lê este estado; quem altera é o ViewModel.
 */
data class TelaJokenPoAvaUiState(
    val opcoes: List<Jogada> = Modalidade.AVANCADO.jogadas, // Pedra, Papel, Tesoura, Lagarto e Spock
    val jogadaJogador: Jogada? = null,
    val jogadaComputador: Jogada? = null,
    val resultado: Resultado? = null,
    val descricao: String = "",          // ex.: "Spock vaporiza Pedra"
    val vitoriasJogador: Int = 0,
    val vitoriasComputador: Int = 0,
    val empates: Int = 0,
    val erro: String? = null
) {
    /** true enquanto o resultado de uma rodada está na tela. */
    val rodadaFinalizada: Boolean get() = resultado != null
}

/**
 * Regras de negócio do Jokenpô avançado (Pedra, Papel, Tesoura, Lagarto e Spock):
 * recebe a escolha do jogador, sorteia a do computador, compara,
 * atualiza o placar e manda o Repository gravar a partida no Room.
 *
 * Cada jogada vence duas outras (as mesmas regras da aba "Avançado" da TelaRegras):
 *  - Pedra   vence Tesoura e Lagarto
 *  - Papel   vence Pedra e Spock
 *  - Tesoura vence Papel e Lagarto
 *  - Lagarto vence Spock e Papel
 *  - Spock   vence Tesoura e Pedra
 */
class TelaJokenPoAvaViewModel(
    private val partidaRepository: PartidaRepository,
    private val jogadorRepository: JogadorRepository
) : ViewModel() {

    private val modalidade = Modalidade.AVANCADO

    private val _uiState = MutableStateFlow(TelaJokenPoAvaUiState())
    val uiState: StateFlow<TelaJokenPoAvaUiState> = _uiState.asStateFlow()

    fun jogar(jogada: Jogada) {
        if (_uiState.value.rodadaFinalizada) return

        val computador = modalidade.jogadaAleatoria()

        val resultado = Resultado.calcular(jogada, computador, modalidade)

        val descricao = when (resultado) {
            Resultado.VITORIA -> jogada.descreverVitoria(computador)
            Resultado.DERROTA -> computador.descreverVitoria(jogada)
            Resultado.EMPATE -> "Os dois escolheram ${jogada.rotulo}"
        } ?: ""

        _uiState.update { estado ->
            estado.copy(
                jogadaJogador = jogada,
                jogadaComputador = computador,
                resultado = resultado,
                descricao = descricao,
                vitoriasJogador = estado.vitoriasJogador + if (resultado == Resultado.VITORIA) 1 else 0,
                vitoriasComputador = estado.vitoriasComputador + if (resultado == Resultado.DERROTA) 1 else 0,
                empates = estado.empates + if (resultado == Resultado.EMPATE) 1 else 0,
                erro = null
            )
        }

        salvarPartida(jogada, computador)
    }

    fun jogarNovamente() {
        _uiState.update { estado ->
            estado.copy(
                jogadaJogador = null,
                jogadaComputador = null,
                resultado = null,
                descricao = "",
                erro = null
            )
        }
    }

    fun reiniciarPontuacao() {
        _uiState.value = TelaJokenPoAvaUiState()
    }

    private fun salvarPartida(jogada: Jogada, computador: Jogada) {
        viewModelScope.launch {
            val jogador = jogadorRepository.jogadorAtual.value
                ?: jogadorRepository.entrarOuRegistrar(NOME_VISITANTE).getOrElse { erro ->
                    _uiState.update { it.copy(erro = erro.message) }
                    return@launch
                }

            partidaRepository.registrarPartida(jogador.id, modalidade, jogada, computador)
                .onFailure { erro ->
                    _uiState.update { it.copy(erro = "A partida não foi salva: ${erro.message}") }
                }
        }
    }

    private companion object {
        const val NOME_VISITANTE = "Visitante"
    }
}