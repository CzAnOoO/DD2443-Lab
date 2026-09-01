package lab1;

public class Task0 implements Runnable {
    @Override
    public void run() {
        System.out.println(Long.toString(Thread.currentThread().threadId()) + " :Hello world");
    }

    public static void main(String[] args) {
        Runnable task0 = new Task0();

        for (int i = 0; i < 5; i++) {
            Thread t = new Thread(task0);
            t.start();
        }

        System.out.println("Hello world");
    }

}