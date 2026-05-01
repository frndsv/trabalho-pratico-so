# Simulador de Algoritmos de Escalonamento

## Integrantes

-   Nome 1: Anthony Cesar de Carvalho Santos
-   Nome 2: Caio Santos Borges 
-   Nome 3: Sofia Fernandes Ferreira Silva

------------------------------------------------------------------------

## Descrição do que foi implementado

Este projeto implementa um simulador de algoritmos de escalonamento de
CPU, permitindo analisar o comportamento de diferentes estratégias de
gerenciamento de processos.

O sistema lê um conjunto de processos a partir de um arquivo de entrada
(`processos.txt`) e executa os algoritmos selecionados pelo usuário via
interface de linha de comando (CLI).

------------------------------------------------------------------------

## Requisitos para execução

-   Java JDK 8 ou superior instalado\
-   Terminal ou IDE (IntelliJ, VS Code, Eclipse, etc.)

------------------------------------------------------------------------

## Estrutura esperada

    /src
     ├── App.java
     ├── Processo.java
     ├── processos.txt

------------------------------------------------------------------------

## Compilação

No terminal, dentro da pasta do projeto:

``` bash
javac App.java
```

Se houver múltiplos arquivos `.java`:

``` bash
javac *.java
```

------------------------------------------------------------------------

## Execução

Após compilar:

``` bash
java App
```

------------------------------------------------------------------------

## Arquivo de entrada utilizado

O arquivo `processos.txt` deve estar na raiz do projeto e seguir o
formato:

    PID;Chegada;Burst;Prioridade;IO

### Exemplo:

    1;0;10;1;3,7
    2;2;5;2;
    3;4;8;1;4

-   **PID**: identificador do processo
-   **Chegada**: tempo da chegada daquele processo
-   **Burst**: tempo total de CPU
-   **Prioridade**: prioridade do processo
-   **IO**: instantes de I/O (separados por vírgula, opcional)

------------------------------------------------------------------------

## Algoritmos implementados

-   First-Come, First-Served (FCFS)
-   Shortest Remaining Time First (SRTF) 
-   Round Robin com quantum dinâmico 
-   Multilevel Queue

------------------------------------------------------------------------

## Saída

Após a execução do algoritmo de escalonamento selecionado pelo usuário, o sistema exibe:

-   Tempo de espera de cada processo
-   Tempo de turnaround
-   Métricas gerais:
    -   Tempo médio de espera
    -   Tempo médio de turnaround
    -   Throughput

------------------------------------------------------------------------

## Observações

-   Certifique-se de que o arquivo `processos.txt` está corretamente
    formatado.
- Certifique-se de que o arquivo `processos.txt` está na raiz do projeto.
-   Para grandes quantidades de processos, recomenda-se salvar a saída
    em arquivo.

------------------------------------------------------------------------

## Relatório

