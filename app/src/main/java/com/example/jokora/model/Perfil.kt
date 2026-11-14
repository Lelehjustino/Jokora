package com.example.jokora.model

/**
 * Perfil persistido no banco local.
 * ADMIN é mantido apenas para compatibilidade com bancos antigos; não existe
 * opção administrativa na interface nem rota administrativa no aplicativo.
 */
enum class Perfil(val rotulo: String) {
    JOGADOR("Jogador"),
    ADMIN("Administrador");

    val podeAcessarAdmin: Boolean
        get() = this == ADMIN
}
