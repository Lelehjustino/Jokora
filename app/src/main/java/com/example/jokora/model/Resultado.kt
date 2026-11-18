package com.example.jokora.model

enum class Resultado(val rotulo: String) {
    VITORIA("Vitória"),
    DERROTA("Derrota"),
    EMPATE("Empate");

    companion object {
        fun calcular(jogador: Jogada, oponente: Jogada, modalidade: Modalidade): Resultado {
            require(modalidade.permite(jogador)) { "${jogador.rotulo} não é permitida em ${modalidade.rotulo}" }
            require(modalidade.permite(oponente)) { "${oponente.rotulo} não é permitida em ${modalidade.rotulo}" }
            return when {
                jogador == oponente -> EMPATE
                jogador.venceDe(oponente) -> VITORIA
                else -> DERROTA
            }
        }
    }
}
