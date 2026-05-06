package src;
import java.util.*;

class Processo {
    int pid; // Id do processo - Vindo do arquivo
    int chegada; // Tempo de chegada do processo - Vindo do arquivo
    int burst; // Burst do processo - Vindo do arquivo
    int restante;  // Tempo restante do processo - Iniciado com o Burse, mas atualizado (subtraido  ) na execução do escalonamento
    int prioridade; // Prioridade do processo - Vindo do arquivo

    List<Integer> ioInstantes; // Lista de instantes de I/O - Vindo do arquivo 
    int ioIndex = 0; // Indice de I/O. Indica quando que vai ser a próxima execução de I/O

    int tempoExecutado = 0; // Tempo executado - Calculado durante a execução do escalonamento
    int tempoDesdeUltimoIO = 0;  // Tempo desde o último I/O - Calculado durante a execução do escalonamento

    int tempoBloqueado = 0;  // Tempo bloqueado - Calculado durante a execução do escalonamento
    int tempoEspera = 0;  // Tempo esperando para ser executado - Calculado durante a execução do escalonamento
    int tempoFinal = 0;  // Tempo em que o processo termina - Calculado durante a execução do escalonamento

    int tau = 10;  // Tau fixo fornecido pelo enunciado 
    boolean finalizado = false; // Controle se terminou ou não

    // Construtor :D
    public Processo(int pid, int chegada, int burst, int prioridade, List<Integer> io) {
        this.pid = pid;
        this.chegada = chegada;
        this.burst = burst;        
        this.restante = burst;
        this.prioridade = prioridade;
        this.ioInstantes = io;
    }
}