package com.example.jokora.data.entity

import androidx.room.Entity
import androidx.room.ForeignKey
import androidx.room.Index
import androidx.room.PrimaryKey
import com.example.jokora.model.Jogada
import com.example.jokora.model.Modalidade
import com.example.jokora.model.Resultado

@Entity(
    tableName = "partidas",
    foreignKeys = [
        ForeignKey(
            entity = JogadorEntity::class,
            parentColumns = ["id"],
            childColumns = ["jogadorId"],
            onDelete = ForeignKey.CASCADE // apagar o jogador apaga o histórico dele
        )
    ],
    indices = [Index("jogadorId")]
)
data class PartidaEntity(
    @PrimaryKey(autoGenerate = true)
    val id: Long = 0,
    val jogadorId: Long,
    val modalidade: Modalidade,
    val jogadaJogador: Jogada,
    val jogadaOponente: Jogada,
    val resultado: Resultado,
    val dataHora: Long = System.currentTimeMillis()
)
