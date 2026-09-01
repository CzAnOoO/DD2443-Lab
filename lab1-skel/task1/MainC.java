public class MainC {

	static volatile int a;

	public static class Incrementer implements Runnable {
		public synchronized void run() {
			for (int i = 0; i < 1_000_000; i++) {
				a++;
			}
		}
	}

	static long run_experiment(int n) {

		long startTime = System.nanoTime();

		/* ----------- task1b  ----------- */
		Thread ts[] = new Thread[n];

		Runnable task1 = new Incrementer();

		for (int i = 0; i < n; i++) {
			ts[i] = new Thread(task1);
			ts[i].start();
		}

		for (int i = 0; i < n; i++) {
			try {
				ts[i].join();
			} catch (InterruptedException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		}
		/* ----------- ------ ----------- */
		long endTime = System.nanoTime();
		// System.out.println("current a :" + a);
		a = 0;

		return endTime - startTime;
	}

	public static void main(String[] args) {
		int x = 2;
		int y = 1;

		if (args.length > 0) {
			x = Integer.parseInt(args[0]);
			y = Integer.parseInt(args[1]);
		}

		for (int n = 1; n <= 64; n *= 2) {
			for (int i = 0; i < x; i++) {
				run_experiment(n);
			}

			long time = 0;
			for (int i = 0; i < y; i++) {
				time += run_experiment(n);
			}
			time = time / (long) y;

			// System.out.println("time(ms): " + time);
			System.out.println(n + "  " + time);
		}
	}
}
