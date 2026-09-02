public class CountingSemaphore {

	volatile int semaphore = 0;
	public int n;

	public int getSemaphore() {
		synchronized (this) {
			return semaphore;
		}
	}

	public CountingSemaphore(int n) {
		// TODO
		this.n = n;
	}

	public void signal() throws InterruptedException {
		// TODO
		synchronized (this) {
			semaphore++;
			if (semaphore > n) {
				semaphore--;
				wait();
			} else if (semaphore - 1 < 0) {
				notify();
			}
		}
	}

	public void s_wait() throws InterruptedException {
		// TODO
		synchronized (this) {
			semaphore--;
			if (semaphore < 0) {
				wait();
			}
		}
	}
}
