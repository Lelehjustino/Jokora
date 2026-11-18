package com.example.jokora.model

enum class Jogada(val rotulo: String) {
    PEDRA("Pedra"),
    PAPEL("Papel"),
    TESOURA("Tesoura"),
    LAGARTO("Lagarto"),
    SPOCK("Spock");

    fun venceDe(outra: Jogada): Boolean = regras[this]?.containsKey(outra) == true

    fun verboContra(outra: Jogada): String? = regras[this]?.get(outra)

    fun descreverVitoria(outra: Jogada): String? =
        verboContra(outra)?.let { "$rotulo $it ${outra.rotulo}" }

    companion object {
        private val regras: Map<Jogada, Map<Jogada, String>> by lazy {
            mapOf(
                PEDRA to mapOf(TESOURA to "quebra", LAGARTO to "esmaga"),
                PAPEL to mapOf(PEDRA to "cobre", SPOCK to "refuta"),
                TESOURA to mapOf(PAPEL to "corta", LAGARTO to "decapita"),
                LAGARTO to mapOf(PAPEL to "come", SPOCK to "envenena"),
                SPOCK to mapOf(TESOURA to "derrete", PEDRA to "vaporiza")
            )
        }
    }
}
