package dispatcher;

import java.util.ArrayList;
import java.util.LinkedList;
import java.util.Queue;
import java.util.Random;

public class Dispatcher {

    public static int idCounter = 0; // id sayacı
    private Process previousProcess; // önceki prosesin tutulması için gereken değişken

    public static final int TOTAL_MEM = 1024;
    public static final int REAL_MEM = 64;
    public static final int USER_MEM = (TOTAL_MEM - REAL_MEM);
    public int YAZICI = 2;
    public int TARAYICI = 1;
    public int MODEM = 1;
    public int CD = 2;

    public int processYazici;
    public int processTarayici;
    public int processModem;
    public int processCD;
    public int processMem;

    public int processYazici2;
    public int processTarayici2;
    public int processModem2;
    public int processCD2;
    public int processMem2;

    private static final int REAL_TIME = 0;
    private static final int PRIORITY_1 = 1;
    private static final int PRIORITY_2 = 2;
    private static final int PRIORITY_3 = 3;

    // Her öncelik seviyesi için ayrı kuyruk
    private Queue<Process> realTimeQueue;
    private Queue<Process> priority1Queue;
    private Queue<Process> priority2Queue;
    private Queue<Process> priority3Queue;
    private Queue<Process> temp;

    // şimdiki zaman
    private int currentTime;

    // round robin için quantum zaman aralığı
    private static final int QUANTUM = 1;

    public Dispatcher() {
        realTimeQueue = new LinkedList<>();
        priority1Queue = new LinkedList<>();
        priority2Queue = new LinkedList<>();
        priority3Queue = new LinkedList<>();
        temp = new LinkedList<>();
        currentTime = 0;
    }

    public void addProcess(Process process) {
        // prosesler önceliğe göre ilgili kuyruğa eklenir
        switch (process.getPriority()) {
            case REAL_TIME:
                realTimeQueue.add(process);
                break;
            case PRIORITY_1:
                priority1Queue.add(process);
                break;
            case PRIORITY_2:
                priority2Queue.add(process);
                break;
            case PRIORITY_3:
                priority3Queue.add(process);
                break;
        }
    }

    public void run() {
        int realTimeArrival;
        // Tüm kuyruklar boş olana kadar devam eden while
        while (!realTimeQueue.isEmpty() || !priority1Queue.isEmpty() || !priority2Queue.isEmpty() || !priority3Queue.isEmpty()) {
            // Şimdiki zamanda ulaşmış olan gerçek zamanlı proses var mı kontrol edilir.
            if (!realTimeQueue.isEmpty()) {
                realTimeArrival = realTimeQueue.peek().getArrivalTime();
            } else {
                realTimeArrival = 10000;
            }

            if (currentTime >= realTimeArrival) {
                // Sonraki gerçek zamanlı prosesin yürütülmesi
                Process process = realTimeQueue.poll();
                runProcess(process);
            }
            // 1.Öncelik kuyruğunda proses var mı kontrol edilir
            else if (!priority1Queue.isEmpty()) {
                // sonrakı 1 öncelikli prosesin yürütülmesi
                Process process = priority1Queue.peek();
                runProcessForQuantum(process);
            }
            // 2.Öncelik kuyruğunda proses var mı kontrol edilir
            else if (!priority2Queue.isEmpty()) {
                // sonrakı 2 öncelikli prosesin yürütülmesi
                Process process = priority2Queue.peek();
                runProcessForQuantum(process);
            }
            // tüm prosesler 3 öncelikli ise, round robin sralama algoritması kullanılır.
            else {
                // bitmemiş prosesler bir listede tutulur
                ArrayList<Process> unfinishedProcesses = new ArrayList<>();
                while (!priority3Queue.isEmpty()) {
                    Process process = priority3Queue.poll();
                    // quantum süresine göre proses yürütülür
                    runProcessForQuantum(process);
                    // Eğer proses tamamlanmamış ise; tekrar bitmemiş proses kuyruğuna eklenir.
                    if (process.getBurstTime() > 0) {
                        unfinishedProcesses.add(process);
                    }
                }
                // bitmemiş prosesler tekrar 3.öncelik kuyruğuna eklenir.
                priority3Queue.addAll(unfinishedProcesses);
            }
        }
    }

    private void runProcess(Process process) {
        processYazici = process.getYaziciIhtiyaci();
        processTarayici = process.getTarayiciIhtiyaci();
        processModem = process.getModemIhtiyaci();
        processCD = process.getCDIhtiyaci();
        processMem = process.getBellekIhtiyaci();

        // Prosesler için rastgele renk şeması üretir
        Random rand = new Random();
        int red = rand.nextInt(256);
        int green = rand.nextInt(256);
        int blue = rand.nextInt(256);
        String color = "\u001B[38;2;" + red + ";" + green + ";" + blue + "m";

        //***if -> prosesin istediği bellekyeterlimi kontrollerini yap, geçerse devam et, geçemezse hata mesajı yazdır
        if ((processYazici != 0) || (processTarayici != 0) || (processModem != 0) || (processCD != 0)) {
            System.out.println(color + currentTime + " sn PROCESS BAŞLAYAMADI:GERÇEK ZAMANLI PROCESS KAYNAK KULLANMAYA ÇALIŞIYOR.");
        } else if (processMem > REAL_MEM) {
            System.out.println(color + currentTime + " sn PROCESS BAŞLAYAMADI:GERÇEK ZAMANLI PROCESS 64MB'TAN FAZLA BELLEK TALEP EDİYOR. PROCESS SİLİNDİ.");
        } else {

            if (previousProcess == null) {
                //***bellektahsisi yap
                System.out.println(color + currentTime + " sn  proses başladı       " + "(id:000" + process.id + "  öncelik:" + process.getPriority() + "  kalan süre:" + process.getBurstTime() + " sn)" + "Mbayt:" + processMem + " prn:" + processYazici + " scn:" + processTarayici + " modem:" + processModem + " cd:" + processCD);
            } else if (process.id != previousProcess.id) {
                if (previousProcess.getBurstTime() != 0) {
                    System.out.println(currentTime + " sn  proses askıda       " + "(id:000" + previousProcess.id + "  öncelik:" + previousProcess.getPriority() + "  kalan süre:" + previousProcess.getBurstTime() + " sn)" + "Mbayt:" + processMem + " prn:" + processYazici + " scn:" + processTarayici + " modem:" + processModem + " cd:" + processCD);
                }
                System.out.println(color + currentTime + " sn  proses başladı       " + "(id:000" + process.id + "  öncelik:" + process.getPriority() + "  kalan süre:" + process.getBurstTime() + " sn)" + "Mbayt:" + processMem + " prn:" + processYazici + " scn:" + processTarayici + " modem:" + processModem + " cd:" + processCD);
            }
            int burst = process.getBurstTime();
            while (burst > 0) {
                if (burst < process.startingBurstTime) {
                    System.out.println(color + currentTime + " sn  proses yürütülüyor   " + "(id:000" + process.id + "  öncelik:" + process.getPriority() + "  kalan süre:" + burst + " sn)" + "Mbayt:" + processMem + " prn:" + processYazici + " scn:" + processTarayici + " modem:" + processModem + " cd:" + processCD);
                }
                burst--;
                currentTime++;
            }
            // patlama zamanı 0 yapılarak proses sonlandırılır.
            process.setBurstTime(0);
            //*** bellek serbest bırak
            System.out.println(color + currentTime + " sn  proses sonlandı      " + "(id:000" + process.id + "  öncelik:" + process.getPriority() + "  kalan süre:0 sn)" + "Mbayt:" + processMem + " prn:" + processYazici + " scn:" + processTarayici + " modem:" + processModem + " cd:" + processCD);
            previousProcess = process;
        }
    }

    private void runProcessForQuantum(Process process) {

        //***if -> prosesin istediği bellekyeterlimi kaynYetrlimi kontrollerini yap, geçerse devam et, geçemezse hata yazdır

        processYazici2 = process.getYaziciIhtiyaci();
        processTarayici2 = process.getTarayiciIhtiyaci();
        processModem2 = process.getModemIhtiyaci();
        processCD2 = process.getCDIhtiyaci();
        processMem2 = process.getBellekIhtiyaci();

        // Prosesler için rastgele renk şeması üretir
        Random rand = new Random();
        int red = rand.nextInt(256);
        int green = rand.nextInt(256);
        int blue = rand.nextInt(256);
        String color = "\u001B[38;2;" + red + ";" + green + ";" + blue + "m";

        if ((processYazici2 <= YAZICI) && (processTarayici2 <= TARAYICI) && (processModem2 <= MODEM) && (processCD2 <= CD) && (processMem2 <= USER_MEM)) {
            YAZICI = YAZICI - processYazici2;
            TARAYICI = TARAYICI - processTarayici2;
            MODEM = MODEM - processModem2;
            CD = CD - processCD2;

            if (previousProcess == null) {
                System.out.println(color + currentTime + " sn  proses başladı       " + "(id:000" + process.id + "  öncelik:" + process.getPriority() + "  kalan süre:" + process.getBurstTime() + " sn" + "Mbayt:" + processMem2 + " prn:" + processYazici2 + " scn:" + processTarayici2 + " modem:" + processModem2 + " cd:" + processCD2);
            } else if (process.id != previousProcess.id) {
                if (previousProcess.getBurstTime() != 0) {
                    System.out.println(currentTime + " sn  proses askıda       " + "(id:000" + previousProcess.id + "  öncelik:" + previousProcess.getPriority() + "  kalan süre:" + previousProcess.getBurstTime() + " sn" + "Mbayt:" + processMem2 + " prn:" + processYazici2 + " scn:" + processTarayici2 + " modem:" + processModem2 + " cd:" + processCD2);
                }
                System.out.println(color + currentTime + " sn  proses başladı       " + "(id:000" + process.id + "  öncelik:" + process.getPriority() + "  kalan süre:" + process.getBurstTime() + " sn" + "Mbayt:" + processMem2 + " prn:" + processYazici2 + " scn:" + processTarayici2 + " modem:" + processModem2 + " cd:" + processCD2);
            } else {
            System.out.println(color + currentTime + " sn proses yürütülüyor " + "(id:000" + process.id + " öncelik:" + process.getPriority() + " kalan süre:" + process.getBurstTime() + " sn" + "Mbayt:" + processMem2 + " prn:" + processYazici2 + " scn:" + processTarayici2 + " modem:" + processModem2 + " cd:" + processCD2);
            }
            // 1 quantum zamanı kadar işlem yürütülür.
            int burstTime = process.getBurstTime();
            if (burstTime > QUANTUM) {
            // şimdiki zaman ve patlama zamanı güncellenir.
            currentTime += QUANTUM;
            process.setBurstTime(burstTime - QUANTUM);
            //önceki işlem olarak belirtilir.
            previousProcess = process;
            } else {
            // şimdiki zaman güncellenir ve patlama zamanı 0 yapılarak işlem sonlandırılır, önceki işlem olarak belirtilir.
            previousProcess = process;
            currentTime += burstTime;
            process.setBurstTime(0);
            System.out.println(color + currentTime + " sn proses sonlandı " + "(id:000" + process.id + " öncelik:" + process.getPriority() + " kalan süre:0 sn)");
            //sonlanan işlem bulunduğu kuyruktan kaldırılır.
            switch (process.getPriority()) {
            case PRIORITY_1:
            priority1Queue.remove();
            break;
            case PRIORITY_2:
            priority2Queue.remove();
            break;
            }
            }
            //*** kaynak bellek serbest bırak
            YAZICI = YAZICI + processYazici2;
            TARAYICI = TARAYICI + processTarayici2;
            MODEM = MODEM + processModem2;
            CD = CD + processCD2;
            } else {
            System.out.println(color + currentTime + " sn PROCESS BAŞLAYAMADI:KAYNAK/BELLEK YETERLİ DEĞİL. PROCESS SİLİNDİ.");
            switch (process.getPriority()) {
                case PRIORITY_1:
                    priority1Queue.remove();
                    break;
                case PRIORITY_2:
                    priority2Queue.remove();
                    break;
                case PRIORITY_3:
                    priority3Queue.remove();
                    break;
            }
        }
    }
    
