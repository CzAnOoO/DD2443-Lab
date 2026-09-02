public class Main {

	static volatile int semaphore = 0;

	public static class Runner implements Runnable {

		CountingSemaphore semaphore;

		public void setSemaphore(CountingSemaphore s) {
			this.semaphore = s;
		}

		public void run() {
			try {
				semaphore.signal();
				System.out.println("Thread start:" + Thread.currentThread().threadId() + "start");
				Thread.sleep(100);
				System.out.println("Thread: " + Thread.currentThread().threadId() + "finish");
			} catch (InterruptedException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		}
	}

	public static void main(String[] args) throws InterruptedException {
		int n = 3;
		int x = 5;
		CountingSemaphore semaphore = new CountingSemaphore(n);

		Runner r = new Runner();
		r.setSemaphore(semaphore);

		Thread ts[] = new Thread[x];

		for (int i = 0; i < x; i++) {
			ts[i] = new Thread(r);
			ts[i].start();
		}
		Thread.sleep(50);
		System.out.println("Current semaphore: " + semaphore.getSemaphore());

		for (int i = 0; i < x; i++) {
			ts[i].join();
		}

	}
}
