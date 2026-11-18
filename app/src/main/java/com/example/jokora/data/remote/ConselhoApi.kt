package com.example.jokora.data.remote

import retrofit2.http.GET

data class ConselhoResposta(
    val slip: Conselho
)

data class Conselho(
    val id: Int,
    val advice: String
)

interface ConselhoApi {

    @GET("advice")
    suspend fun buscarConselho(): ConselhoResposta
}
