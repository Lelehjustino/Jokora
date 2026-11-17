package com.example.jokora.data.remote

import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory

/** Ponto único de criação do Retrofit (singleton, criado só no primeiro uso). */
object RetrofitClient {

    private const val BASE_URL = "https://api.adviceslip.com/"

    val conselhoApi: ConselhoApi by lazy {
        Retrofit.Builder()
            .baseUrl(BASE_URL)
            .addConverterFactory(GsonConverterFactory.create())
            .build()
            .create(ConselhoApi::class.java)
    }
}
