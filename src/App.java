package src;
import java.nio.charset.Charset;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;
import java.util.Queue;
import java.util.Scanner;
import java.io.File;
import java.io.IOException;
import java.io.PrintWriter;
import java.lang.reflect.InvocationTargetException;


public class App {

	/** Nome do arquivo de dados. O arquivo deve estar localizado na raiz do projeto */
    static String nomeArquivoDados;
    
    /** Scanner para leitura de dados do teclado */
    static Scanner teclado;

    /** Vetor de processos cadastrados */
    static List<Processo> processosCadastrados;
        
    static void limparTela() {
        System.out.print("\033[H\033[2J");
        System.out.flush();
    }

    /** Gera um efeito de pausa na CLI. Espera por um enter para continuar */
    static void pausa() {
        System.out.println("Digite enter para continuar...");
        teclado.nextLine();
    }

    /** Cabeçalho principal da CLI do sistema */
    static void cabecalho() {
        System.out.println("=========================================");
        System.out.println("SIMULAÇAO ALGORITMOS DE ESCALONAMENTO :D");
        System.out.println("=========================================");
    }
   
    static <T extends Number> T lerOpcao(String mensagem, Class<T> classe) {
        
    	T valor;
        
    	System.out.println(mensagem);
    	try {
            valor = classe.getConstructor(String.class).newInstance(teclado.nextLine());
        } catch (InstantiationException | IllegalAccessException | IllegalArgumentException 
        		| InvocationTargetException | NoSuchMethodException | SecurityException e) {
            return null;
        }
        return valor;
    }
    
    /** Imprime o menu principal, lê a opção do usuário e a retorna (int).
     * @return Um inteiro com a opção do usuário.
     */
    static int menu() {
        cabecalho();
        System.out.println("1 - First-Come, First-Served");
        System.out.println("2 - Shortest Remaining Time First");
        System.out.println("3 - Round Robin com Quantum de Predição");
        System.out.println("4 - Multilevel Queue");
        System.out.println("0 - Sair");
        System.out.print("Digite sua opçao: ");
        return Integer.parseInt(teclado.nextLine());
    }

	public static void main(String[] args) {
		
		teclado = new Scanner(System.in, Charset.forName("UTF-8"));
        
		nomeArquivoDados = "processos.txt";
        processosCadastrados = lerArquivoProcessos(nomeArquivoDados);
        
        int opcao = -1;
      
        do{
            opcao = menu();
            switch (opcao) {
                case 1 -> escalonamentoFirstComeFirstServed();
                case 2 -> escalonamentoShortestRemainingTimeFirst();
                case 3 -> escalonamentoRoundRobin();
                case 4 -> escalonamentoMultiLevelQueue();
            }
            pausa();
        }while(opcao != 0);       

        teclado.close();    
    }

    /**  Método para leitura do arquivo com os processos a serem utilizados.
     * @return Lista de processos.
     */
    public static List<Processo> lerArquivoProcessos(String caminho) {

        Scanner arquivo = null;
        List<Processo> lista = null;

        try {
            arquivo = new Scanner(new File(caminho), Charset.forName("UTF-8"));
            lista = new ArrayList<>();

            while (arquivo.hasNextLine()) {

                String[] p = arquivo.nextLine().split(";");

                int pid = Integer.parseInt(p[0]);
                int chegada = Integer.parseInt(p[1]);
                int burst = Integer.parseInt(p[2]);
                int prioridade = Integer.parseInt(p[3]);

                List<Integer> io = new ArrayList<>();

                if (p.length > 4 && !p[4].isEmpty()) {
                    for (String s : p[4].split(",")) {
                        io.add(Integer.parseInt(s));
                    }
                }

                lista.add(new Processo(pid, chegada, burst, prioridade, io));
            }

        } catch (IOException e) {
            lista = null;
        } finally {
            if (arquivo != null) arquivo.close(); 
        }

        return lista;
    }

    /**  Método auxiliar para conferir se todos os processos terminarama a execução.
     * @return Valor boolean. True caso todos os processos estejam terminados, false se algum processo ainda não tiver terminado.
     */
    public static boolean todosFinalizados(List<Processo> ps) {
        for (Processo p : ps)
            if (!p.finalizado)
                return false;
        return true;
    }

    

    /**  Método para imprimir as métricas dos processos. É chamado ao final de um algoritmo de escalonamento pra imprimir as métricas dele.
     * @return Métricas de Tempo de Espera Médio, Tempo de Retorno (Turnaround ) Médio e Vazão (Throughput).
     */
    private static void imprimirMetricas(List<Processo> ps, int tempoTotal, String nome) {

        int totalEspera = 0;
        int totalTurnaround = 0;
        int n = ps.size();

        
        System.out.println("\n=== RESULTADO " + nome + " ===");

        for (Processo p : ps) {
            int turnaround = p.tempoFinal - p.chegada;

            totalEspera += p.tempoEspera;
            totalTurnaround += turnaround;


        }

        double mediaEspera = (double) totalEspera / n;
        double mediaTurnaround = (double) totalTurnaround / n;
        double throughput = (double) n / tempoTotal;

        System.out.println("\n=== MÉTRICAS ===");
        System.out.printf("Tempo médio de espera: %.2f ms\n", mediaEspera);
        System.out.printf("Tempo médio de turnaround: %.2f ms\n", mediaTurnaround);
        System.out.printf("Throughput: %.2f processos/segundo\n", throughput * 1000);

    }

    /**  Algoritmo de escalonamento não-preemptivo simples por ordem de chegada.
     * @return Métricas de Tempo de Espera Médio, Tempo de Retorno (Turnaround ) Médio e Vazão (Throughput) .
     */
    private static void escalonamentoFirstComeFirstServed() {

        // Lista de processos carregados do arquivo
        List<Processo> ps = processosCadastrados;

        // Fila dos processos prontos
        Queue<Processo> fila = new LinkedList<>();

        // Fila para os prcoessos que estão bloqueados (I/O)
        List<Processo> bloqueados = new ArrayList<>();

        // Processo atual
        Processo atual = null;

        // Tempo :)
        int tempo = 0;

        // While que executa o algoritmo de escalonamento mesmo. Executa até que todos os prcoessos sejam terminados
        while (!todosFinalizados(ps)) {

            // Processos que chegam: For para verificar se algum processo chega no tempo atual. 
            for (Processo p : ps) {
                if (p.chegada == tempo) {
                    fila.add(p);
                }
            }

            // Iterador para passar pelos processos que estão bloqueados
            Iterator<Processo> it = bloqueados.iterator();

            // While que executa enquanto existir algum processo que esta bloqueado para subtrair o tempo que ele passou bloqueado
            while (it.hasNext()) {
                Processo p = it.next();
                p.tempoBloqueado--;

                // If para conferir se acabou o tempo bloqueado. Se tiver acabado, aquele processo volta pra fica de processos prontos para voltar a ser executado e sai da fila de processos bloqueados.
                if (p.tempoBloqueado == 0) {
                    fila.add(p);
                    it.remove();
                }
            }

            // Se a CPU estiver livre, pega o próximo processo da fila
            if (atual == null && !fila.isEmpty()) {
                atual = fila.poll(); // Pega o primeiro processo da fila E tira ele da fila de processos.
            }

            // Execução do prcoesso em si
            if (atual != null) {
                // Subtrai o tempo restante do processo que está sendo executado porque ele foi executado por 1 segundo
                atual.restante--;
                // Soma o tempo executado do processo que está sendo executado porque ele foi executado por 1 segundo 
                atual.tempoExecutado++;

                // Conferir instantes de I/O. Se ainda tem algum I/O para fazer e chegou nesse tempo, entra em I/O.
                if (atual.ioIndex < atual.ioInstantes.size() && atual.tempoExecutado == atual.ioInstantes.get(atual.ioIndex)) {

                    atual.tempoBloqueado = 5; // Tempo que vai ficar bloqueado (Fixo, vindo do enunciado :D)
                    bloqueados.add(atual); // O processo atual que vai entrar em I/O vai pra fila de bloqueados
                    atual.ioIndex++; // Se tive algum, vai pegar o index do próximo I/O que o processo vai ter 
                    atual = null; // Livre pro próximo processo que precisar da CPU
                }

               // Conferir se o processo terminou
                else if (atual.restante == 0) {
                    atual.finalizado = true; // Guarda que o processo esta como finalizado
                    atual.tempoFinal = tempo + 1; // Guarda o momento que ele finalizou
                    atual = null; // Livre pro próximo processo que precisar da CPU
                }

                // Se o processo não entrar em I/O ou finalizar, na próxima execução do While, vai continuar nele até ele terminar. Isso é a lógica First-Come, First-Served :D
            }

            // Gerenciar espera dos processos na fila
            for (Processo p : fila) {
                p.tempoEspera++; // Soma + 1 ao tempo de espera do processo porque precisamos guardar o tempo que o processo fica em espera.
            }

            tempo++; // Soma o tempo :)

        }

        imprimirMetricas(ps, tempo, "FIRST-COME, FIRST-SERVED");
    }
    

    /**  Variante preemptiva do SJF. O escalonador sempre escolhe o processo que possui o menor tempo de execução restante
     * @return Métricas de Tempo de Espera Médio, Tempo de Retorno (Turnaround ) Médio e Vazão (Throughput) .
     */
    private static void escalonamentoShortestRemainingTimeFirst() {
        System.out.println("Ainda nao ta pronto :P ...");
    }

    /**  Escalonamento circular onde o valor do quantum é recalculado a cada troca de contexto para ser igual á menor média exponencial (τ ) entre os processos na fila de prontos. Considerando α = 0.5, τ0 = 10ms.
     * @return Métricas de Tempo de Espera Médio, Tempo de Retorno (Turnaround ) Médio e Vazão (Throughput) .
     */
    private static void escalonamentoRoundRobin() {
        System.out.println("Ainda nao ta pronto :P ...");
    }
        
    /**  Implementar duas filas estáticas com prioridades fixas. A Fila 1 (Alta Prioridade) deve ser escalonada via Round-Robin (quantum fixo), e a Fila 2 (Baixa
    Prioridade) via FCFS (First-Come, First-Served ). Processos na Fila 2 só executam se a Fila 1
    estiver vazia.
     * @return Métricas de Tempo de Espera Médio, Tempo de Retorno (Turnaround ) Médio e Vazão (Throughput) .
     */
    private static void escalonamentoMultiLevelQueue() {
        System.out.println("Ainda nao ta pronto :P ...");
    }





}
