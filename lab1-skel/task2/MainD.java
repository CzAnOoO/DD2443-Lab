public class MainD {

    static volatile int sharedInt = 0;
    static volatile boolean done = false;
    static volatile long startTime;
    static volatile long delay;

    static class Incrementer implements Runnable {

        private final Object object;

        public Incrementer(Object obj) {
            this.object = obj;

        }

        @Override
        public void run() {
            synchronized (this.object) {
                for (int i = 0; i < 1_000_000; i++) {
                    sharedInt++;
                }
                startTime = System.nanoTime();

                done = true;
                object.notify();

            }

        }
    }

    static class Printer implements Runnable {

        private final Object object;

        public Printer(Object obj) {
            this.object = obj;

        }

        @Override
        public void run() {
            synchronized (this.object) {

                while (!done) {
                    try {
                        object.wait();
                    } catch (InterruptedException e) {
                        e.printStackTrace();
                    }
                }
                long current = System.nanoTime();
                delay = current - startTime;
                System.out.println("sharedInt: " + sharedInt);

            }

        }
    }

    public static void main(String[] args) {

        int totalRuns = 30;
        int warmUpRuns = 10;
        long totalDelay = 0;
        int measuredCount = 0;

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

            if (i >= warmUpRuns) {
                totalDelay += delay;
                measuredCount++;
            }

        }

        System.out.println("Busy-Waiting Average Delay: " + (totalDelay / measuredCount) + " ns");
    }

}
