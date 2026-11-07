package com.example.jokora.navigation

import com.example.jokora.model.Perfil

/** Rotas nomeadas do app. Use sempre estas constantes — nunca strings soltas. */
sealed class Rota(
    val route: String,
    val titulo: String,
    val apenasAdmin: Boolean = false
) {
    object Inicial : Rota("tela_inicial", "Início")
    object Regras : Rota("tela_regras", "Regras")
    object Jokenpo : Rota("tela_jokenpo", "Jokenpô")
    object JokenpoAvancado : Rota("tela_jokenpo_avancado", "Avançado")
    object Historico : Rota("tela_historico", "Histórico")
    object Admin : Rota("tela_admin", "Admin", apenasAdmin = true)

    /** O perfil pode abrir esta rota? */
    fun permitidaPara(perfil: Perfil): Boolean = !apenasAdmin || perfil.podeAcessarAdmin

    companion object {
        val inicial: Rota get() = Inicial

        val todas: List<Rota> get() = listOf(Inicial, Regras, Jokenpo, JokenpoAvancado, Historico, Admin)

        /** Itens da Bottom Navigation Bar; Admin só aparece para quem tem permissão. */
        fun itensBarraInferior(perfil: Perfil): List<Rota> = todas.filter { it.permitidaPara(perfil) }

        fun daRoute(route: String?): Rota? = todas.firstOrNull { it.route == route }
    }
}
