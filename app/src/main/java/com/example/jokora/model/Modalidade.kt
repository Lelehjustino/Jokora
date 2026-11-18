package com.example.jokora.model

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

    fun jogadaAleatoria(): Jogada = jogadas.random()
}
