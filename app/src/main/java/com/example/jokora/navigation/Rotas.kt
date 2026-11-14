package com.example.jokora.navigation

/** Rotas nomeadas do app. Use sempre estas constantes — nunca strings soltas. */
sealed class Rota(
    val route: String,
    val titulo: String
) {
    object Inicial : Rota("tela_inicial", "Início")
    object Regras : Rota("tela_regras", "Regras")
    object Jokenpo : Rota("tela_jokenpo", "Jokenpô")
    object JokenpoAvancado : Rota("tela_jokenpo_avancado", "Avançado")
    object Historico : Rota("tela_historico", "Perfil")

    companion object {
        val inicial: Rota get() = Inicial
        val todas: List<Rota> get() = listOf(Inicial, Regras, Jokenpo, JokenpoAvancado, Historico)
        fun itensBarraInferior(): List<Rota> = todas
        fun daRoute(route: String?): Rota? = todas.firstOrNull { it.route == route }
    }
}
