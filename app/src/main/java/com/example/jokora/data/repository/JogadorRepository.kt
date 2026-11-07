package com.example.jokora.data.repository

import android.database.sqlite.SQLiteConstraintException
import android.util.Log
import com.example.jokora.data.dao.JogadorDao
import com.example.jokora.data.entity.JogadorComPartidas
import com.example.jokora.data.entity.JogadorEntity
import com.example.jokora.model.Perfil
import kotlinx.coroutines.CancellationException
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.catch

/** Erros de domínio que a UI pode mostrar ao usuário (message em português). */
sealed class JokenpoException(mensagem: String, causa: Throwable? = null) : Exception(mensagem, causa) {
    class NomeInvalido : JokenpoException("Informe um nome com pelo menos 2 caracteres.")
    class JogadorJaExiste(nome: String) : JokenpoException("Já existe um jogador chamado \"$nome\".")
    class JogadorNaoEncontrado(id: Long) : JokenpoException("Jogador $id não encontrado.")
    class JogadaInvalida(mensagem: String) : JokenpoException(mensagem)
    class ErroBanco(causa: Throwable) : JokenpoException("Falha ao acessar o banco de dados.", causa)
}

/** Executa [bloco] convertendo exceções em Result.failure, sem engolir cancelamento de coroutine. */
internal suspend fun <T> executarComSeguranca(tag: String, bloco: suspend () -> T): Result<T> =
    try {
        Result.success(bloco())
    } catch (e: CancellationException) {
        throw e
    } catch (e: JokenpoException) {
        Result.failure(e)
    } catch (e: Exception) {
        Log.e(tag, "Erro no banco", e)
        Result.failure(JokenpoException.ErroBanco(e))
    }

class JogadorRepository(private val dao: JogadorDao) {

    // ---------- Sessão (jogador logado no app) ----------
    private val _jogadorAtual = MutableStateFlow<JogadorEntity?>(null)

    /** Jogador selecionado no momento; null = ninguém entrou ainda. */
    val jogadorAtual: StateFlow<JogadorEntity?> = _jogadorAtual.asStateFlow()

    fun selecionarJogador(jogador: JogadorEntity) { _jogadorAtual.value = jogador }
    fun sair() { _jogadorAtual.value = null }

    // ---------- Consultas (Flow) ----------
    fun listarJogadores(): Flow<List<JogadorEntity>> =
        dao.listarTodos().catch { e ->
            Log.e(TAG, "Erro ao listar jogadores", e)
            emit(emptyList())
        }

    fun observarJogador(id: Long): Flow<JogadorEntity?> =
        dao.observarPorId(id).catch { e ->
            Log.e(TAG, "Erro ao observar jogador $id", e)
            emit(null)
        }

    fun listarJogadoresComPartidas(): Flow<List<JogadorComPartidas>> =
        dao.listarComPartidas().catch { e ->
            Log.e(TAG, "Erro ao listar jogadores com partidas", e)
            emit(emptyList())
        }

    fun observarJogadorComPartidas(id: Long): Flow<JogadorComPartidas?> =
        dao.observarComPartidas(id).catch { e ->
            Log.e(TAG, "Erro ao observar partidas do jogador $id", e)
            emit(null)
        }

    // ---------- Operações ----------
    /** Registra um jogador novo. Falha se o nome for inválido ou já existir. */
    suspend fun registrarJogador(nome: String, perfil: Perfil = Perfil.JOGADOR): Result<JogadorEntity> =
        executarComSeguranca(TAG) {
            val nomeLimpo = validarNome(nome)
            if (dao.buscarPorNome(nomeLimpo) != null) throw JokenpoException.JogadorJaExiste(nomeLimpo)
            try {
                val novo = JogadorEntity(nome = nomeLimpo, perfil = perfil)
                novo.copy(id = dao.inserir(novo))
            } catch (e: SQLiteConstraintException) {
                throw JokenpoException.JogadorJaExiste(nomeLimpo)
            }
        }

    /** Entra com um jogador existente pelo nome ou cria um novo (perfil JOGADOR) e o seleciona. */
    suspend fun entrarOuRegistrar(nome: String): Result<JogadorEntity> =
        executarComSeguranca(TAG) {
            val nomeLimpo = validarNome(nome)
            dao.buscarPorNome(nomeLimpo)
                ?: registrarJogador(nomeLimpo).getOrThrow()
        }.onSuccess { selecionarJogador(it) }

    suspend fun buscarPorId(id: Long): Result<JogadorEntity> =
        executarComSeguranca(TAG) {
            dao.buscarPorId(id) ?: throw JokenpoException.JogadorNaoEncontrado(id)
        }

    suspend fun atualizarJogador(jogador: JogadorEntity): Result<Unit> =
        executarComSeguranca(TAG) {
            val atualizado = jogador.copy(nome = validarNome(jogador.nome))
            try {
                if (dao.atualizar(atualizado) == 0) throw JokenpoException.JogadorNaoEncontrado(jogador.id)
            } catch (e: SQLiteConstraintException) {
                throw JokenpoException.JogadorJaExiste(atualizado.nome)
            }
            if (_jogadorAtual.value?.id == atualizado.id) _jogadorAtual.value = atualizado
        }

    suspend fun alterarPerfil(jogadorId: Long, perfil: Perfil): Result<Unit> =
        executarComSeguranca(TAG) {
            val jogador = dao.buscarPorId(jogadorId) ?: throw JokenpoException.JogadorNaoEncontrado(jogadorId)
            atualizarJogador(jogador.copy(perfil = perfil)).getOrThrow()
        }

    /** Exclui o jogador e, por CASCADE, todo o histórico dele. */
    suspend fun excluirJogador(jogadorId: Long): Result<Unit> =
        executarComSeguranca(TAG) {
            if (dao.excluirPorId(jogadorId) == 0) throw JokenpoException.JogadorNaoEncontrado(jogadorId)
            if (_jogadorAtual.value?.id == jogadorId) sair()
        }

    private fun validarNome(nome: String): String {
        val limpo = nome.trim()
        if (limpo.length < 2) throw JokenpoException.NomeInvalido()
        return limpo
    }

    private companion object { const val TAG = "JogadorRepository" }
}
