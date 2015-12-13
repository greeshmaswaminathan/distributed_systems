package consistency.type;

import java.util.concurrent.BlockingQueue;
import java.util.concurrent.LinkedBlockingQueue;

public class EventualReplicationQueue {

	private static final EventualReplicationQueue instance = new EventualReplicationQueue();
	private BlockingQueue<EventualReplicationRequest> queue = new LinkedBlockingQueue<EventualReplicationRequest>();
	private boolean shutDown;
	
	private EventualReplicationQueue(){
		Runtime.getRuntime().addShutdownHook(new Thread() {
			public void run() {
				shutDown = true;
				//System.out.println("Shutting down");
			}
		});
	}
	
	public static EventualReplicationQueue getInstance(){
		return instance;
	}
	
	public void add(EventualReplicationRequest request){
		queue.add(request);
		//System.out.println("Queuing replication:"+request.getKey()+":"+request.getMapKey()+":"+request.getMapValue());
	}
	
	public void run(){
		while(!shutDown){
			try {
				EventualReplicationRequest request = queue.take();
				//System.out.println("Replicating:"+request.getKey()+":"+request.getMapKey()+":"+request.getMapValue());
				request.getTargetDataStore().writeToMap(request.getKey(), request.getMapKey(), request.getMapValue());
			} catch (InterruptedException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		}
	}

}
