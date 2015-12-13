package consistency.type;

import java.util.Map;

import consistency.datastore.AbstractDataStore;
import consistency.datastore.BackEndStore;

public class StronglyConsistentStore extends AbstractDataStore {
	

	public StronglyConsistentStore() {
		initDataStores();
	}

	@Override
	public String read(String key) {
		// round robin server picking
		BackEndStore pickedServer = pickAServer();
		injectDelay(pickedServer);
		return pickedServer.read(key);
	}

	@Override
	public synchronized void write(String key, String value) {
		injectDelay(primaryDataStore);
		primaryDataStore.write(key, value);
		for (BackEndStore secondaryDataStore : secondaryDataStores) {
			injectDelay(secondaryDataStore);
			secondaryDataStore.write(key, value);
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
