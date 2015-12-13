package consistency.type;

import java.util.concurrent.Executors;

import consistency.datastore.BackEndStore;

public class ConsistentPrefixStore extends StronglyConsistentStore {

	public ConsistentPrefixStore() {
		super();
		// start the queue
		Executors.newSingleThreadExecutor().execute(new Runnable() {
			public void run() {
				SimpleReplicationQueue.getInstance().run();
			}
		});
	}


	@Override
	public synchronized void write(String key, String value) {
		injectDelay(primaryDataStore);
		primaryDataStore.write(key, value);
		for (BackEndStore secondaryDataStore : secondaryDataStores) {
			//injectDelay(secondaryDataStore);
			//secondaryDataStore.write(key, value);
			SimpleReplicationQueue.getInstance().add(new SimpleReplicationRequest(secondaryDataStore, key, value));
		}

	}

	


}
