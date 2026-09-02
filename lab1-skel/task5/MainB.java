import java.util.concurrent.locks.ReentrantLock;
import java.util.Random;
import java.util.concurrent.locks.Condition;

public class MainB {

	final static int n = 6;

	static volatile Boolean chopsticks[] = new Boolean[n]; // true = Upplockad, false = på bordet
	// static ReentrantLock[] locks = new ReentrantLock[n];
	static ReentrantLock lock = new ReentrantLock();
	static Condition[] notPicked = new Condition[n];

	public static class Philosopher implements Runnable {
		int id;
		int left;
		int right;

		Philosopher(int i) {
			this.id = i;
			this.left = this.id;
			this.right = (this.id + 1) % n;
		}

		boolean canPick() throws InterruptedException {
			lock.lock();
			try {
				// väntar om en av ätpinnarna på ena sidan är upptagen
				while (chopsticks[left] || chopsticks[right]) {
					notPicked[id].await();
				}

				chopsticks[left] = true;
				chopsticks[right] = true;
			} finally {
				lock.unlock();
			}
			return true;
		}

		void eat() throws InterruptedException {
			if (canPick()) {
				lock.lock();
				try {
					System.out.println(Thread.currentThread().getName() + "eat");
					chopsticks[left] = false;
					chopsticks[right] = false;

					notPicked[(id - 1 + n) % n].signal(); // left
					notPicked[(id + 1) % n].signal(); // right

				} finally {
					lock.unlock();
				}
				Thread.sleep((int) (Math.random() * 201) + 200);
			}
		}

		public void run() {
			while (true) {
				try {
					eat();
				} catch (InterruptedException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
			}
		}

	}

	public static void main(String[] args) throws InterruptedException {

		Thread ts[] = new Thread[n];

		for (int i = 0; i < n; i++) {
			// locks[i] = new ReentrantLock();
			chopsticks[i] = false;
			notPicked[i] = lock.newCondition();
		}

		for (int i = 0; i < n; i++) {
			Runnable r = new Philosopher(i);
			ts[i] = new Thread(r, "Philosopher " + String.valueOf(i));
			ts[i].start();
		}

		for (int i = 0; i < n; i++) {
			ts[i].join();
		}
	}
}
