package consistency.client;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.PrintStream;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

import consistency.proxy.Proxy.Consistency;

public class ClientTest {

	
	public static void main(String[] arg) throws InterruptedException, FileNotFoundException{
		
		System.setOut(new PrintStream(new FileOutputStream(new File("sysout"))));
		
		
		ExecutorService newCachedThreadPool = Executors.newCachedThreadPool();
		
		newCachedThreadPool.execute(new Client(1,Consistency.CONSISTENT_PREFIX));
		newCachedThreadPool.execute(new Client(2,Consistency.CONSISTENT_PREFIX));
		newCachedThreadPool.execute(new Client(3,Consistency.CONSISTENT_PREFIX));
		newCachedThreadPool.execute(new Client(4,Consistency.CONSISTENT_PREFIX));
		newCachedThreadPool.execute(new Client(5,Consistency.CONSISTENT_PREFIX));
		
		/*newCachedThreadPool.execute(new Client(1,Consistency.EVENTUAL));
		newCachedThreadPool.execute(new Client(2,Consistency.EVENTUAL));
		newCachedThreadPool.execute(new Client(3,Consistency.EVENTUAL));
		newCachedThreadPool.execute(new Client(4,Consistency.EVENTUAL));
		newCachedThreadPool.execute(new Client(5,Consistency.EVENTUAL));*/
		
		/*newCachedThreadPool.execute(new Client(1,Consistency.BOUNDED_STALENESS));
		newCachedThreadPool.execute(new Client(2,Consistency.BOUNDED_STALENESS));
		newCachedThreadPool.execute(new Client(3,Consistency.BOUNDED_STALENESS));
		newCachedThreadPool.execute(new Client(4,Consistency.BOUNDED_STALENESS));
		newCachedThreadPool.execute(new Client(5,Consistency.BOUNDED_STALENESS));*/
		
		/*newCachedThreadPool.execute(new Client(1,Consistency.READ_MY_WRITES));
		newCachedThreadPool.execute(new Client(2,Consistency.READ_MY_WRITES));
		newCachedThreadPool.execute(new Client(3,Consistency.READ_MY_WRITES));
		newCachedThreadPool.execute(new Client(4,Consistency.READ_MY_WRITES));
		newCachedThreadPool.execute(new Client(5,Consistency.READ_MY_WRITES));*/
		
		/*newCachedThreadPool.execute(new Client(1,Consistency.MONOTONIC_READS));
		newCachedThreadPool.execute(new Client(2,Consistency.MONOTONIC_READS));
		newCachedThreadPool.execute(new Client(3,Consistency.MONOTONIC_READS));
		newCachedThreadPool.execute(new Client(4,Consistency.MONOTONIC_READS));
		newCachedThreadPool.execute(new Client(5,Consistency.MONOTONIC_READS));*/
		
		/*newCachedThreadPool.execute(new Client(1,Consistency.SIMPLE_EVENTUAL));
		newCachedThreadPool.execute(new Client(2,Consistency.SIMPLE_EVENTUAL));
		newCachedThreadPool.execute(new Client(3,Consistency.SIMPLE_EVENTUAL));
		newCachedThreadPool.execute(new Client(4,Consistency.SIMPLE_EVENTUAL));
		newCachedThreadPool.execute(new Client(5,Consistency.SIMPLE_EVENTUAL));*/
		
		
		/*newCachedThreadPool.execute(new Client(1,Consistency.STRONG));
		newCachedThreadPool.execute(new Client(2,Consistency.STRONG));
		newCachedThreadPool.execute(new Client(3,Consistency.STRONG));
		newCachedThreadPool.execute(new Client(4,Consistency.STRONG));
		newCachedThreadPool.execute(new Client(5,Consistency.STRONG));*/
		
		Runtime.getRuntime().addShutdownHook(new Thread() {
		    public void run() { 
		    	newCachedThreadPool.shutdown(); 
		    	}
		});
		
	}

	
	
}
