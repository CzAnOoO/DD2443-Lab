public class MainD2 {

    static volatile int sharedInt = 0;
    static volatile boolean done = false;
    static volatile long startTime;
    static volatile long delay;

    static class Incrementer implements Runnable {


    

        @Override
        public void run() {
            
                for (int i = 0; i < 1_000_000; i++) {
                    sharedInt++;
                }
                startTime = System.nanoTime();
                done = true;

            

        }
    }

    static class Printer implements Runnable {

    

        @Override
        public void run() {
            

                while (!done) {
                    
                }
                long current = System.nanoTime();
                delay = current - startTime;
                //System.out.println("sharedInt: " + sharedInt);

            

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

            Thread incrementingThread = new Thread(new Incrementer());
            Thread printingThread = new Thread(new Printer());

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

        System.out.println("Busy waiting Average Delay: " + (totalDelay / measuredCount) + " ns");
    }

}
