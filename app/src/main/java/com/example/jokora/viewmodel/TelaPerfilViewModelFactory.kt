package com.example.jokora.viewmodel

import android.content.Context
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import com.example.jokora.data.database.DatabaseProvider

/** Cria o TelaPerfilViewModel entregando os Repositories que ele precisa. */
class TelaPerfilViewModelFactory(
    context: Context
) : ViewModelProvider.Factory {

    private val jogadorRepository =
        DatabaseProvider.jogadorRepository(context.applicationContext)

    private val partidaRepository =
        DatabaseProvider.partidaRepository(context.applicationContext)

    @Suppress("UNCHECKED_CAST")
    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        if (modelClass.isAssignableFrom(TelaPerfilViewModel::class.java)) {
            return TelaPerfilViewModel(jogadorRepository, partidaRepository) as T
        }
        throw IllegalArgumentException("ViewModel desconhecido: ${modelClass.name}")
    }
}
