package consistency.type;

import java.util.concurrent.BlockingQueue;
import java.util.concurrent.LinkedBlockingQueue;

public class SimpleReplicationQueue {

	private static final SimpleReplicationQueue instance = new SimpleReplicationQueue();
	private BlockingQueue<SimpleReplicationRequest> queue = new LinkedBlockingQueue<SimpleReplicationRequest>();
	private boolean shutDown;
	
	private SimpleReplicationQueue(){
		Runtime.getRuntime().addShutdownHook(new Thread() {
			public void run() {
				shutDown = true;
				//System.out.println("Shutting down");
			}
		});
	}
	
	public static SimpleReplicationQueue getInstance(){
		return instance;
	}
	
	public void add(SimpleReplicationRequest request){
		queue.add(request);
		//System.out.println("Queuing replication:"+request.getKey()+":"+request.getMapKey()+":"+request.getMapValue());
	}
	
	public void run(){
		while(!shutDown){
			try {
				SimpleReplicationRequest request = queue.take();
				//System.out.println("Replicating:"+request.getKey()+":"+request.getMapKey()+":"+request.getMapValue());
				request.getTargetDataStore().write(request.getKey(), request.getValue());;
			} catch (InterruptedException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		}
	}

}
