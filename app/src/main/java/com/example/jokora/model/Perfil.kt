package com.example.jokora.model

enum class Perfil(val rotulo: String) {
    JOGADOR("Jogador"),
    ADMIN("Administrador");

    val podeAcessarAdmin: Boolean
        get() = this == ADMIN
}
