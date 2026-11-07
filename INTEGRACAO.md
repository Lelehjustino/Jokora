# Jokora — Guia de integração para as integrantes

Arquitetura (MVVM): **UI (ui/screens) → ViewModel (viewmodel) → Repository → DAO → Room**.
ViewModels recebem **apenas Repositories**. Nunca use `JokenpoDatabase` ou DAOs fora de `data/`.

## 1. Abrir o projeto

1. Clone o repositório (ou descompacte o zip) em `AndroidStudioProjects`.
2. Android Studio > **File > Open** > pasta `Jokora` > aguarde o **Gradle Sync**.
3. **Build > Rebuild Project** e rode no emulador. O app abre no Início com
   "tela ainda não integrada" e a barra inferior com 5 abas.

```
app/src/main/java/com/example/jokora/
├── MainActivity.kt              JokoraTheme { JokoraApp() }
├── data/                        PRONTO — não alterar sem combinar com o grupo
│   ├── database/  JokenpoDatabase.kt (+ JokenpoConverters) · DatabaseProvider.kt
│   ├── dao/       JogadorDao.kt · PartidaDao.kt
│   ├── entity/    JogadorEntity.kt (+ JogadorComPartidas) · PartidaEntity.kt
│   └── repository/ JogadorRepository.kt (+ JokenpoException) · PartidaRepository.kt (+ Placar)
├── model/         Jogada.kt · Resultado.kt · Modalidade.kt · Perfil.kt   PRONTO
├── navigation/    NavGraph.kt · Rotas.kt · BottomNavigation.kt          PRONTO
├── ui/
│   ├── screens/     ← TelaInicial.kt, TelaRegras.kt, TelaJokenpo.kt, TelaJokenpoAvancado.kt…
│   ├── components/  ← componentes visuais reutilizáveis (botões, cards…)
│   └── theme/       Theme.kt (Material 3 padrão — o grupo define a identidade visual)
└── viewmodel/       ← JokenpoViewModel.kt, HistoricoViewModel.kt… + seus UiStates
```

Os arquivos `.gitkeep` só existem para as pastas vazias irem para o Git; podem apagar
quando colocarem o primeiro arquivo na pasta.

## 2. Como cada integrante conecta sua tela

1. Crie a tela em `ui/screens/` com esta assinatura:
```kotlin
package com.example.jokora.ui.screens

@Composable
fun TelaJokenpo(nav: AcoesNavegacao) { … }
```
2. Em `navigation/NavGraph.kt`, troque **só a sua linha** em `TelasDoApp`:
```kotlin
val jokenpo: @Composable (AcoesNavegacao) -> Unit = { nav -> TelaJokenpo(nav) },
```
3. Para ir a outra tela: `nav.navegar(Rota.Regras)`. Para voltar: `nav.voltar()`.

| Integrante | Tela | Slot em TelasDoApp | Rota |
|---|---|---|---|
| 1 | TelaInicial | `inicial` | `Rota.Inicial` (tela de partida) |
| 2 | TelaRegras | `regras` | `Rota.Regras` |
| 3 | TelaJokenpo | `jokenpo` | `Rota.Jokenpo` |
| 4 | TelaJokenpoAvancado | `jokenpoAvancado` | `Rota.JokenpoAvancado` |
| a definir | TelaHistorico | `historico` | `Rota.Historico` |
| a definir | TelaAdmin | `admin` | `Rota.Admin` (só perfil ADMIN) |

Cada uma mexe em uma linha diferente do NavGraph, então não há conflito no Git.

## 3. Modelo de ViewModel (regra de negócio visível no ViewModel)

O README exige que **os ViewModels apliquem as regras de negócio**. Por isso o ViewModel
sorteia a jogada do computador e calcula o resultado; o Repository só grava.

```kotlin
package com.example.jokora.viewmodel

data class JokenpoUiState(
    val jogadas: List<Jogada> = Modalidade.TRADICIONAL.jogadas,
    val jogadaJogador: Jogada? = null,
    val jogadaComputador: Jogada? = null,
    val resultado: Resultado? = null,
    val descricao: String = "",          // ex.: "Pedra quebra Tesoura"
    val placar: Placar = Placar(),
    val erro: String? = null
)

class JokenpoViewModel(
    private val partidaRepository: PartidaRepository,
    private val jogadorRepository: JogadorRepository,
    private val modalidade: Modalidade = Modalidade.TRADICIONAL   // AVANCADO na outra tela
) : ViewModel() {

    private val _uiState = MutableStateFlow(JokenpoUiState(jogadas = modalidade.jogadas))
    val uiState: StateFlow<JokenpoUiState> = _uiState.asStateFlow()

    fun jogar(jogada: Jogada) {
        val jogador = jogadorRepository.jogadorAtual.value
        if (jogador == null) {
            _uiState.update { it.copy(erro = "Entre com um jogador primeiro.") }
            return
        }

        // Regras de negócio no ViewModel
        val computador = modalidade.jogadaAleatoria()
        val resultado = Resultado.calcular(jogada, computador, modalidade)
        val descricao = when (resultado) {
            Resultado.VITORIA -> jogada.descreverVitoria(computador)
            Resultado.DERROTA -> computador.descreverVitoria(jogada)
            Resultado.EMPATE -> "Empate!"
        } ?: ""

        _uiState.update {
            it.copy(jogadaJogador = jogada, jogadaComputador = computador,
                    resultado = resultado, descricao = descricao, erro = null)
        }

        viewModelScope.launch {
            partidaRepository.registrarPartida(jogador.id, modalidade, jogada, computador)
                .onFailure { e -> _uiState.update { it.copy(erro = e.message) } }
        }
    }
}
```

Na tela (sem Factory):
```kotlin
val context = LocalContext.current
val vm: JokenpoViewModel = viewModel {
    JokenpoViewModel(
        DatabaseProvider.partidaRepository(context),
        DatabaseProvider.jogadorRepository(context),
        Modalidade.TRADICIONAL
    )
}
val estado by vm.uiState.collectAsStateWithLifecycle()
```

## 4. Requisitos do README que ficam nas telas/ViewModels

**LazyColumn** — TelaHistorico:
```kotlin
LazyColumn {
    items(estado.partidas, key = { it.id }) { partida -> /* item visual */ }
}
```

**map, filter e sortedBy com lambdas** — no HistoricoViewModel, por exemplo:
```kotlin
val partidas: StateFlow<List<PartidaEntity>> =
    partidaRepository.historicoDoJogador(jogadorId)
        .map { lista ->
            lista.filter { filtro == null || it.modalidade == filtro }   // filter
                 .sortedBy { it.dataHora }                               // sortedBy
        }
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5_000), emptyList())

val linhas = partidas.value.map { "${it.jogadaJogador.rotulo} x ${it.jogadaOponente.rotulo}" } // map
```
Ranking na TelaAdmin: `listarJogadoresComPartidas()` +
`sortedByDescending { j -> j.partidas.count { it.resultado == Resultado.VITORIA } }`.

**Controle de acesso por perfil** — já pronto: o item Admin só aparece para `Perfil.ADMIN`
e a rota redireciona para o Início se alguém sem permissão tentar abrir.

## 5. O que a infraestrutura já oferece

**JogadorRepository**
- `jogadorAtual: StateFlow<JogadorEntity?>`, `selecionarJogador()`, `sair()` — sessão do app
- `entrarOuRegistrar(nome)` — ideal para a TelaInicial
- `registrarJogador(nome, perfil)`, `atualizarJogador()`, `alterarPerfil()`, `excluirJogador()`
- `listarJogadores()`, `observarJogador(id)`, `listarJogadoresComPartidas()`, `observarJogadorComPartidas(id)` — `Flow`

**PartidaRepository**
- `registrarPartida(jogadorId, modalidade, jogadaJogador, jogadaOponente)` — valida e grava
- `jogarContraComputador(...)` — atalho que sorteia e grava (prefiram o modelo da seção 3)
- `historicoGeral()`, `historicoDoJogador(id)`, `historicoDoJogador(id, modalidade)`, `placar(id, modalidade?)` — `Flow`
- `excluirPartida()`, `limparHistoricoDoJogador()`, `limparTodoHistorico()` — área admin

**Models**: `Jogada.venceDe()`, `Jogada.descreverVitoria()` ("Spock vaporiza Pedra"),
`Modalidade.jogadas`, `Modalidade.jogadaAleatoria()`, `Resultado.calcular()`, `Perfil.podeAcessarAdmin`.

Escritas devolvem `Result<T>`; erros chegam como `JokenpoException` com mensagem em português.

## 6. Decisões para o grupo

- **Primeiro administrador**: definam como é criado (ex.: na TelaInicial, um nome reservado
  chama `registrarJogador(nome, Perfil.ADMIN)`).
- **TelaHistorico e TelaAdmin**: definam quem desenvolve.
- Ao alterar qualquer Entity, subam `version` em `JokenpoDatabase` (o banco é recriado).

## 7. Checklist antes de entregar

1. **Build > Rebuild Project** sem erros.
2. **App Inspection > Database Inspector**: tabelas `jogadores` e `partidas` com dados reais.
3. Navegar pelas 6 telas, inclusive Admin com um usuário ADMIN.
4. Preencher o link do APK no README.
