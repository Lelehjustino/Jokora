package com.example.jokora.data.database

import androidx.room.Database
import androidx.room.RoomDatabase
import androidx.room.TypeConverter
import androidx.room.TypeConverters
import com.example.jokora.data.dao.JogadorDao
import com.example.jokora.data.dao.PartidaDao
import com.example.jokora.data.entity.JogadorEntity
import com.example.jokora.data.entity.PartidaEntity
import com.example.jokora.model.Jogada
import com.example.jokora.model.Modalidade
import com.example.jokora.model.Perfil
import com.example.jokora.model.Resultado

@Database(
    entities = [JogadorEntity::class, PartidaEntity::class],
    version = 1,
    exportSchema = false
)
@TypeConverters(JokenpoConverters::class)
abstract class JokenpoDatabase : RoomDatabase() {
    abstract fun jogadorDao(): JogadorDao
    abstract fun partidaDao(): PartidaDao

    companion object {
        const val NOME_BANCO = "jokora.db"
    }
}

/** Salva os enums como texto (nome da constante), legível e estável no SQLite. */
class JokenpoConverters {
    @TypeConverter fun jogadaParaTexto(valor: Jogada): String = valor.name
    @TypeConverter fun textoParaJogada(valor: String): Jogada = Jogada.valueOf(valor)

    @TypeConverter fun resultadoParaTexto(valor: Resultado): String = valor.name
    @TypeConverter fun textoParaResultado(valor: String): Resultado = Resultado.valueOf(valor)

    @TypeConverter fun modalidadeParaTexto(valor: Modalidade): String = valor.name
    @TypeConverter fun textoParaModalidade(valor: String): Modalidade = Modalidade.valueOf(valor)

    @TypeConverter fun perfilParaTexto(valor: Perfil): String = valor.name
    @TypeConverter fun textoParaPerfil(valor: String): Perfil = Perfil.valueOf(valor)
}
