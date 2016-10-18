import java.util.Queue;
import java.util.concurrent.Semaphore;
import java.util.concurrent.ConcurrentLinkedQueue;

public final class BlockingQueue<T> {
	
	private	Queue<T> q = new ConcurrentLinkedQueue<>();
	private Semaphore mutex = new Semaphore(1, true);
	private Semaphore empty;
	private Semaphore full = new Semaphore(0, true);

	public BlockingQueue(int size){
		empty = new Semaphore(size);
	}

	public void enqueue(T t) throws InterruptedException{
		empty.acquire();
		mutex.acquire();
		q.add(t);
		mutex.release();
		full.release();
	}

	public T dequeue() throws InterruptedException{
		full.acquire();
		mutex.acquire();
		T result = q.remove();
		mutex.release();
		empty.release();
		return result;
	}

	public static void main(String[] args) throws Exception {
		BlockingQueue<Integer> queue = new BlockingQueue<>(100);
		Runnable r = () -> { // replace lambda if you don’t have access to Java 8
			for (int i = 0; i < 200; i++) {
				try {
					int n = queue.dequeue();
					System.out.println(n + " Removed");
					Thread.sleep(500);
				} catch (Exception e) {}
			}
		};
		Thread t = new Thread(r);
		t.start();
		for (int i = 0; i < 200; i++) {
			System.out.println("Adding " + i);
			queue.enqueue(i);
		}
	}

}
