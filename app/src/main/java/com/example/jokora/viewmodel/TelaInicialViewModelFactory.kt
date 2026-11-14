package com.example.jokora.viewmodel

import android.content.Context
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import com.example.jokora.data.database.DatabaseProvider

class TelaInicialViewModelFactory(
    context: Context
) : ViewModelProvider.Factory {

    private val jogadorRepository =
        DatabaseProvider.jogadorRepository(context.applicationContext)

    @Suppress("UNCHECKED_CAST")
    override fun <T : ViewModel> create(
        modelClass: Class<T>
    ): T {
        if (modelClass.isAssignableFrom(TelaInicialViewModel::class.java)) {
            return TelaInicialViewModel(jogadorRepository) as T
        }

        throw IllegalArgumentException(
            "ViewModel desconhecido: ${modelClass.name}"
        )
    }
}