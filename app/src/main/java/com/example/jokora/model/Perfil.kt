package com.example.jokora.model

/**
 * Tipos de usuário. Controla o acesso à área administrativa:
 * - JOGADOR: cria o perfil pelo nome e joga.
 * - ADMIN: entra pelo login simulado (senha) e acessa a TelaAdmin.
 */
enum class Perfil(val rotulo: String) {
    JOGADOR("Jogador"),
    ADMIN("Administrador");

    val podeAcessarAdmin: Boolean
        get() = this == ADMIN
}
