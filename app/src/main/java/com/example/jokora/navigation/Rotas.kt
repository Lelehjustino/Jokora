package com.example.jokora.navigation

sealed class Rota(
    val route: String,
    val titulo: String,
    val apenasAdmin: Boolean = false
) {
    object Inicial : Rota("tela_inicial", "Início")
    object Regras : Rota("tela_regras", "Regras")
    object Jokenpo : Rota("tela_jokenpo", "Jokenpô")
    object JokenpoAvancado : Rota("tela_jokenpo_avancado", "Avançado")
    object Perfil : Rota("tela_perfil", "Perfil")
    object Admin : Rota("tela_admin", "Admin", apenasAdmin = true)

    companion object {
        val inicial: Rota get() = Inicial
        val todas: List<Rota> get() = listOf(Inicial, Regras, Jokenpo, JokenpoAvancado, Perfil, Admin)
    }
}
