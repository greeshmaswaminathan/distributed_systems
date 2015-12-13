package consistency.type;

import org.junit.Assert;
import org.junit.Test;

public class EventualReplicationQueueTest {

	@Test
	public void testIfSingleton() {
		EventualReplicationQueue instance1 = EventualReplicationQueue.getInstance();
		EventualReplicationQueue instance2 = EventualReplicationQueue.getInstance();
		org.junit.Assert.assertTrue(instance1 == instance2);
	}

	@Test
	public void testWithThreads() {
		SomeThread thread1 = new SomeThread(1);
		SomeThread thread2 = new SomeThread(2);
		SomeThread thread3 = new SomeThread(3);
		new Thread(thread1).start();
		new Thread(thread2).start();
		new Thread(thread3).start();
		Assert.assertTrue(thread1.getInstance() == thread2.getInstance());
		Assert.assertTrue(thread2.getInstance() == thread3.getInstance());
	}

	class SomeThread implements Runnable {

		private EventualReplicationQueue instance;
		private int id;

		public SomeThread(int id) {
			this.id = id;
		}

		@Override
		public void run() {
			System.out.println("In thread " + id);
			instance = EventualReplicationQueue.getInstance();
		}

		public EventualReplicationQueue getInstance() {
			return instance;
		}

	}
}
