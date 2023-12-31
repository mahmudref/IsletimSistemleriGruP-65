package dispatcher;

import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;

public class Program {

    public static void main(String[] args) {
        Dispatcher dispatcher = new Dispatcher();

        try {
            readProcessesFromFile("giriş.txt", dispatcher);
            dispatcher.run();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private static void readProcessesFromFile(String filePath, Dispatcher dispatcher) throws IOException {
        try (BufferedReader reader = new BufferedReader(new FileReader(filePath))) {
            String line;
            while ((line = reader.readLine()) != null) {
                String[] values = line.split(", ");
                int arrival = Integer.parseInt(values[0]);
                int priority = Integer.parseInt(values[1]);
                int burst = Integer.parseInt(values[2]);

                int bellek = Integer.parseInt(values[3]);
                int yazicilar = Integer.parseInt(values[4]);
                int tarayicilar = Integer.parseInt(values[5]);
                int modemler = Integer.parseInt(values[6]);
                int cdler = Integer.parseInt(values[7]);

                dispatcher.addProcess(new Process(arrival, priority, burst, bellek, yazicilar, tarayicilar, modemler, cdler));
            }
        }
    }
}
