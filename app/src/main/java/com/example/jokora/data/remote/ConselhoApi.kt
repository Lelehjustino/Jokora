package com.example.jokora.data.remote

import retrofit2.http.GET

/**
 * Formato do JSON devolvido por https://api.adviceslip.com/advice
 *
 * {"slip": {"id": 42, "advice": "..."}}
 */
data class ConselhoResposta(
    val slip: Conselho
)

data class Conselho(
    val id: Int,
    val advice: String
)

/** Endpoints da API pública Advice Slip (não precisa de chave). */
interface ConselhoApi {

    /** Sorteia um conselho aleatório. */
    @GET("advice")
    suspend fun buscarConselho(): ConselhoResposta
}
