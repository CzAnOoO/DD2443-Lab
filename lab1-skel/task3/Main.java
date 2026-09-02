public class Main {
	public static class Producer implements Runnable {
		
		private final Buffer buffer;
		public Producer(Buffer buffer){
			this.buffer= buffer;
		}
		
		public void run() {
				for (int i = 0; i < 1_000_000; i++) {
					buffer.add(i);
            	}
				buffer.close();
			
		}
	}

	public static class Consumer implements Runnable {
		 
		private final Buffer buffer;

		public Consumer(Buffer buffer){
				this.buffer = buffer;
		}
		
		public void run() {

			try {
            while (true) {
                int item = buffer.remove();
                System.out.println("This is the removed integer : " + item);
            }
        	} catch (IllegalStateException e) {
            // Buffer closed and completely drained -> exit loop cleanly
        	}
		}
	}

	public static void main(String [] args) {

		Buffer buffer = new Buffer(100);
		Thread producerThread = new Thread(new Producer(buffer));
        Thread consumThread = new Thread(new Consumer(buffer));

		consumThread.start();
		producerThread.start();
		 try{
            producerThread.join();
            consumThread.join();

        }catch (InterruptedException e) {
                e.printStackTrace();
            }

	}
}
