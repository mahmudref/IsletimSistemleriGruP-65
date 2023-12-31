package dispatcher;

public class Process {
    // Variables for process identification
    int id; // Değişken proses kimliğini taşır

    // Variables for time and priority
    private int arrivalTime; // Değişken varış zamanını taşır
    private int priority; // Değişken öncelik düzeyini taşır

    // Variables for burst time
    private int burstTime; // Değişken patlama zamanını taşır
    public int startingBurstTime; // Değişken başlangıçtaki patlama süresini taşır

    // Variables for hardware requirements
    public int memory;
    public int printer;
    public int tarayicilar;
    public int modemler;
    public int cdler;

    // Constructor for initializing process attributes
    public Process(int arrivalTime, int priority, int burstTime, int memory, int printer, int tarayicilar, int modemler, int cdler) {
        this.arrivalTime = arrivalTime;
        this.priority = priority;
        this.burstTime = burstTime;
        startingBurstTime = burstTime;

        this.memory = memory;
        this.printer = printer;
        this.tarayicilar = tarayicilar;
        this.modemler = modemler;
        this.cdler = cdler;

        id = Dispatcher.idCounter;
        Dispatcher.idCounter++;
    }

    // Getter methods for accessing process attributes
    public int getArrivalTime() {
        return arrivalTime;
    }

    public int getPriority() {
        return priority;
    }

    public int getBurstTime() {
        return burstTime;
    }

    // Setter method for updating burst time
    public void setBurstTime(int burstTime) {
        this.burstTime = burstTime;
    }

    // Getter methods for hardware requirements
    public int getBellekIhtiyaci() {
        return memory;
    }

    public int getYaziciIhtiyaci() {
        return printer;
    }

    public int getTarayiciIhtiyaci() {
        return tarayicilar;
    }

    public int getModemIhtiyaci() {
        return modemler;
    }

    public int getCDIhtiyaci() {
        return cdler;
    }
}
