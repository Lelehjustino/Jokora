package com.example.jokora.model

/** Tipos de usuário. Controla o acesso à área administrativa. */
enum class Perfil(val rotulo: String) {
    JOGADOR("Jogador"),
    ADMIN("Administrador");

    val podeAcessarAdmin: Boolean
        get() = this == ADMIN
}
