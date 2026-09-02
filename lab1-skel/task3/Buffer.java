import java.util.ArrayDeque;
import java.util.Queue;
import java.util.concurrent.locks.Condition;
import java.util.concurrent.locks.ReentrantLock;

	public class Buffer {

	private final Queue<Integer> queue = new ArrayDeque<>();
	private final ReentrantLock lock = new ReentrantLock();
	private final Condition notFull = lock.newCondition();
    private final Condition notEmpty = lock.newCondition();

	private int capacity = 0;
	private int putIndex = 0;
    private int takeIndex = 0;
	private boolean closed;
	private int items;

	public Buffer(int size) {
		// TODO
			this.capacity = size;	
		}

	void add(int i) {
		// TODO
		lock.lock();
		try {
			
			if (closed) {
                throw new IllegalStateException("Buffer is closed.");
            }

			// while the list is full and not closed, wait until not full 
			while (!closed && queue.size()==capacity){
				try {
                    notFull.await();
                } catch (InterruptedException e) {
                    Thread.currentThread().interrupt();
                    throw new RuntimeException("Thread interrupted while waiting to add", e);
                }
			}

			// if close and empty throw exceptions 
			if (closed) {
                throw new IllegalStateException("Buffer was closed while waiting to add.");
            }

			// insert 
			queue.add(i);
			notEmpty.signal();

		} finally{

			lock.unlock();

		}


	}


	public int remove() {
		// TODO

			lock.lock();

			try {
				
				while (queue.isEmpty() && !closed) {
					try {
                    	notEmpty.await();
                	} catch (InterruptedException e) {
                    	Thread.currentThread().interrupt();
    					throw new RuntimeException("Thread was interrupted while waiting to remove", e);
                	}          
				}	

				if(queue.isEmpty() && closed){
					throw new IllegalStateException("Buffer was closed while waiting to remove.");
	
				}

				int item=queue.remove();
				notFull.signal();
				return item;


			} finally {
				lock.unlock();
			}
	}

	public void close() {
				
		// TODO
		lock.lock();
		try {
			if(closed){
				throw new IllegalStateException("Buffer is already closed.");
			}
			closed=true;
			notFull.signalAll();
            notEmpty.signalAll();
		} finally {

			lock.unlock();

		}
	
	
	
	}
}
