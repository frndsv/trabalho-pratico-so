package src;
import java.util.*;

class Processo {
    int pid, chegada, restante, prioridade;

    List<Integer> ioInstantes;
    int ioIndex = 0;

    int tempoExecutado = 0;
    int tempoDesdeUltimoIO = 0;

    int tempoBloqueado = 0;
    int tempoEspera = 0;
    int tempoFinal = 0;

    int tau = 10;
    boolean finalizado = false;

    public Processo(int pid, int chegada, int burst, int prioridade, List<Integer> io) {
        this.pid = pid;
        this.chegada = chegada;
        this.restante = burst;
        this.prioridade = prioridade;
        this.ioInstantes = io;
    }
}