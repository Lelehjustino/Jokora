package com.example.jokora.data.dao

import androidx.room.Dao
import androidx.room.Delete
import androidx.room.Insert
import androidx.room.Query
import androidx.room.Update
import com.example.jokora.data.entity.PartidaEntity
import com.example.jokora.model.Modalidade
import kotlinx.coroutines.flow.Flow

@Dao
interface PartidaDao {

    // ---------- Create ----------
    @Insert
    suspend fun inserir(partida: PartidaEntity): Long

    // ---------- Read ----------
    @Query("SELECT * FROM partidas ORDER BY dataHora DESC")
    fun listarTodas(): Flow<List<PartidaEntity>>

    @Query("SELECT * FROM partidas WHERE jogadorId = :jogadorId ORDER BY dataHora DESC")
    fun listarPorJogador(jogadorId: Long): Flow<List<PartidaEntity>>

    @Query(
        "SELECT * FROM partidas WHERE jogadorId = :jogadorId AND modalidade = :modalidade " +
            "ORDER BY dataHora DESC"
    )
    fun listarPorJogadorEModalidade(jogadorId: Long, modalidade: Modalidade): Flow<List<PartidaEntity>>

    @Query("SELECT * FROM partidas WHERE id = :id")
    suspend fun buscarPorId(id: Long): PartidaEntity?

    // ---------- Update ----------
    @Update
    suspend fun atualizar(partida: PartidaEntity): Int

    // ---------- Delete ----------
    @Delete
    suspend fun excluir(partida: PartidaEntity): Int

    @Query("DELETE FROM partidas WHERE jogadorId = :jogadorId")
    suspend fun excluirDoJogador(jogadorId: Long): Int

    @Query("DELETE FROM partidas")
    suspend fun excluirTodas(): Int
}
