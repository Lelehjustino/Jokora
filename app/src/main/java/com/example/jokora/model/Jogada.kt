package com.example.jokora.model

/**
 * Jogadas possíveis. As três primeiras formam o Jokenpô tradicional;
 * LAGARTO e SPOCK completam a modalidade avançada.
 * As regras de vitória ficam aqui para que as duas modalidades compartilhem a mesma lógica.
 */
enum class Jogada(val rotulo: String) {
    PEDRA("Pedra"),
    PAPEL("Papel"),
    TESOURA("Tesoura"),
    LAGARTO("Lagarto"),
    SPOCK("Spock");

    /** true se esta jogada vence [outra]. */
    fun venceDe(outra: Jogada): Boolean = regras[this]?.containsKey(outra) == true

    /** Verbo da vitória ("Pedra quebra Tesoura"), ou null se esta jogada não vence [outra]. */
    fun verboContra(outra: Jogada): String? = regras[this]?.get(outra)

    /** Frase pronta da vitória, ex.: "Spock vaporiza Pedra". */
    fun descreverVitoria(outra: Jogada): String? =
        verboContra(outra)?.let { "$rotulo $it ${outra.rotulo}" }

    companion object {
        // Cada jogada vence exatamente duas outras (no modo avançado)
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
