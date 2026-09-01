public class MainC {

    static volatile int sharedInt = 0;
    static volatile boolean done = false;

    public static class Incrementer implements Runnable {

        private final Object object;

        public Incrementer(Object obj) {
            this.object = obj;

        }

        public void run() {

            synchronized (this.object) {
                for (int i = 0; i < 1_000_000; i++) {
                    sharedInt++;
                }

                done = true;
                object.notify();

            }

        }
    }

    public static class Printer implements Runnable {

        private final Object object;

        public Printer(Object obj) {
            this.object = obj;

        }

        public void run() {
            synchronized (this.object) {

                while (!done) {
                    try {
                        object.wait();
                    } catch (InterruptedException e) {
                        e.printStackTrace();
                    }
                }
                System.out.println("sharedInt: " + sharedInt);

            }

        }
    }

    public static void main(String[] args) {

        int totalRuns = 30;

        for (int i = 0; i < totalRuns; i++) {
            sharedInt = 0;
            done = false;

            final Object obj = new Object();
            Thread incrementingThread = new Thread(new Incrementer(obj));
            Thread printingThread = new Thread(new Printer(obj));

            incrementingThread.start();
            printingThread.start();

            try {
                incrementingThread.join();
                printingThread.join();

            } catch (InterruptedException e) {
                e.printStackTrace();
            }

        }

    }
}
