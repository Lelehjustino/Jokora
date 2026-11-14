# Jokora — Jogo de Jokenpô
**[⬇️ Download do APK](https://drive.google.com/file/d/1VDlnUpbKmRhYHe8Qcaqo8Tam58CWVUDo/view?usp=sharing)**

## Sobre o projeto

O **Jokora** é um aplicativo mobile Android desenvolvido em Kotlin e Jetpack Compose, inspirado no clássico jogo Jokenpô. O aplicativo permite que os usuários disputem partidas contra o computador em duas modalidades: a tradicional, Pedra, Papel e Tesoura, e a avançada, Pedra, Papel, Tesoura, Lagarto e Spock.

O projeto foi desenvolvido como parte de um trabalho acadêmico, com o objetivo de aplicar conceitos de desenvolvimento mobile, arquitetura de software, navegação entre telas, gerenciamento de estado e persistência de dados.

## Demonstração

[Assistir à demonstração do aplicativo](https://drive.google.com/file/d/1fDLQOPZg7RlTZL6yUWSzZ10BDkJjnJOl/view?usp=drive_link)

A demonstração apresenta o aplicativo e suas funcionalidades. 

## 🎮 Conheça as regras do Jokenpô Avançado

Aprenda como funciona a modalidade Pedra, Papel, Tesoura, Lagarto e Spock:

[![Regras do Pedra, Papel, Tesoura, Lagarto e Spock](https://drive.google.com/thumbnail?id=1odUV1OGS0oHbzoLx6A7Pgwwlhe8JTKSx&sz=w1000)](https://www.youtube.com/watch?v=smY7u7hwJPk)

*Clique na imagem para assistir ao vídeo no YouTube.*

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
com.example.jokora/
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

## Problemas com commit 

Durante o desenvolvimento do trabalho, ocorreu um problema no histórico do Git: um dos commits acabou sobrescrevendo alterações que haviam sido realizadas anteriormente por outros integrantes do grupo.

Apesar desse problema, todos os membros participaram ativamente do desenvolvimento e realizaram seus próprios commits. Temos os registros no histórico do repositório que comprovam a participação individual de cada integrante.

É importante ressaltar que todos os membros contribuíram para o projeto e realizaram commits. O problema ocorrido foi relacionado à integração e à preservação das alterações no repositório, e não à falta de participação de algum integrante.

[Registros que todos os membros commitaram](https://drive.google.com/drive/folders/198Opoppmu5f8oCCk06LRNScipgnI-oGs?usp=sharing)

## Equipe e desenvolvimento

O projeto foi desenvolvido em equipe, com divisão de responsabilidades entre os integrantes para a implementação das principais telas e funcionalidades.

A estrutura de dados, a arquitetura e a navegação foram organizadas para permitir a integração entre as diferentes partes do aplicativo.

## Objetivo acadêmico

O Jokora tem como objetivo demonstrar, na prática, a aplicação dos conceitos estudados durante a disciplina, especialmente arquitetura MVVM, desenvolvimento de interfaces Android, persistência de dados, navegação, gerenciamento de estado e organização de código em camadas.
