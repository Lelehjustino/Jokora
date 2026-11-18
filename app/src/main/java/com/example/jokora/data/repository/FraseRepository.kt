package com.example.jokora.data.repository

import android.util.Log
import com.example.jokora.data.remote.ConselhoApi
import kotlinx.coroutines.CancellationException
import java.io.IOException

/**
 * Busca a "frase do dia" na API externa (Retrofit).
 * Isola as falhas de rede/API: o ViewModel recebe sempre um Result, nunca uma exceção.
 */
class FraseRepository(private val api: ConselhoApi) {

    suspend fun buscarFraseDoDia(): Result<String> =
        try {
            val resposta = api.buscarConselho()
            Result.success(resposta.slip.advice)
        } catch (e: CancellationException) {
            throw e
        } catch (e: IOException) {
            Log.e(TAG, "Sem conexão com a API", e)
            Result.failure(JokenpoException.SemConexao(e))
        } catch (e: Exception) {
            Log.e(TAG, "Erro ao consultar a API", e)
            Result.failure(JokenpoException.ErroApi(e))
        }

    private companion object { const val TAG = "FraseRepository" }
}
