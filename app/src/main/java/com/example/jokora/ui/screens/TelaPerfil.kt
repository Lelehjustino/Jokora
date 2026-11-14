package com.example.jokora.ui.screens

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.Button
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Person
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.lifecycle.viewmodel.compose.viewModel
import com.example.jokora.data.entity.PartidaEntity
import com.example.jokora.data.repository.Placar
import com.example.jokora.model.Modalidade
import com.example.jokora.model.Resultado
import com.example.jokora.navigation.AcoesNavegacao
import com.example.jokora.viewmodel.TelaPerfilViewModel
import com.example.jokora.viewmodel.TelaPerfilViewModelFactory

/** Aba Perfil: cria/troca o perfil do jogador e mostra suas estatísticas e partidas. */
@Composable
fun TelaPerfil(
    nav: AcoesNavegacao,
    modifier: Modifier = Modifier
) {
    val context = LocalContext.current
    val viewModel: TelaPerfilViewModel = viewModel(
        factory = TelaPerfilViewModelFactory(context)
    )
    val uiState by viewModel.uiState.collectAsStateWithLifecycle()
    var mostrarEstatisticasAvancadas by rememberSaveable { mutableStateOf(false) }

    val partidasEstatisticas = if (mostrarEstatisticasAvancadas) {
        uiState.partidas.filter { it.modalidade == Modalidade.AVANCADO }
    } else {
        uiState.partidas
    }
    val placarExibido = Placar(
        vitorias = partidasEstatisticas.count { it.resultado == Resultado.VITORIA },
        derrotas = partidasEstatisticas.count { it.resultado == Resultado.DERROTA },
        empates = partidasEstatisticas.count { it.resultado == Resultado.EMPATE }
    )

    Column(
        modifier = modifier
            .fillMaxSize()
            .verticalScroll(rememberScrollState())
            .padding(horizontal = 20.dp, vertical = 16.dp),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.spacedBy(16.dp)
    ) {
        Box(modifier = Modifier.fillMaxWidth()) {
            TextButton(
                onClick = { nav.voltar() },
                modifier = Modifier.align(Alignment.CenterStart)
            ) {
                Text("← Voltar")
            }
            Text(
                text = "Perfil",
                style = MaterialTheme.typography.headlineMedium,
                fontWeight = FontWeight.Bold,
                modifier = Modifier.align(Alignment.Center)
            )
        }

        if (!uiState.possuiPerfil) {
            CriarPerfil(
                erro = uiState.mensagem,
                onCriar = { nome -> viewModel.criarPerfil(nome) }
            )
        } else {
            CabecalhoPerfil(nome = uiState.nome)

            Estatisticas(
                placar = placarExibido,
                avancadas = mostrarEstatisticasAvancadas,
                onAlternarModo = {
                    mostrarEstatisticasAvancadas = !mostrarEstatisticasAvancadas
                }
            )

            HistoricoPerfil(partidas = uiState.partidas)

            OutlinedButton(
                onClick = { viewModel.trocarPerfil() },
                modifier = Modifier.fillMaxWidth()
            ) {
                Text("Trocar perfil")
            }

        }
    }
}

/** Formulário simples para criar ou selecionar um jogador pelo nome. */
@Composable
private fun CriarPerfil(
    erro: String?,
    onCriar: (nome: String) -> Unit
) {
    var nome by rememberSaveable { mutableStateOf("") }

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
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.spacedBy(12.dp)
        ) {
            Text(text = "👤", fontSize = 48.sp)
            Text(
                text = "Crie seu perfil",
                style = MaterialTheme.typography.titleLarge,
                fontWeight = FontWeight.Bold
            )
            Text(
                text = "Suas partidas ficam salvas no seu nome.",
                style = MaterialTheme.typography.bodyMedium,
                textAlign = TextAlign.Center
            )

            OutlinedTextField(
                value = nome,
                onValueChange = { nome = it },
                label = { Text("Nome do jogador") },
                singleLine = true,
                isError = erro != null,
                supportingText = if (erro != null) {
                    { Text(erro) }
                } else {
                    null
                },
                modifier = Modifier.fillMaxWidth()
            )

            Button(
                onClick = { onCriar(nome) },
                enabled = nome.isNotBlank(),
                modifier = Modifier.fillMaxWidth()
            ) {
                Text("Entrar / Criar perfil")
            }
        }
    }
}

/** Avatar com a inicial do nome, o nome e o tipo de perfil. */
@Composable
private fun CabecalhoPerfil(nome: String) {
    Column(
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.spacedBy(6.dp)
    ) {
        Box(
            modifier = Modifier
                .size(88.dp)
                .background(MaterialTheme.colorScheme.primary, CircleShape),
            contentAlignment = Alignment.Center
        ) {
            Icon(
                imageVector = Icons.Filled.Person,
                contentDescription = "Foto de perfil",
                tint = MaterialTheme.colorScheme.onPrimary,
                modifier = Modifier.size(52.dp)
            )
        }
        Text(
            text = nome,
            style = MaterialTheme.typography.titleLarge,
            fontWeight = FontWeight.Bold
        )
    }
}

/**
 * Exibe as estatísticas gerais ou somente as partidas do modo avançado.
 * O cartão mantém o mesmo tamanho e alterna o conteúdo pelo botão de estrela.
 */
@Composable
private fun Estatisticas(
    placar: Placar,
    avancadas: Boolean,
    onAlternarModo: () -> Unit
) {
    Card(
        modifier = Modifier.fillMaxWidth(),
        colors = CardDefaults.cardColors(
            containerColor = MaterialTheme.colorScheme.surfaceVariant
        )
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.spacedBy(10.dp)
        ) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(
                    text = if (avancadas) "Minhas estatísticas avançadas" else "Minhas estatísticas",
                    style = MaterialTheme.typography.titleMedium,
                    fontWeight = FontWeight.Bold,
                    modifier = Modifier.weight(1f)
                )
                TextButton(onClick = onAlternarModo) {
                    Text(
                        text = if (avancadas) "☆" else "★",
                        fontSize = 25.sp,
                        color = MaterialTheme.colorScheme.primary
                    )
                }
            }
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceEvenly
            ) {
                NumeroEstatistica(valor = placar.vitorias.toString(), legenda = "Vitórias")
                NumeroEstatistica(valor = placar.empates.toString(), legenda = "Empates")
                NumeroEstatistica(valor = placar.derrotas.toString(), legenda = "Derrotas")
            }
            Text(
                text = "${placar.total} partida(s) • " +
                    "${(placar.aproveitamento * 100).toInt()}% de aproveitamento",
                style = MaterialTheme.typography.bodySmall
            )
        }
    }
}

@Composable
private fun HistoricoPerfil(partidas: List<PartidaEntity>) {
    Card(
        modifier = Modifier.fillMaxWidth(),
        colors = CardDefaults.cardColors(
            containerColor = MaterialTheme.colorScheme.surface
        )
    ) {
        Column(
            modifier = Modifier.fillMaxWidth().padding(16.dp),
            verticalArrangement = Arrangement.spacedBy(10.dp)
        ) {
            Text(
                text = "Histórico de partidas",
                style = MaterialTheme.typography.titleMedium,
                fontWeight = FontWeight.Bold
            )

            if (partidas.isEmpty()) {
                Text(
                    text = "Você ainda não jogou nenhuma partida.",
                    style = MaterialTheme.typography.bodyMedium,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )
            } else {
                partidas.forEachIndexed { index, partida ->
                    if (index > 0) HorizontalDivider()
                    Row(
                        modifier = Modifier.fillMaxWidth().padding(vertical = 5.dp),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Column(modifier = Modifier.weight(1f)) {
                            Text(
                                text = "${partida.jogadaJogador.rotulo} × ${partida.jogadaOponente.rotulo}",
                                fontWeight = FontWeight.SemiBold
                            )
                            Text(
                                text = "${partida.modalidade.rotulo} • ${java.text.SimpleDateFormat("dd/MM/yyyy 'às' HH:mm", java.util.Locale("pt", "BR")).format(java.util.Date(partida.dataHora))}",
                                style = MaterialTheme.typography.bodySmall,
                                color = MaterialTheme.colorScheme.onSurfaceVariant
                            )
                        }
                        Text(
                            text = when (partida.resultado) {
                                com.example.jokora.model.Resultado.VITORIA -> "Vitória"
                                com.example.jokora.model.Resultado.DERROTA -> "Derrota"
                                com.example.jokora.model.Resultado.EMPATE -> "Empate"
                            },
                            fontWeight = FontWeight.Bold,
                            color = when (partida.resultado) {
                                com.example.jokora.model.Resultado.VITORIA -> MaterialTheme.colorScheme.primary
                                com.example.jokora.model.Resultado.DERROTA -> MaterialTheme.colorScheme.error
                                com.example.jokora.model.Resultado.EMPATE -> MaterialTheme.colorScheme.secondary
                            }
                        )
                    }
                }
            }
        }
    }
}

@Composable
private fun NumeroEstatistica(valor: String, legenda: String) {
    Column(horizontalAlignment = Alignment.CenterHorizontally) {
        Text(
            text = valor,
            style = MaterialTheme.typography.headlineMedium,
            fontWeight = FontWeight.Bold
        )
        Text(text = legenda, style = MaterialTheme.typography.bodySmall)
    }
}