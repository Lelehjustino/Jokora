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
 * Tudo o que a TelaJokenPo precisa mostrar.
 * A tela só lê este estado; quem altera é o ViewModel.
 */
data class TelaJokenPoUiState(
    val opcoes: List<Jogada> = Modalidade.TRADICIONAL.jogadas, // Pedra, Papel e Tesoura
    val jogadaJogador: Jogada? = null,
    val jogadaComputador: Jogada? = null,
    val resultado: Resultado? = null,
    val descricao: String = "",          // ex.: "Pedra quebra Tesoura"
    val vitoriasJogador: Int = 0,
    val vitoriasComputador: Int = 0,
    val empates: Int = 0,
    val erro: String? = null
) {
    /** true enquanto o resultado de uma rodada está na tela. */
    val rodadaFinalizada: Boolean get() = resultado != null
}

/**
 * Regras de negócio do Jokenpô tradicional:
 * recebe a escolha do jogador, sorteia a do computador, compara,
 * atualiza o placar e manda o Repository gravar a partida no Room.
 */
class TelaJokenPoViewModel(
    private val partidaRepository: PartidaRepository,
    private val jogadorRepository: JogadorRepository
) : ViewModel() {

    private val modalidade = Modalidade.TRADICIONAL

    private val _uiState = MutableStateFlow(TelaJokenPoUiState())
    val uiState: StateFlow<TelaJokenPoUiState> = _uiState.asStateFlow()

    /** 1. O jogador escolhe → 2. o ViewModel recebe a escolha. */
    fun jogar(jogada: Jogada) {
        if (_uiState.value.rodadaFinalizada) return // precisa clicar em "Jogar novamente"

        // 3. O computador faz uma escolha aleatória
        val computador = modalidade.jogadaAleatoria()

        // 4. O sistema compara as duas jogadas
        val resultado = Resultado.calcular(jogada, computador, modalidade)

        val descricao = when (resultado) {
            Resultado.VITORIA -> jogada.descreverVitoria(computador)
            Resultado.DERROTA -> computador.descreverVitoria(jogada)
            Resultado.EMPATE -> "Os dois escolheram ${jogada.rotulo}"
        } ?: ""

        // 5. O resultado aparece na tela e 6. a pontuação é atualizada
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

        // 7. A partida é registrada no banco Room
        salvarPartida(jogada, computador)
    }

    /** Limpa a rodada atual, mantendo o placar. */
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

    /** Zera o placar da tela. O histórico salvo no banco não é apagado. */
    fun reiniciarPontuacao() {
        _uiState.value = TelaJokenPoUiState()
    }

    private fun salvarPartida(jogada: Jogada, computador: Jogada) {
        viewModelScope.launch {
            // Toda partida precisa de um jogador (chave estrangeira no Room).
            // Se ninguém entrou pela Tela Inicial, usa o jogador "Visitante".
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
