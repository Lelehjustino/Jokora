package com.example.jokora.viewmodel

import android.content.Context
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import com.example.jokora.data.database.DatabaseProvider

/** Cria o TelaJokenPoViewModel entregando os Repositories que ele precisa. */
class TelaJokenPoViewModelFactory(
    context: Context
) : ViewModelProvider.Factory {

    private val partidaRepository =
        DatabaseProvider.partidaRepository(context.applicationContext)

    private val jogadorRepository =
        DatabaseProvider.jogadorRepository(context.applicationContext)

    @Suppress("UNCHECKED_CAST")
    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        if (modelClass.isAssignableFrom(TelaJokenPoViewModel::class.java)) {
            return TelaJokenPoViewModel(partidaRepository, jogadorRepository) as T
        }
        throw IllegalArgumentException("ViewModel desconhecido: ${modelClass.name}")
    }
}
