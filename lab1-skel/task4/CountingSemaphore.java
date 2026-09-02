public class CountingSemaphore {

	public volatile int semaphore = 0;
	int n;

	public int getSemaphore() {
		synchronized (this) {
			return semaphore;
		}
	}

	public CountingSemaphore(int n) {
		// TODO
		this.n = n;
		this.semaphore = n;
	}

	public void signal() throws InterruptedException {
		// TODO
		synchronized (this) {
			semaphore++;
			if (semaphore - 1 < 0) {
				notify();
			} else if (semaphore > n) {
				semaphore--;
				wait();
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
