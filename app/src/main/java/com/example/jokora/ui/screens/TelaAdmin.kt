package com.example.jokora.ui.screens

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.itemsIndexed
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material.icons.filled.Edit
import androidx.compose.material.icons.filled.Refresh
import androidx.compose.material.icons.filled.Search
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.lifecycle.viewmodel.compose.viewModel
import com.example.jokora.navigation.AcoesNavegacao
import com.example.jokora.viewmodel.JogadorResumo
import com.example.jokora.viewmodel.TelaAdminViewModel
import com.example.jokora.viewmodel.TelaAdminViewModelFactory

@Composable
fun TelaAdmin(
    nav: AcoesNavegacao,
    modifier: Modifier = Modifier
) {
    val context = LocalContext.current
    val viewModel: TelaAdminViewModel = viewModel(
        factory = TelaAdminViewModelFactory(context)
    )
    val uiState by viewModel.uiState.collectAsStateWithLifecycle()

    var jogadorParaRenomear by remember { mutableStateOf<JogadorResumo?>(null) }
    var jogadorParaExcluir by remember { mutableStateOf<JogadorResumo?>(null) }
    var jogadorParaZerar by remember { mutableStateOf<JogadorResumo?>(null) }

    Column(
        modifier = modifier
            .fillMaxSize()
            .padding(horizontal = 20.dp, vertical = 16.dp),
        verticalArrangement = Arrangement.spacedBy(12.dp)
    ) {
        Box(modifier = Modifier.fillMaxWidth()) {
            TextButton(
                onClick = { nav.voltar() },
                modifier = Modifier.align(Alignment.CenterStart)
            ) {
                Text("← Voltar")
            }
            Text(
                text = "Admin",
                style = MaterialTheme.typography.headlineMedium,
                fontWeight = FontWeight.Bold,
                modifier = Modifier.align(Alignment.Center)
            )
        }

        Text(
            text = "Ranking de jogadores • ${uiState.totalJogadores} cadastrado(s)",
            style = MaterialTheme.typography.bodyMedium,
            color = MaterialTheme.colorScheme.onSurfaceVariant
        )

        OutlinedTextField(
            value = uiState.busca,
            onValueChange = { viewModel.atualizarBusca(it) },
            label = { Text("Buscar jogador") },
            leadingIcon = { Icon(Icons.Filled.Search, contentDescription = null) },
            singleLine = true,
            modifier = Modifier.fillMaxWidth()
        )

        uiState.mensagem?.let { texto ->
            Text(text = texto, color = MaterialTheme.colorScheme.primary)
        }
        uiState.erro?.let { texto ->
            Text(text = texto, color = MaterialTheme.colorScheme.error)
        }

        if (uiState.jogadores.isEmpty()) {
            Text(
                text = if (uiState.busca.isBlank()) "Nenhum jogador cadastrado ainda."
                else "Nenhum jogador encontrado para \"${uiState.busca}\".",
                textAlign = TextAlign.Center,
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(top = 24.dp)
            )
        } else {
            LazyColumn(
                modifier = Modifier.weight(1f),
                verticalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                itemsIndexed(uiState.jogadores, key = { _, jogador -> jogador.id }) { indice, jogador ->
                    CartaoJogador(
                        posicao = indice + 1,
                        jogador = jogador,
                        onRenomear = { jogadorParaRenomear = jogador },
                        onZerar = { jogadorParaZerar = jogador },
                        onExcluir = { jogadorParaExcluir = jogador }
                    )
                }
            }
        }
    }

    jogadorParaRenomear?.let { jogador ->
        DialogoRenomear(
            nomeAtual = jogador.nome,
            onConfirmar = { novoNome ->
                viewModel.renomearJogador(jogador.id, novoNome)
                jogadorParaRenomear = null
            },
            onCancelar = { jogadorParaRenomear = null }
        )
    }

    jogadorParaZerar?.let { jogador ->
        DialogoConfirmacao(
            titulo = "Zerar histórico",
            texto = "Apagar as ${jogador.partidas} partida(s) de ${jogador.nome}? O jogador continua cadastrado.",
            textoConfirmar = "Zerar",
            onConfirmar = {
                viewModel.zerarHistorico(jogador.id)
                jogadorParaZerar = null
            },
            onCancelar = { jogadorParaZerar = null }
        )
    }

    jogadorParaExcluir?.let { jogador ->
        DialogoConfirmacao(
            titulo = "Excluir jogador",
            texto = "Excluir ${jogador.nome} e todo o histórico de partidas? Essa ação não pode ser desfeita.",
            textoConfirmar = "Excluir",
            onConfirmar = {
                viewModel.excluirJogador(jogador.id)
                jogadorParaExcluir = null
            },
            onCancelar = { jogadorParaExcluir = null }
        )
    }
}

@Composable
private fun CartaoJogador(
    posicao: Int,
    jogador: JogadorResumo,
    onRenomear: () -> Unit,
    onZerar: () -> Unit,
    onExcluir: () -> Unit
) {
    Card(
        modifier = Modifier.fillMaxWidth(),
        colors = CardDefaults.cardColors(
            containerColor = MaterialTheme.colorScheme.surfaceVariant
        )
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(start = 14.dp, end = 4.dp, top = 8.dp, bottom = 8.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Text(
                text = "$posicao º",
                style = MaterialTheme.typography.titleMedium,
                fontWeight = FontWeight.Bold,
                modifier = Modifier.width(40.dp)
            )
            Column(modifier = Modifier.weight(1f)) {
                Text(text = jogador.nome, fontWeight = FontWeight.SemiBold)
                Text(
                    text = "${jogador.vitorias} vitória(s) • ${jogador.partidas} partida(s)",
                    style = MaterialTheme.typography.bodySmall
                )
            }
            IconButton(onClick = onRenomear) {
                Icon(Icons.Filled.Edit, contentDescription = "Renomear ${jogador.nome}")
            }
            IconButton(onClick = onZerar) {
                Icon(Icons.Filled.Refresh, contentDescription = "Zerar histórico de ${jogador.nome}")
            }
            IconButton(onClick = onExcluir) {
                Icon(
                    Icons.Filled.Delete,
                    contentDescription = "Excluir ${jogador.nome}",
                    tint = MaterialTheme.colorScheme.error
                )
            }
        }
    }
}

@Composable
private fun DialogoRenomear(
    nomeAtual: String,
    onConfirmar: (String) -> Unit,
    onCancelar: () -> Unit
) {
    var novoNome by remember { mutableStateOf(nomeAtual) }

    AlertDialog(
        onDismissRequest = onCancelar,
        title = { Text("Renomear jogador") },
        text = {
            OutlinedTextField(
                value = novoNome,
                onValueChange = { novoNome = it },
                label = { Text("Novo nome") },
                singleLine = true
            )
        },
        confirmButton = {
            TextButton(
                onClick = { onConfirmar(novoNome) },
                enabled = novoNome.isNotBlank() && novoNome.trim() != nomeAtual
            ) {
                Text("Salvar")
            }
        },
        dismissButton = {
            TextButton(onClick = onCancelar) { Text("Cancelar") }
        }
    )
}

@Composable
private fun DialogoConfirmacao(
    titulo: String,
    texto: String,
    textoConfirmar: String,
    onConfirmar: () -> Unit,
    onCancelar: () -> Unit
) {
    AlertDialog(
        onDismissRequest = onCancelar,
        title = { Text(titulo) },
        text = { Text(texto) },
        confirmButton = {
            TextButton(onClick = onConfirmar) {
                Text(textoConfirmar, color = MaterialTheme.colorScheme.error)
            }
        },
        dismissButton = {
            TextButton(onClick = onCancelar) { Text("Cancelar") }
        }
    )
}
