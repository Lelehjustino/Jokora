package com.example.jokora.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.jokora.data.repository.JogadorRepository
import com.example.jokora.navigation.Rota
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.stateIn

data class SessaoUiState(
    val isAdmin: Boolean = false,
    val abasVisiveis: List<Rota> = Rota.todas.filter { !it.apenasAdmin }
)

class SessaoViewModel(
    jogadorRepository: JogadorRepository
) : ViewModel() {

    val uiState: StateFlow<SessaoUiState> = jogadorRepository.jogadorAtual
        .map { jogador ->
            val isAdmin = jogador?.perfil?.podeAcessarAdmin == true
            SessaoUiState(
                isAdmin = isAdmin,
                abasVisiveis = Rota.todas.filter { rota -> !rota.apenasAdmin || isAdmin }
            )
        }
        .stateIn(
            scope = viewModelScope,
            started = SharingStarted.WhileSubscribed(5000),
            initialValue = SessaoUiState()
        )
}
