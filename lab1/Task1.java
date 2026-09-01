package lab1;

public class Task1 implements Runnable {
    static volatile int a;

    @Override
    public synchronized void run() {
        for (int i = 0; i < 1_000_000; i++) {
            a++;
        }
    }

    public static void main(String[] args) {
        Runnable task1 = new Task1();

        int n = 4;

        if (args.length > 0) {
            n = Integer.parseInt(args[0]);
        }

        Thread ts[] = new Thread[n];

        for (int i = 0; i < n; i++) {
            ts[i] = new Thread(task1);
            ts[i].start();
        }

        for (int i = 0; i < n; i++) {
            try {
                ts[i].join();
            } catch (InterruptedException e) {
                // TODO Auto-generated catch block
                e.printStackTrace();
            }
        }

        System.out.println("value of a: " + a);
    }

}