package com.example.jokora.model

/** Resultado sempre do ponto de vista do jogador. */
enum class Resultado(val rotulo: String) {
    VITORIA("Vitória"),
    DERROTA("Derrota"),
    EMPATE("Empate");

    companion object {
        /**
         * Calcula o resultado de uma rodada.
         * @throws IllegalArgumentException se alguma jogada não pertence à modalidade.
         */
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
