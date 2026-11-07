package com.example.jokora.data.repository

import android.util.Log
import com.example.jokora.data.dao.PartidaDao
import com.example.jokora.data.entity.PartidaEntity
import com.example.jokora.model.Jogada
import com.example.jokora.model.Modalidade
import com.example.jokora.model.Resultado
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.catch
import kotlinx.coroutines.flow.map

/** Placar consolidado de um jogador (pronto para a UI exibir). */
data class Placar(
    val vitorias: Int = 0,
    val derrotas: Int = 0,
    val empates: Int = 0
) {
    val total: Int get() = vitorias + derrotas + empates
    val aproveitamento: Float get() = if (total == 0) 0f else vitorias.toFloat() / total
}

class PartidaRepository(private val dao: PartidaDao) {

    // ---------- Regras + persistência ----------
    /**
     * Joga uma rodada contra o computador: sorteia a jogada do oponente,
     * calcula o resultado e grava a partida. Usado pelas duas modalidades.
     */
    suspend fun jogarContraComputador(
        jogadorId: Long,
        modalidade: Modalidade,
        jogada: Jogada
    ): Result<PartidaEntity> =
        registrarPartida(jogadorId, modalidade, jogada, modalidade.jogadaAleatoria())

    /** Registra uma partida já decidida (ex.: dois jogadores no mesmo aparelho). */
    suspend fun registrarPartida(
        jogadorId: Long,
        modalidade: Modalidade,
        jogadaJogador: Jogada,
        jogadaOponente: Jogada
    ): Result<PartidaEntity> = executarComSeguranca(TAG) {
        val resultado = try {
            Resultado.calcular(jogadaJogador, jogadaOponente, modalidade)
        } catch (e: IllegalArgumentException) {
            throw JokenpoException.JogadaInvalida(e.message ?: "Jogada inválida")
        }
        val partida = PartidaEntity(
            jogadorId = jogadorId,
            modalidade = modalidade,
            jogadaJogador = jogadaJogador,
            jogadaOponente = jogadaOponente,
            resultado = resultado
        )
        partida.copy(id = dao.inserir(partida))
    }

    // ---------- Histórico (Flow) ----------
    fun historicoGeral(): Flow<List<PartidaEntity>> =
        dao.listarTodas().catch { e ->
            Log.e(TAG, "Erro ao carregar histórico", e)
            emit(emptyList())
        }

    fun historicoDoJogador(jogadorId: Long): Flow<List<PartidaEntity>> =
        dao.listarPorJogador(jogadorId).catch { e ->
            Log.e(TAG, "Erro ao carregar histórico do jogador $jogadorId", e)
            emit(emptyList())
        }

    fun historicoDoJogador(jogadorId: Long, modalidade: Modalidade): Flow<List<PartidaEntity>> =
        dao.listarPorJogadorEModalidade(jogadorId, modalidade).catch { e ->
            Log.e(TAG, "Erro ao carregar histórico ($modalidade) do jogador $jogadorId", e)
            emit(emptyList())
        }

    /** Placar do jogador; passe [modalidade] para filtrar, ou null para as duas. */
    fun placar(jogadorId: Long, modalidade: Modalidade? = null): Flow<Placar> {
        val origem = if (modalidade == null) historicoDoJogador(jogadorId)
        else historicoDoJogador(jogadorId, modalidade)
        return origem.map { partidas ->
            Placar(
                vitorias = partidas.count { it.resultado == Resultado.VITORIA },
                derrotas = partidas.count { it.resultado == Resultado.DERROTA },
                empates = partidas.count { it.resultado == Resultado.EMPATE }
            )
        }
    }

    // ---------- Manutenção (área admin) ----------
    suspend fun excluirPartida(partida: PartidaEntity): Result<Unit> =
        executarComSeguranca(TAG) { dao.excluir(partida); Unit }

    suspend fun limparHistoricoDoJogador(jogadorId: Long): Result<Int> =
        executarComSeguranca(TAG) { dao.excluirDoJogador(jogadorId) }

    suspend fun limparTodoHistorico(): Result<Int> =
        executarComSeguranca(TAG) { dao.excluirTodas() }

    private companion object { const val TAG = "PartidaRepository" }
}
