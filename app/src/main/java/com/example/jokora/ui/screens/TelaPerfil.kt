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
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Person
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.lifecycle.viewmodel.compose.viewModel
import com.example.jokora.data.entity.PartidaEntity
import com.example.jokora.data.repository.JogadorRepository
import com.example.jokora.data.repository.Placar
import com.example.jokora.model.Resultado
import com.example.jokora.navigation.AcoesNavegacao
import com.example.jokora.navigation.Rota
import com.example.jokora.viewmodel.TelaPerfilViewModel
import com.example.jokora.viewmodel.TelaPerfilViewModelFactory
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

/**
 * Aba Perfil: cria/troca o perfil, faz o login de administrador
 * e mostra as estatísticas e o histórico de partidas (LazyColumn).
 */
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

    LazyColumn(
        modifier = modifier
            .fillMaxSize()
            .padding(horizontal = 20.dp),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.spacedBy(16.dp)
    ) {
        item {
            Box(modifier = Modifier.fillMaxWidth().padding(top = 16.dp)) {
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
        }

        if (!uiState.possuiPerfil) {
            item {
                CriarPerfil(
                    erro = uiState.mensagem,
                    onCriar = { nome -> viewModel.criarPerfil(nome) },
                    onEntrarAdmin = { senha -> viewModel.entrarComoAdmin(senha) }
                )
            }
        } else {
            item {
                CabecalhoPerfil(nome = uiState.nome, tipoPerfil = uiState.tipoPerfil)
            }

            // Controle de perfil: atalho para a área restrita só aparece para ADMIN
            if (uiState.isAdmin) {
                item {
                    Button(
                        onClick = { nav.navegar(Rota.Admin) },
                        modifier = Modifier.fillMaxWidth(),
                        colors = ButtonDefaults.buttonColors(
                            containerColor = MaterialTheme.colorScheme.tertiary
                        )
                    ) {
                        Text("⚙️  Gerenciar jogadores")
                    }
                }
            }

            item {
                Estatisticas(
                    placar = uiState.placar,
                    avancadas = uiState.apenasAvancado,
                    onAlternarModo = { viewModel.alternarEstatisticas() }
                )
            }

            item {
                Text(
                    text = "Histórico de partidas",
                    style = MaterialTheme.typography.titleMedium,
                    fontWeight = FontWeight.Bold,
                    modifier = Modifier.fillMaxWidth()
                )
            }

            if (uiState.partidas.isEmpty()) {
                item {
                    Text(
                        text = "Você ainda não jogou nenhuma partida.",
                        style = MaterialTheme.typography.bodyMedium,
                        color = MaterialTheme.colorScheme.onSurfaceVariant,
                        modifier = Modifier.fillMaxWidth()
                    )
                }
            } else {
                items(uiState.partidas, key = { it.id }) { partida ->
                    LinhaPartida(partida = partida)
                    HorizontalDivider()
                }
            }

            item {
                OutlinedButton(
                    onClick = { viewModel.trocarPerfil() },
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(bottom = 16.dp)
                ) {
                    Text("Trocar perfil")
                }
            }
        }
    }
}

/** Formulário para criar/selecionar um jogador pelo nome ou entrar como administrador. */
@Composable
private fun CriarPerfil(
    erro: String?,
    onCriar: (nome: String) -> Unit,
    onEntrarAdmin: (senha: String) -> Unit
) {
    var nome by rememberSaveable { mutableStateOf("") }
    var senha by rememberSaveable { mutableStateOf("") }
    var modoAdmin by rememberSaveable { mutableStateOf(false) }

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
            Text(text = if (modoAdmin) "🔒" else "👤", fontSize = 48.sp)
            Text(
                text = if (modoAdmin) "Login do administrador" else "Crie seu perfil",
                style = MaterialTheme.typography.titleLarge,
                fontWeight = FontWeight.Bold
            )

            if (modoAdmin) {
                Text(
                    text = "Senha de demonstração: ${JogadorRepository.SENHA_ADMIN}",
                    style = MaterialTheme.typography.bodySmall,
                    textAlign = TextAlign.Center
                )

                OutlinedTextField(
                    value = senha,
                    onValueChange = { senha = it },
                    label = { Text("Senha") },
                    singleLine = true,
                    visualTransformation = PasswordVisualTransformation(),
                    isError = erro != null,
                    supportingText = if (erro != null) {
                        { Text(erro) }
                    } else {
                        null
                    },
                    modifier = Modifier.fillMaxWidth()
                )

                Button(
                    onClick = { onEntrarAdmin(senha) },
                    enabled = senha.isNotBlank(),
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Text("Entrar como administrador")
                }
            } else {
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

            TextButton(onClick = { modoAdmin = !modoAdmin }) {
                Text(if (modoAdmin) "Voltar para o perfil de jogador" else "Sou administrador")
            }
        }
    }
}

/** Avatar, nome e tipo de perfil (Jogador ou Administrador). */
@Composable
private fun CabecalhoPerfil(nome: String, tipoPerfil: String) {
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
        Text(
            text = tipoPerfil,
            color = MaterialTheme.colorScheme.onSecondaryContainer,
            style = MaterialTheme.typography.labelLarge,
            modifier = Modifier
                .background(MaterialTheme.colorScheme.secondaryContainer, CircleShape)
                .padding(horizontal = 14.dp, vertical = 4.dp)
        )
    }
}

/**
 * Exibe as estatísticas gerais ou somente as partidas do modo avançado.
 * O filtro é feito no TelaPerfilViewModel; a tela só chama [onAlternarModo].
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

/** Uma linha do histórico (item da LazyColumn). */
@Composable
private fun LinhaPartida(partida: PartidaEntity) {
    val formatoData = SimpleDateFormat("dd/MM/yyyy 'às' HH:mm", Locale("pt", "BR"))

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
                text = "${partida.modalidade.rotulo} • ${formatoData.format(Date(partida.dataHora))}",
                style = MaterialTheme.typography.bodySmall,
                color = MaterialTheme.colorScheme.onSurfaceVariant
            )
        }
        Text(
            text = partida.resultado.rotulo,
            fontWeight = FontWeight.Bold,
            color = when (partida.resultado) {
                Resultado.VITORIA -> MaterialTheme.colorScheme.primary
                Resultado.DERROTA -> MaterialTheme.colorScheme.error
                Resultado.EMPATE -> MaterialTheme.colorScheme.secondary
            }
        )
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
