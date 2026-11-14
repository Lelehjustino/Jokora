package com.example.jokora.ui.screens

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.Button
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Tab
import androidx.compose.material3.TabRow
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.viewmodel.compose.viewModel
import com.example.jokora.model.RegraJogo
import com.example.jokora.viewmodel.TelaRegrasViewModel
import com.example.jokora.viewmodel.TelaRegrasViewModelFactory
import com.example.jokora.navigation.AcoesNavegacao
import com.example.jokora.navigation.Rota

object AbaRegras {
    const val TRADICIONAL = 0
    const val AVANCADO = 1

    var proxima: Int = TRADICIONAL
}

@Composable
fun TelaRegras(
    modifier: Modifier = Modifier,
    nav: AcoesNavegacao,
) {
    val viewModel: TelaRegrasViewModel = viewModel(
        factory = TelaRegrasViewModelFactory()
    )

    val uiState = viewModel.uiState

    var abaSelecionada by remember {
        mutableIntStateOf(AbaRegras.proxima).also { AbaRegras.proxima = AbaRegras.TRADICIONAL }
    }

    val regras = if (abaSelecionada == 0) {
        uiState.regrasTradicionais
    } else {
        uiState.regrasAvancadas
    }

    Scaffold(
        modifier = modifier.fillMaxSize()
    ) { padding ->

        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(padding)
                .padding(horizontal = 18.dp)
        ) {

            Spacer(modifier = Modifier.height(16.dp))

            TextButton(
                onClick = {
                    nav.voltar()
                }
            ) {
                Text("← Voltar")
            }

            Text(
                text = "📖 Como jogar",
                fontSize = 28.sp,
                fontWeight = FontWeight.Bold,
                color = MaterialTheme.colorScheme.primary
            )

            Spacer(modifier = Modifier.height(6.dp))

            Text(
                text = "Conheça as regras e descubra como vencer cada rodada!",
                fontSize = 14.sp
            )

            Spacer(modifier = Modifier.height(18.dp))

            TabRow(
                selectedTabIndex = abaSelecionada
            ) {
                Tab(
                    selected = abaSelecionada == 0,
                    onClick = { abaSelecionada = 0 },
                    text = {
                        Text("Tradicional")
                    }
                )

                Tab(
                    selected = abaSelecionada == 1,
                    onClick = { abaSelecionada = 1 },
                    text = {
                        Text("Avançado")
                    }
                )
            }

            Spacer(modifier = Modifier.height(12.dp))

            Text(
                text = if (abaSelecionada == 0)
                    "Pedra, Papel e Tesoura"
                else
                    "Pedra, Papel, Tesoura, Lagarto e Spock",
                fontSize = 18.sp,
                fontWeight = FontWeight.Bold
            )

            Text(
                text = if (abaSelecionada == 0)
                    "Três opções e três combinações vencedoras."
                else
                    "Cinco opções e dez combinações possíveis.",
                fontSize = 13.sp
            )

            Spacer(modifier = Modifier.height(12.dp))

            LazyColumn(
                modifier = Modifier.weight(1f),
                verticalArrangement = Arrangement.spacedBy(10.dp)
            ) {
                items(
                    items = regras,
                    key = { it.jogada }
                ) { regra ->

                    RegraCard(regra = regra)
                }
            }

            Spacer(modifier = Modifier.height(12.dp))

            Button(
                onClick = {
                    if (abaSelecionada == 0) {
                        nav.navegar(Rota.Jokenpo)
                    } else {
                        nav.navegar(Rota.JokenpoAvancado)
                    }
                },
                modifier = Modifier.fillMaxWidth()
            ) {
                Text(
                    text = if (abaSelecionada == 0)
                        "Jogar Jokenpô Tradicional"
                    else
                        "Jogar Jokenpô Avançado"
                )
            }

            Spacer(modifier = Modifier.height(16.dp))
        }
    }
}

@Composable
private fun RegraCard(
    regra: RegraJogo
) {
    Card(
        modifier = Modifier.fillMaxWidth(),
        colors = CardDefaults.cardColors(
            containerColor = MaterialTheme.colorScheme.surfaceVariant
        ),
        elevation = CardDefaults.cardElevation(2.dp)
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {

            Text(
                text = regra.emoji,
                fontSize = 36.sp
            )

            Column(
                modifier = Modifier
                    .weight(1f)
                    .padding(start = 14.dp)
            ) {

                Text(
                    text = regra.jogada,
                    fontSize = 18.sp,
                    fontWeight = FontWeight.Bold
                )

                Text(
                    text = "Vence: ${regra.venceDe.joinToString(" e ")}",
                    fontSize = 14.sp,
                    color = MaterialTheme.colorScheme.primary,
                    fontWeight = FontWeight.SemiBold
                )

                Text(
                    text = regra.explicacao,
                    fontSize = 13.sp
                )
            }
        }
    }
}