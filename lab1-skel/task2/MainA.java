public class MainA {

	    static volatile int sharedInt=0;


	public static class Incrementer implements Runnable {
		public void run() {
 				for (int i = 0; i < 1_000_000; i++) {
                    sharedInt++;
                }

		}
	}

	public static class Printer implements Runnable {
		public void run() {

            System.out.println("sharedInt: " + sharedInt);   

		}
	}

	public static void main(String [] args) {


		int totalRuns = 10;

        for(int i =0;i<totalRuns;i++){
        sharedInt=0;

        Thread incrementingThread = new Thread(new Incrementer());
        Thread printingThread = new Thread(new Printer());

        incrementingThread.start();
        printingThread.start();

        try{
            incrementingThread.join();
            printingThread.join();

        }catch (InterruptedException e) {
                e.printStackTrace();
            }

        
        

        }



	}
}
