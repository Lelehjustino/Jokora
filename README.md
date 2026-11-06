# Jokora — Jogo de Jokenpô
<!--  **[⬇️ Download do APK]()**  -->

## Sobre o projeto

O **Jokora** é um aplicativo mobile Android desenvolvido em Kotlin e Jetpack Compose, inspirado no clássico jogo Jokenpô. O aplicativo permite que os usuários disputem partidas contra o computador em duas modalidades: a tradicional, Pedra, Papel e Tesoura, e a avançada, Pedra, Papel, Tesoura, Lagarto e Spock.

O projeto foi desenvolvido como parte de um trabalho acadêmico, com o objetivo de aplicar conceitos de desenvolvimento mobile, arquitetura de software, navegação entre telas, gerenciamento de estado e persistência de dados.

<!--## 🎮 Conheça as regras do Jokenpô Avançado

Aprenda como funciona a modalidade Pedra, Papel, Tesoura, Lagarto e Spock:

[![Regras do Pedra, Papel, Tesoura, Lagarto e Spock](https://img.youtube.com/vi/ID_DO_VIDEO/hqdefault.jpg)](https://www.youtube.com/watch?v=https://www.youtube.com/shorts/EHrb492R-Yk)

*Clique na imagem para assistir ao vídeo.*  -->
## Funcionalidades

- **Tela inicial:** apresenta o aplicativo e permite acessar as modalidades de jogo e demais funcionalidades.
- **Jokenpô tradicional:** permite jogar Pedra, Papel e Tesoura contra o computador.
- **Jokenpô avançado:** adiciona as opções Lagarto e Spock à dinâmica tradicional.
- **Regras:** apresenta as regras e combinações vencedoras de cada modalidade.
- **Histórico:** permite consultar as partidas realizadas e seus resultados.
- **Área administrativa:** disponibiliza funcionalidades restritas aos usuários com perfil de administrador.

## Requisitos do trabalho

O desenvolvimento do Jokora contempla os seguintes requisitos:

- Aplicativo Android desenvolvido com Kotlin e Jetpack Compose.
- Arquitetura MVVM, separando interface, lógica de negócio e acesso aos dados.
- Navegação entre, no mínimo, seis telas.
- Utilização de Bottom Navigation Bar para navegação entre as principais áreas do aplicativo.
- Utilização de ViewModels para gerenciamento de estado e aplicação das regras de negócio.
- Utilização de listas com LazyColumn ou componentes equivalentes.
- Processamento de listas utilizando funções como `map`, `filter` e `sortedBy`, com expressões lambda.
- Controle de acesso conforme o perfil do usuário, diferenciando jogador e administrador.
- Persistência de dados utilizando Room Database.

## Telas do aplicativo

| Tela | Descrição |
|---|---|
| TelaInicial | Página principal e acesso às funcionalidades. |
| TelaRegras | Explicação das regras das duas modalidades. |
| TelaJokenpo | Partida na modalidade tradicional. |
| TelaJokenpoAvancado | Partida na modalidade avançada. |
| TelaHistorico | Consulta das partidas realizadas. |
| TelaAdmin | Área de acesso restrito ao administrador. |

## Tecnologias utilizadas

- **Kotlin:** linguagem de programação.
- **Jetpack Compose:** construção das interfaces.
- **Android Studio:** ambiente de desenvolvimento.
- **MVVM:** padrão arquitetural para organização do projeto.
- **Room Database:** persistência local dos dados.
- **Navigation Compose:** gerenciamento da navegação entre telas.
- **ViewModel:** gerenciamento de estados e lógica de apresentação.
- **Kotlin Flow:** observação e atualização de dados.

## Arquitetura do projeto

O projeto está organizado de forma a separar as responsabilidades de cada camada:

```text
com.example.Jokora/
│
├── data/
│   ├── database/
│   ├── dao/
│   ├── entity/
│   └── repository/
│
├── model/
│
├── navigation/
│
├── ui/
│   ├── screens/
│   ├── components/
│   └── theme/
│
├── viewmodel/
│
└── MainActivity.kt
```

### Organização das camadas

- **Data:** contém a configuração do banco de dados, entidades, DAOs e repositórios responsáveis pelo acesso e gerenciamento dos dados.
- **Model:** reúne os modelos que representam os elementos e conceitos do jogo.
- **Navigation:** concentra as rotas, o fluxo de navegação e a barra de navegação do aplicativo.
- **UI:** contém as telas e os componentes visuais reutilizáveis.
- **ViewModel:** concentra a lógica de apresentação, gerenciamento de estado e comunicação entre a interface e os repositórios.

## Persistência de dados

O aplicativo utiliza Room Database para armazenar localmente as informações dos jogadores e das partidas.

A camada de persistência é organizada por meio de:

- **Entities:** representam as tabelas do banco de dados.
- **DAOs:** definem as operações de consulta, inserção, atualização e exclusão.
- **Repositories:** intermediam o acesso aos dados e disponibilizam as operações para os ViewModels.
- **Database:** configura e disponibiliza a instância do banco de dados.

## Como executar o projeto

1. Clone este repositório:

```bash
git clone URL_DO_REPOSITORIO
```

2. Abra o projeto no Android Studio.
3. Aguarde a sincronização das dependências do Gradle.
4. Selecione um emulador Android ou conecte um dispositivo físico.
5. Execute o aplicativo pelo botão **Run**.

## Equipe e desenvolvimento

O projeto foi desenvolvido em equipe, com divisão de responsabilidades entre os integrantes para a implementação das principais telas e funcionalidades.

A estrutura de dados, a arquitetura e a navegação foram organizadas para permitir a integração entre as diferentes partes do aplicativo.

## Objetivo acadêmico

O Jokora tem como objetivo demonstrar, na prática, a aplicação dos conceitos estudados durante a disciplina, especialmente arquitetura MVVM, desenvolvimento de interfaces Android, persistência de dados, navegação, gerenciamento de estado e organização de código em camadas.
