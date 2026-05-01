package src;
import java.nio.charset.Charset;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;
import java.util.Scanner;
import java.io.File;
import java.io.IOException;
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
        System.out.println("SIMULAÇÃO ALGORITMOS DE ESCALONAMENTO :D");
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
        System.out.print("Digite sua opção: ");
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

    /**  Algoritmo de escalonamento não-preemptivo simples por ordem de chegada.
     * @return Métricas de Tempo de Espera Médio, Tempo de Retorno (Turnaround ) Médio e Vazão (Throughput) .
     */
    private static void escalonamentoFirstComeFirstServed() {
        
    }

    /**  Variante preemptiva do SJF. O escalonador sempre escolhe o processo que possui o menor tempo de execução restante
     * @return Métricas de Tempo de Espera Médio, Tempo de Retorno (Turnaround ) Médio e Vazão (Throughput) .
     */
    private static void escalonamentoShortestRemainingTimeFirst() {
        
    }

    /**  Escalonamento circular onde o valor do quantum é recalculado a cada troca de contexto para ser igual á menor média exponencial (τ ) entre os processos na fila de prontos. Considerando α = 0.5, τ0 = 10ms.
     * @return Métricas de Tempo de Espera Médio, Tempo de Retorno (Turnaround ) Médio e Vazão (Throughput) .
     */
    private static void escalonamentoRoundRobin() {
       
    }
        
    /**  Implementar duas filas estáticas com prioridades fixas. A Fila 1 (Alta Prioridade) deve ser escalonada via Round-Robin (quantum fixo), e a Fila 2 (Baixa
    Prioridade) via FCFS (First-Come, First-Served ). Processos na Fila 2 só executam se a Fila 1
    estiver vazia.
     * @return Métricas de Tempo de Espera Médio, Tempo de Retorno (Turnaround ) Médio e Vazão (Throughput) .
     */
    private static void escalonamentoMultiLevelQueue() {

    }





}
