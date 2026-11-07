package com.example.jokora.data.entity

import androidx.room.Embedded
import androidx.room.Entity
import androidx.room.Index
import androidx.room.PrimaryKey
import androidx.room.Relation
import com.example.jokora.model.Perfil

@Entity(
    tableName = "jogadores",
    indices = [Index(value = ["nome"], unique = true)]
)
data class JogadorEntity(
    @PrimaryKey(autoGenerate = true)
    val id: Long = 0,
    val nome: String,
    val perfil: Perfil = Perfil.JOGADOR,
    val criadoEm: Long = System.currentTimeMillis()
)

/** Relacionamento 1:N — um jogador e todas as suas partidas. */
data class JogadorComPartidas(
    @Embedded
    val jogador: JogadorEntity,
    @Relation(
        parentColumn = "id",
        entityColumn = "jogadorId"
    )
    val partidas: List<PartidaEntity>
)
