package com.example.jokora.ui.screens

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.compose.ui.platform.LocalContext
import com.example.jokora.viewmodel.TelaInicialViewModel
import com.example.jokora.viewmodel.TelaInicialViewModelFactory
import com.example.jokora.navigation.AcoesNavegacao
import com.example.jokora.navigation.Rota

@Composable
fun TelaInicial(
    nav: AcoesNavegacao,
    modifier: Modifier = Modifier
) {
    val context = LocalContext.current

    val viewModel: TelaInicialViewModel = viewModel(
        factory = TelaInicialViewModelFactory(context)
    )

    val uiState by viewModel.uiState.collectAsState()

    Scaffold(
        modifier = modifier.fillMaxSize()
    ) { padding ->

        LazyColumn(
            modifier = Modifier
                .fillMaxSize()
                .background(MaterialTheme.colorScheme.background)
                .padding(padding)
                .padding(horizontal = 20.dp),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.spacedBy(16.dp)
        ) {

            item {
                Spacer(modifier = Modifier.height(25.dp))

                Text(
                    text = "🖖",
                    fontSize = 65.sp
                )

                Text(
                    text = "JOKORA",
                    fontSize = 34.sp,
                    fontWeight = FontWeight.ExtraBold,
                    color = MaterialTheme.colorScheme.primary
                )

                Text(
                    text = "O clássico desafio com uma nova dimensão!",
                    fontSize = 14.sp,
                    textAlign = TextAlign.Center,
                    color = MaterialTheme.colorScheme.onBackground
                )
            }

            item {
                Card(
                    modifier = Modifier.fillMaxWidth(),
                    colors = CardDefaults.cardColors(
                        containerColor = MaterialTheme.colorScheme.surfaceVariant
                    )
                ) {
                    Column(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(18.dp),
                        horizontalAlignment = Alignment.CenterHorizontally
                    ) {
                        Text(
                            text = if (uiState.possuiJogador)
                                "Olá, ${uiState.nomeJogador}!"
                            else
                                "Olá, jogador!",
                            fontSize = 21.sp,
                            fontWeight = FontWeight.Bold
                        )

                    }
                }
            }

            // Frase do dia: vem da API externa (Retrofit) pelo TelaInicialViewModel
            item {
                FraseDoDia(
                    frase = uiState.frase,
                    carregando = uiState.carregandoFrase,
                    erro = uiState.erroFrase,
                    onNovaFrase = { viewModel.carregarFrase() }
                )
            }

            item {
                Text(
                    text = "Escolha sua modalidade",
                    fontSize = 21.sp,
                    fontWeight = FontWeight.Bold,
                    modifier = Modifier.padding(top = 8.dp)
                )
            }

            item {
                Card(
                    modifier = Modifier.fillMaxWidth(),
                    colors = CardDefaults.cardColors(
                        containerColor = MaterialTheme.colorScheme.surface
                    ),
                    elevation = CardDefaults.cardElevation(4.dp)
                ) {
                    Column(
                        modifier = Modifier.padding(18.dp),
                        horizontalAlignment = Alignment.CenterHorizontally
                    ) {
                        Text(
                            text = "🪨 📄 ✂️",
                            fontSize = 35.sp
                        )

                        Text(
                            text = "Jokenpô Tradicional",
                            fontSize = 19.sp,
                            fontWeight = FontWeight.Bold
                        )

                        Text(
                            text = "Pedra, Papel e Tesoura",
                            fontSize = 14.sp
                        )

                        Spacer(modifier = Modifier.height(12.dp))

                        Button(
                            onClick = {
                                nav.navegar(Rota.Jokenpo)
                            },
                            modifier = Modifier.fillMaxWidth()
                        ) {
                            Text("Jogar tradicional")
                        }
                    }
                }
            }

            item {
                Card(
                    modifier = Modifier.fillMaxWidth(),
                    colors = CardDefaults.cardColors(
                        containerColor = MaterialTheme.colorScheme.surface
                    ),
                    elevation = CardDefaults.cardElevation(4.dp)
                ) {
                    Column(
                        modifier = Modifier.padding(18.dp),
                        horizontalAlignment = Alignment.CenterHorizontally
                    ) {
                        Text(
                            text = "🪨 📄 ✂️ 🦎 🖖",
                            fontSize = 30.sp
                        )

                        Text(
                            text = "Jokenpô Avançado",
                            fontSize = 19.sp,
                            fontWeight = FontWeight.Bold
                        )

                        Text(
                            text = "Pedra, Papel, Tesoura, Lagarto e Spock",
                            fontSize = 13.sp,
                            textAlign = TextAlign.Center
                        )

                        Spacer(modifier = Modifier.height(12.dp))

                        Button(
                            onClick = { nav.navegar(Rota.JokenpoAvancado) },
                            modifier = Modifier.fillMaxWidth()
                        ) {
                            Text("Jogar avançado")
                        }
                    }
                }
            }

            item {
                OutlinedButton(
                    onClick = { nav.navegar(Rota.Regras) },
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Text("📖  Conhecer as regras")
                }
            }

            item {
                OutlinedButton(
                    onClick = { nav.navegar(Rota.Perfil) },
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Text("👤  Meu perfil")
                }
            }

            // Controle de perfil: o botão só existe para o administrador
            if (uiState.isAdmin) {
                item {
                    Button(
                        onClick = { nav.navegar(Rota.Admin) },
                        modifier = Modifier.fillMaxWidth(),
                        colors = ButtonDefaults.buttonColors(
                            containerColor = MaterialTheme.colorScheme.tertiary
                        )
                    ) {
                        Text("⚙️  Área administrativa")
                    }
                }
            }

            item {
                Spacer(modifier = Modifier.height(20.dp))

                Text(
                    text = "Jokora • Divirta-se e desafie seus limites!",
                    fontSize = 12.sp,
                    color = MaterialTheme.colorScheme.onBackground.copy(alpha = 0.6f),
                    textAlign = TextAlign.Center
                )

                Spacer(modifier = Modifier.height(20.dp))
            }
        }
    }
}

/** Cartão com a frase do dia buscada na API (com estados de carregando e erro). */
@Composable
private fun FraseDoDia(
    frase: String?,
    carregando: Boolean,
    erro: String?,
    onNovaFrase: () -> Unit
) {
    Card(
        modifier = Modifier.fillMaxWidth(),
        colors = CardDefaults.cardColors(
            containerColor = MaterialTheme.colorScheme.secondaryContainer
        )
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Text(
                text = "💡 Conselho do dia",
                fontWeight = FontWeight.Bold
            )

            Spacer(modifier = Modifier.height(8.dp))

            when {
                carregando -> CircularProgressIndicator(modifier = Modifier.size(28.dp))
                erro != null -> Text(
                    text = erro,
                    color = MaterialTheme.colorScheme.error,
                    textAlign = TextAlign.Center
                )
                frase != null -> Text(
                    text = "\"$frase\"",
                    textAlign = TextAlign.Center,
                    fontSize = 15.sp
                )
            }

            TextButton(onClick = onNovaFrase, enabled = !carregando) {
                Text(if (erro != null) "Tentar novamente" else "Nova frase")
            }
        }
    }
}
