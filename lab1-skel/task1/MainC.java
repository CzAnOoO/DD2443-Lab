public class MainC {

	static volatile int a;

	public static class Incrementer implements Runnable {
		public void run() {
			for (int i = 0; i < 1_000_000; i++) {
				synchronized (this) {
					a++;
				}
			}
		}
	}

	static long run_experiment(int n) {

		/* ----------- task1b  ----------- */
		Thread ts[] = new Thread[n];

		Runnable task1 = new Incrementer();

		long startTime = System.nanoTime();

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
		int x = 1;
		int y = 1;

		if (args.length > 1) {
			x = Integer.parseInt(args[0]);
			y = Integer.parseInt(args[1]);
		}

		// System.out.println(x);
		// System.out.println(y);

		for (int n = 1; n <= 64; n *= 2) {
			for (int i = 0; i < x; i++) {
				run_experiment(n);
			}

			long time = 0;

			/* Standard Deviation */
			// https://www.baeldung.com/java-calculate-standard-deviation 

			long nums[] = new long[y];
			for (int i = 0; i < y; i++) {
				nums[i] = run_experiment(n);
				time += nums[i];
			}
			double mean = (double) time / y; // mean

			double squareds = 0.0;
			double standardDeviation = 0.0;

			for (long num : nums) {
				squareds += Math.pow(num - mean, 2);
			}

			standardDeviation = Math.sqrt(squareds / y);
			// System.out.println("time(ms): " + time);
			System.out.println(n + "  " + mean + "   " + standardDeviation / mean);
		}
	}
}
