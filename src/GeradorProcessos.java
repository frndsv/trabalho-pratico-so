package src;
import java.io.BufferedWriter;
import java.io.FileWriter;
import java.io.IOException;
import java.util.*;

public class GeradorProcessos {

    public static void main(String[] args) {
        int quantidade = 7000;

        try (BufferedWriter writer = new BufferedWriter(new FileWriter("processos.txt"))) {

            Random random = new Random();

            for (int i = 1; i <= quantidade; i++) {

                int pid = 100 + i;
                int chegada = random.nextInt(500); // 0 a 499
                int burst = random.nextInt(61) + 10; // 10 a 70
                int prioridade = random.nextInt(5) + 1; // 1 a 5

                // quantidade de IOs (0 a 3)
                int qtdIO = random.nextInt(4);

                Set<Integer> ioSet = new HashSet<>();

                while (ioSet.size() < qtdIO) {
                    int io = random.nextInt(burst - 1) + 1; // entre 1 e burst-1
                    ioSet.add(io);
                }

                List<Integer> ioList = new ArrayList<>(ioSet);
                Collections.sort(ioList);

                StringBuilder linha = new StringBuilder();
                linha.append(pid).append(";")
                     .append(chegada).append(";")
                     .append(burst).append(";")
                     .append(prioridade).append(";");

                for (int j = 0; j < ioList.size(); j++) {
                    linha.append(ioList.get(j));
                    if (j < ioList.size() - 1) {
                        linha.append(",");
                    }
                }

                writer.write(linha.toString());
                writer.newLine();
            }

            System.out.println("Arquivo processos.txt gerado com sucesso!");

        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}