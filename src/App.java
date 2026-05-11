package src;
import java.nio.charset.Charset;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;
import java.util.PriorityQueue;
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

    /** Metodo auxiliar para criar uma nova lista de processos, para nao utilizar porcessos ja modificados por outros metodos
     * @return Lista de processos.
     */
    private static List<Processo> copiarProcessos(List<Processo> origem) {
        List<Processo> copia = new ArrayList<>();

        for (Processo p : origem) {
            Processo novo = new Processo(
                p.pid,
                p.chegada,
                p.restante,
                p.prioridade,
                new ArrayList<>(p.ioInstantes)
            );

            copia.add(novo);
        }

        return copia;
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

        // Variáveis para serem utilizadas no cálculo
        int totalEspera = 0; 
        int totalTurnaround = 0;
        int n = ps.size();

        
        System.out.println("\n=== RESULTADO " + nome + " ===");

        // For para percorrer os processos
        for (Processo p : ps) {
            int turnaround = p.tempoFinal - p.chegada; // Calcula o tempo de retorno do processo

            totalEspera += p.tempoEspera; // Soma o tempo de espera do processo atual na variável que vai ser usada pra média
            totalTurnaround += turnaround; // Soma o tempo de retorno (turnaround) do processo atual na variável que vai ser usada pra média

        }

        double mediaEspera = (double) totalEspera / n; // Calcula a média de espera total: Soma das esperas de cada process / quantidade de processos
        double mediaTurnaround = (double) totalTurnaround / n; // Calcula a média de tempo de retorno (turnaround): Soma dos tempos de retorno de cada process / quantidade de processos
        double throughput = (double) n / tempoTotal; // Calcula a vazão (Throughput): Quantidade de processos / tempo total gasto no escalonamento

        // Imprimindo cada mética na tela :D
        System.out.println("\n=== MÉTRICAS ===");
        System.out.printf("Tempo médio de espera: %.2f ms\n", mediaEspera);
        System.out.printf("Tempo médio de turnaround: %.2f ms\n", mediaTurnaround);
        System.out.printf("Throughput: %.2f processos/unidade de tempo\n", throughput);

    }

    /**  Algoritmo de escalonamento não-preemptivo simples por ordem de chegada.
     * @return Métricas de Tempo de Espera Médio, Tempo de Retorno (Turnaround ) Médio e Vazão (Throughput) .
     */
    private static void escalonamentoFirstComeFirstServed() {

        // Lista de processos copiados usando os originais do arquivo
        List<Processo> ps = copiarProcessos(processosCadastrados);

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
            Iterator<Processo> itProcesso = bloqueados.iterator();

            // While que executa enquanto existir algum processo que esta bloqueado para subtrair o tempo que ele passou bloqueado
            while (itProcesso.hasNext()) {
                Processo p = itProcesso.next();
                p.tempoBloqueado--;

                // If para conferir se acabou o tempo bloqueado. Se tiver acabado, aquele processo volta pra fica de processos prontos para voltar a ser executado e sai da fila de processos bloqueados.
                if (p.tempoBloqueado == 0) {
                    fila.add(p);
                    itProcesso.remove();
                }
            }

            // Se a CPU estiver livre, pega o próximo processo da fila
            if (atual == null && !fila.isEmpty()) {
                atual = fila.poll(); // Pega o primeiro processo da fila
            }

            // Execução do prcoesso em si
            if (atual != null) {
                // Subtrai o tempo restante do processo que está sendo executado porque ele foi executado por 1 segundo
                atual.restante--;
                // Soma o tempo executado do processo que está sendo executado porque ele foi executado por 1 segundo 
                atual.tempoExecutado++;

                
                // Conferir se o processo terminou
                if(atual.restante == 0) {
                    atual.finalizado = true; // Guarda que o processo esta como finalizado
                    atual.tempoFinal = tempo + 1; // Guarda o momento que ele finalizou
                    atual = null; // Livre pro próximo processo que precisar da CPU
                }

                // Conferir instantes de I/O. Se ainda tem algum I/O para fazer e chegou no tempo referente ao I/O, entra em I/O
                else if (atual.ioIndex < atual.ioInstantes.size() && atual.tempoExecutado == atual.ioInstantes.get(atual.ioIndex)) {

                    atual.tempoBloqueado = 5; // Tempo que vai ficar bloqueado (Fixo, vindo do enunciado :D)
                    bloqueados.add(atual); // O processo atual que vai entrar em I/O vai pra fila de bloqueados
                    atual.ioIndex++; // Se tive algum, vai pegar o index do próximo I/O que o processo vai ter 
                    atual = null; // Livre pro próximo processo que precisar da CPU
                }


                // Se o processo não entrar em I/O ou finalizar, na próxima execução do While, vai continuar nele até ele terminar. Isso é a lógica First-Come, First-Served :D
            }

            // Gerenciar espera dos processos na fila
            for (Processo p : fila) {
                p.tempoEspera++; // Soma + 1 ao tempo de espera do processo porque precisamos guardar o tempo que um processo fica em espera até ele terminar
            }

            tempo++; // Soma o tempo :)

        }

        imprimirMetricas(ps, tempo, "FIRST-COME, FIRST-SERVED");
    }
    

    /**  Variante preemptiva do SJF. O escalonador sempre escolhe o processo que possui o menor tempo de execução restante
     * @return Métricas de Tempo de Espera Médio, Tempo de Retorno (Turnaround ) Médio e Vazão (Throughput) .
     */
    private static void escalonamentoShortestRemainingTimeFirst() {

        // Lista de processos copiados usando os originais do arquivo
        List<Processo> ps = copiarProcessos(processosCadastrados);

        // Fila com prioridade, que mantem o processo com o menor tempo restante no topo. P.restante sendo o atribuito de um Processo, referente ao tempo que falta para o processo termianr de ser executado
        PriorityQueue<Processo> fila = new PriorityQueue<>(Comparator.comparingInt(p -> p.restante));

        // Fila para os prcoessos que estão bloqueados (I/O)
        List<Processo> bloqueados = new ArrayList<>();

        // Processo atual
        Processo atual = null;

        // Tempo :)
        int tempo = 0;

        // While que executa o algoritmo de escalonamento mesmo. Executa até que todos os processos sejam terminados
        while (!todosFinalizados(ps)) {

            // Processos que chegam: For para verificar se algum processo chega no tempo atual
            for (Processo p : ps){
                if (p.chegada == tempo) {
                    fila.add(p);
                } 
            }

            // Iterador para passar pelos processos que estão bloqueados
            Iterator<Processo> itProcesso = bloqueados.iterator(); 

            // While que executa enquanto existir algum processo que está bloqueado para subtrair o tempo que ele passou bloqueado
            while (itProcesso.hasNext()) {
                Processo p = itProcesso.next();
                p.tempoBloqueado--;

                // If para conferir se acabou o tempo bloqueado. Se tiver acabado, aquele processo volta pra fila de processos prontos para voltar a ser executado e sai da fila de processos bloqueados
                if (p.tempoBloqueado == 0) {
                    fila.add(p);
                    itProcesso.remove();
                }
            }

            // Diferente do First come First Served!!! La, confere se a CPU ta liVre e pega o próximo da fila. Aqui ele faz outra verificação
            if (atual != null && !fila.isEmpty()) {
                // Pega o menor processo na fila atual
                Processo menor = fila.peek();
                // Confere se o tempo restante do processo que esta no topo da fila como menor é menor do que o tempo restante do processo que está sendo executado atualmente
                if (menor.restante < atual.restante) {
                    // Se o menor for menor que o atual, o atual volta pra fila.
                    fila.add(atual);
                    // O atual agora se torna o processo que antes era o menor
                    atual = fila.poll();
                }
            }

            // Se a CPU estiver livre, pega o próximo processo na fila
            if (atual == null && !fila.isEmpty()) {
                atual = fila.poll(); // Pega o primeiro processo da fila 
            }


            // Execução do processo em si
            if (atual != null) {
                // Subtrai o tempo restante do processo que está sendo executado porque ele foi executado por 1 segundo
                atual.restante--;
                // Soma o tempo executado do processo que está sendo executado porque ele foi executado por 1 segundo
                atual.tempoExecutado++;
            
                // Conferir se o processo terminou
                if(atual.restante == 0) {
                    atual.finalizado = true; // Guarda que o processo está como finalizado
                    atual.tempoFinal = tempo + 1; // Guarda o momento que ele finalizou
                    atual = null; // Livre pro próximo que precisar da CPU
                }
            

                // Conferir instantes de I/O. Se ainda tem algum I/O para fazer e chegou no tempo referente ao I/O, entra em I/O
                else if (atual.ioIndex < atual.ioInstantes.size() && atual.tempoExecutado == atual.ioInstantes.get(atual.ioIndex)) {

                    atual.tempoBloqueado = 5; // Tempo que vai ficar bloqueado (Fixo, vindo do enunciado :D)
                    bloqueados.add(atual); // O processo atual que vai entra tem I/O vai pra fila de bloqueados
                    atual.ioIndex++; // Se tiver algum, vai pegar o index do próximo I/O que o processo vai ter 
                    atual = null; // livre pro próximo proesso que precisar da CPU
                }

                // Se o processo não entrar em I/O ou finalizar, na próxima execução do While, ele vai conferir novamente se algum processo chegou e é menor que o que está sendo executado atualmente. Se o novo for menor, aí sim o "atual" vai ser trocado pelo menor e ele vai passar a ser exeutado. Um processo só sai da fila se ele for entrar em I/O, terminar ou algum processo menor que ele chegar. Isso é a lógica do Shortest Remaining Time First
            }

            // Gerenciar espera dos processos na fila
            for (Processo p : fila) {
                p.tempoEspera++; // Soma + 1 ao tempo de espera do processo porque precisamos guardar o tempo que um processo fica em espera até ele terminar
            }

            tempo++; // Soma o tempo :)

        }

        imprimirMetricas(ps, tempo, "SHORTEST REMAINING TIME FIRST");
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

        List<Processo> ps = copiarProcessos(processosCadastrados); //copia processos da lista de processos cadastrados

        Queue<Processo> alta = new LinkedList<>(); // lista de processos de alta prioridade
        Queue<Processo> baixa = new LinkedList<>(); // lista de processos de baixa prioridade
        List<Processo> bloqueados = new ArrayList<>(); // lista de processos bloqueados porque estão em I/O


        Processo atual = null; // null significa que ninguém está usando a CPU

        int tempo = 0;
        int quantumAlta = 4; // quantum fixo do round robin
        int quantumRestante = 0; // quanto falta de quantum para o processo atual

        while(!todosFinalizados(ps)){

            // 1. Adiciona processos que chegaram no tempo atual
            for (Processo p : ps) {
                if (p.chegada == tempo) {
                    if (p.prioridade == 1) { // se a prioridade do processo for 1 vai para a fila de alta prioridade
                        alta.add(p);
                    } else { // o resto vai pra fila de baixa prioridade
                        baixa.add(p);
                    }
                }
            }

            //usa Iterator porque dentro do loop serão removidos processos da lista, o que pode dar erro com for each
            Iterator<Processo> itProcesso = bloqueados.iterator();

            //executa enquanto ainda existir processo dentro da lista de bloqueados
            while(itProcesso.hasNext()){ 
                Processo p = itProcesso.next(); // recebe o proximo processo do iterator
                p.tempoBloqueado --; //diminui o tempo q o processo foi bloqueado
                if(p.tempoBloqueado == 0){
                    if (p.prioridade == 1) { // se a prioridade do processo for 1 vai para a fila de alta prioridade
                        alta.add(p);
                    } else { // o resto vai pra fila de baixa prioridade
                        baixa.add(p);
                    }
                    itProcesso.remove();
                }
            }

            //verifica se tem algum processso na fila de alta prioridade para executar enquanto um de baixa esta executando
            if(atual != null && atual.prioridade != 1 && !alta.isEmpty()){ 
                baixa.add(atual);//adiciona o processo atual de baixa prioridade de volta para a fila de baixa prioridade
                atual = null; //retira o processo atual para que outro execute
            }

            if(atual == null){//verifica se tem algum processo na cpu
                if(!alta.isEmpty()){//verifica se tem processos na fila de alta prioridade
                    atual = alta.poll();//remove o primeiro da fila alta e coloca na cpu
                    quantumRestante = quantumAlta;//define o quantum do processo igual ao quantum fixo do RR
                } else if(!baixa.isEmpty()){//verifica se tem processos na fila de baixa prioridade
                    atual = baixa.poll();//remove o primeiro da fila baixa e coloca na cpu
                    quantumRestante = Integer.MAX_VALUE;//define o quantum do processo o maior possivel para funcionar como um FCFS
                }
            }

            if(atual == null){//verifica se nao tem processos 
                tempo++;
                continue; //pula pra proxima iteraçao do while 
            }

            //executa o processo uma unidade de tempo
            atual.restante--;
            atual.tempoExecutado++;
            quantumRestante--;

            //incrementa o tempo de espera dos processos que nao estao em execução nas duas filas
            for(Processo p : alta){
                p.tempoEspera++;
            }
            for(Processo p : baixa){
                p.tempoEspera++;
            }

            //verifica se o processo terminou
            if(atual.restante == 0){
                atual.finalizado = true;
                atual.tempoFinal = tempo + 1;
                atual = null;
            }

            //verifica se o processo precisa fazer I/O
            else if(atual.ioIndex < atual.ioInstantes.size() &&
             atual.tempoExecutado == atual.ioInstantes.get(atual.ioIndex) ){ 
                atual.tempoBloqueado = 5; // tempo que o processo vai ficar bloqueado de acordo com o enunciado :D
                bloqueados.add(atual); // adiciona o processo a lista de bloqueados
                atual.ioIndex++;
                atual = null;
            }

            //executa o roundRobin na fila de alta prioridade
            else if(atual.prioridade == 1 && quantumRestante == 0){
                alta.add(atual);
                atual = null;
            }

            //conta o tempo
            tempo++;
        }

        imprimirMetricas(ps, tempo, "MULTILEVEL QUEUE");
    }


}
