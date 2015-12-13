package consistency.type;

import java.util.concurrent.Executors;

import consistency.datastore.AbstractDataStore;
import consistency.datastore.BackEndStore;

public class SimpleEventualStore extends AbstractDataStore{
	
	public SimpleEventualStore() {
		initDataStores();
		Executors.newSingleThreadExecutor().execute(new Runnable() {
			public void run() {
				SimpleReplicationQueue.getInstance().run();
			}
		});
	}

	@Override
	public String read(String key) {
		BackEndStore pickedServer = pickAServer();
		injectDelay(pickedServer);
		return pickedServer.read(key);
	}

	
	@Override
	public void write(String key, String value) {
		injectDelay(primaryDataStore);
		//synchronized (key.intern()) {
			primaryDataStore.write(key, value);
			for (BackEndStore secondaryDataStore : secondaryDataStores) {
				//injectDelay(secondaryDataStore);
				SimpleReplicationQueue.getInstance().add(new SimpleReplicationRequest(secondaryDataStore, key, value));
			}
		//}
		
		
	}

	

}
