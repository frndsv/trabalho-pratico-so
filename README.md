# Simulador de Algoritmos de Escalonamento

Sistema desenvolvido para o Trabalho Prático 1 da discplina de Sistemas Operacionais.

------------------------------------------------------------------------

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

Após a implementação, realizamos testes de execução com um arquivo `processos.txt` igual para os quatro algoritmos de escalonamento. Para o teste, usamos um volume não tão alto de processos para facilitar o mapeamento dos resultados e entender se foram corretos ou não.

#### Abaixo está o arquivo utilizado no teste

    101;0;18;2;
    102;1;10;1;3,7
    103;2;6;1;
    104;4;14;2;5
    105;6;8;1;2
    106;8;20;2;
    107;10;5;1;
    108;12;9;1;4
    109;15;7;2;
    110;17;11;1;6
    111;20;4;1;
    112;22;13;2;3,8

Como visto, o arquivo segue o modelo dado pelo professor no enunciado. Além disso, escolhemos colocar um número menor de processos, mas com uma variedade maior de tempos de **chegada**, **burst** e **prioridade** para acrescentar mais ao teste.

#### Os resultados das métricas obtidas no console de execução estão apresentados na tabela abaixo

| Algoritmo | Tempo Médio de Espera | Tempo Médio de Turnaround | Throughput |
|---|---:|---:|---:|
| FCFS | 58.08 ms | 71.17 ms | 0.10 |
| SRTF | 32.58 ms | 45.67 ms | 0.10 |
| Round Robin | 66.75 ms | 79.17 ms | 0.10 |
| Multilevel Queue | 43.08 ms | 56.17 ms | 0.09 |

### Análise dos resultados obtidos

Com base nos dados que recebemos das métricas calculadas, é possível concluir que, para esse teste, o algoritmo Shortest Remaining Time (SRTF) foi o mais eficiente entre os demais. A explicação por trás disso é dada pelo fato de esse algoritmo dar prioridade para os processos que possuem menor tempo de execução, o que faz com que os processos com menor tempo de execução sejam realizados primeiro, sem ficarem esperando os maiores.

Quando se trata do algoritmo com o pior resultado, o First Come First Served (FCFS) teve o pior desempenho entre eles. Isso ocorre, pois a ideia desse algoritmo é executar os processos na ordem de chegada, independentemente do tempo que isso vai custar. Com isso, ele tem um resultado inferior comparado com os demais, pois, se um processo grande chegar primeiro, ele monopoliza a CPU até terminar, fazendo com que ocorra um aumento muito grande no tempo de espera dos demais processos (muitas vezes muito menores).

O Round Robin apresentou tempos médios maiores, mas isso não é 100% um ponto negativo. Isso se deve ao fato de que um dos principais focos do Round Robin (principalmente quando aplicado da forma orientada no enunciado desta atividade) é tentar trazer mais justiça entre os processos, dando a eles um tempo igual entre si. Esse aumento também pode ser explicado pelas trocas de contexto que alguns processos realizam, fazendo com que os outros esperem mais tempo.

Por fim, o Multilevel Queue apresentou um resultado intermediário quando comparado aos demais algoritmos, sendo superior ao FCFS e ao Round Robin, mas inferior ao SRTF. Com a divisão das prioridades (onde os processos menores receberam prioridade 1 e os maiores prioridade 2), o algoritmo permitiu que processos menores fossem concluídos mais rapidamente, sem precisar esperar longos períodos por processos que utilizariam a CPU por mais tempo. Caso as prioridades fossem invertidas, os resultados provavelmente seriam menos eficientes.
 