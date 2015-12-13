package consistency.type;

import java.util.Map;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledFuture;
import java.util.concurrent.TimeUnit;

import consistency.datastore.AbstractDataStore;
import consistency.datastore.BackEndStore;

public class BoundedStaleness extends AbstractDataStore {

	private ScheduledFuture<?> schedulerResult;
	
	public BoundedStaleness() {
		initDataStores();
		Runnable runnable = new Runnable() {
			public void run() {
				BoundedReplicationQueue.getInstance().run();
			}
		};
		// start the queue
		schedulerResult = Executors.newSingleThreadScheduledExecutor().scheduleAtFixedRate(runnable, 50, 50, TimeUnit.MILLISECONDS);
	}

	
	@Override
	public String read(String key) {
		return read(key,  10);
	}
	
	@Override
	public String read(String key, int timeBoundInMilliSeconds) {
		BackEndStore pickAServerWithBound = pickAServerWithBound(timeBoundInMilliSeconds);
		injectDelay(pickAServerWithBound);
		return pickAServerWithBound.read(key);
	}
	
	
	
	private BackEndStore pickAServerWithBound(int bound){
		while(true){
			BackEndStore pickedServer = pickAServer();
			if(pickedServer != primaryDataStore){
				long delay = schedulerResult.getDelay(TimeUnit.MILLISECONDS);
				long uptoDateTo = (50 - delay);
				if(uptoDateTo <= bound){
					return pickedServer;
				}
			}else{
				return pickedServer;
			}
			//injectDelay(pickedServer);
		}
	}
	

	@Override
	public synchronized void write(String key, String value) {
		injectDelay(primaryDataStore);
		primaryDataStore.write(key, value);
		for (BackEndStore secondaryDataStore : secondaryDataStores) {
			//injectDelay(secondaryDataStore);
			//secondaryDataStore.write(key, value);
			BoundedReplicationQueue.getInstance().add(new SimpleReplicationRequest(secondaryDataStore, key, value));
		}

	}

	@Override
	public Map<String, String> readAllValues(String key) {
		throw new UnsupportedOperationException("Strongly consistent store -- returns only a single value");
	}

	@Override
	public void writeToMap(String key, String mapKey, String mapValue) {
		throw new UnsupportedOperationException("Strongly consistent store -- returns only a single value");

	}




	


}
