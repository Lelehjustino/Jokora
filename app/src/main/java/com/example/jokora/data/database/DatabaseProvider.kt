package com.example.jokora.data.database

import android.content.Context
import androidx.room.Room
import com.example.jokora.data.repository.JogadorRepository
import com.example.jokora.data.repository.PartidaRepository

object DatabaseProvider {

    @Volatile private var database: JokenpoDatabase? = null
    @Volatile private var jogadorRepository: JogadorRepository? = null
    @Volatile private var partidaRepository: PartidaRepository? = null

    fun getDatabase(context: Context): JokenpoDatabase =
        database ?: synchronized(this) {
            database ?: Room.databaseBuilder(
                context.applicationContext,
                JokenpoDatabase::class.java,
                JokenpoDatabase.NOME_BANCO
            )
                .fallbackToDestructiveMigration()
                .build()
                .also { database = it }
        }

    fun jogadorRepository(context: Context): JogadorRepository =
        jogadorRepository ?: synchronized(this) {
            jogadorRepository ?: JogadorRepository(getDatabase(context).jogadorDao())
                .also { jogadorRepository = it }
        }

    fun partidaRepository(context: Context): PartidaRepository =
        partidaRepository ?: synchronized(this) {
            partidaRepository ?: PartidaRepository(getDatabase(context).partidaDao())
                .also { partidaRepository = it }
        }
}
