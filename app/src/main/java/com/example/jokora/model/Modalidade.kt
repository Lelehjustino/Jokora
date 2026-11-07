package com.example.jokora.model

/**
 * Modalidades de jogo. Ambas usam o mesmo enum [Jogada] e o mesmo [Resultado];
 * só muda o conjunto de jogadas permitidas.
 */
enum class Modalidade(val rotulo: String, val jogadas: List<Jogada>) {
    TRADICIONAL(
        rotulo = "Jokenpô tradicional",
        jogadas = listOf(Jogada.PEDRA, Jogada.PAPEL, Jogada.TESOURA)
    ),
    AVANCADO(
        rotulo = "Jokenpô avançado (Lagarto e Spock)",
        jogadas = Jogada.values().toList()
    );

    fun permite(jogada: Jogada): Boolean = jogada in jogadas

    /** Jogada sorteada para o oponente (computador). */
    fun jogadaAleatoria(): Jogada = jogadas.random()
}
