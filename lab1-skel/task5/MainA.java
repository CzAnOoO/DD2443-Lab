public class MainA {

	static volatile long lastMealTime = System.nanoTime();

	public static class Philosopher implements Runnable {
		private Object leftchopstick; 
		private Object rightchopstick; 

		public Philosopher(Object leftchopstick,Object rightchopstick ){
		
			this.leftchopstick=leftchopstick;
			this.rightchopstick= rightchopstick;

		}


		public void think(){

				try {
        	    Thread.sleep((int) (Math.random() * 10));
        	} catch (InterruptedException e) {
        	    Thread.currentThread().interrupt();
        	    return;
        	}

		}

		public void eat(){

			try {
				Thread.sleep(1);
					lastMealTime = System.nanoTime();						
					} catch (Exception e) {
						Thread.currentThread().interrupt();
						return;					
					}


		}



		public void run() {

			while(true){

				think();

				synchronized(this.leftchopstick){


					// forces the phisopher to hold on to the left chopstick and wait before taking the right one: 
					//this is here to try to force a deadlock 
					try {
               		 	Thread.sleep((int) (Math.random() * 7));
            		} catch (InterruptedException e) {
                		Thread.currentThread().interrupt();
						return;
            		}



					synchronized(this.rightchopstick){
					
						eat();

					}


				}
			}

		}
	}

	public static void main(String [] args) {
			int n = 15;
			Object[] chopsticks = new Object[n];
			Thread[] philosphers= new Thread[n];

			long startTime = System.nanoTime();
			lastMealTime = startTime;

			for(int i =0; i<n;i++){
				chopsticks[i]=new Object();
			}

			for (int i=0 ;i<n;i++){

				Object left = chopsticks[i];
            	Object right = chopsticks[(i + 1) % n];

				philosphers[i]= new Thread(new Philosopher(left,right));
				philosphers[i].start();
			}


			while (System.nanoTime() - lastMealTime < 1_000_000_000L) {
            	try {
					Thread.sleep(100);

				} catch (Exception e) {
					return;
				}
				
        	}

			System.out.println("Time to deadlock for " + n + " philosophers is " + (lastMealTime - startTime) + " ns");


	}
}
