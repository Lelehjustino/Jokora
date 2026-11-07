package com.example.jokora.data.dao

import androidx.room.Dao
import androidx.room.Delete
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import androidx.room.Transaction
import androidx.room.Update
import com.example.jokora.data.entity.JogadorComPartidas
import com.example.jokora.data.entity.JogadorEntity
import kotlinx.coroutines.flow.Flow

@Dao
interface JogadorDao {

    // ---------- Create ----------
    /** Lança SQLiteConstraintException se o nome já existir (índice único). */
    @Insert(onConflict = OnConflictStrategy.ABORT)
    suspend fun inserir(jogador: JogadorEntity): Long

    // ---------- Read ----------
    @Query("SELECT * FROM jogadores ORDER BY nome COLLATE NOCASE")
    fun listarTodos(): Flow<List<JogadorEntity>>

    @Query("SELECT * FROM jogadores WHERE id = :id")
    fun observarPorId(id: Long): Flow<JogadorEntity?>

    @Query("SELECT * FROM jogadores WHERE id = :id")
    suspend fun buscarPorId(id: Long): JogadorEntity?

    @Query("SELECT * FROM jogadores WHERE nome = :nome COLLATE NOCASE LIMIT 1")
    suspend fun buscarPorNome(nome: String): JogadorEntity?

    @Transaction
    @Query("SELECT * FROM jogadores ORDER BY nome COLLATE NOCASE")
    fun listarComPartidas(): Flow<List<JogadorComPartidas>>

    @Transaction
    @Query("SELECT * FROM jogadores WHERE id = :id")
    fun observarComPartidas(id: Long): Flow<JogadorComPartidas?>

    // ---------- Update ----------
    @Update
    suspend fun atualizar(jogador: JogadorEntity): Int

    // ---------- Delete ----------
    @Delete
    suspend fun excluir(jogador: JogadorEntity): Int

    @Query("DELETE FROM jogadores WHERE id = :id")
    suspend fun excluirPorId(id: Long): Int
}
