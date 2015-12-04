package consistency.type;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

import consistency.datastore.DataStore;
import consistency.type.factory.CommonDataStoreFactory;

public class EventuallyConsistentStore implements DataStore {

	private List<DataStore> dataStores;
	private int serverIndex = 0;
	private ExecutorService pool = null;
	private Map<DataStore, Long> delay = new HashMap<DataStore, Long>();

	public EventuallyConsistentStore() {
		dataStores = new ArrayList<DataStore>();
		DataStore primaryDataStore = CommonDataStoreFactory.getPrimaryDataStore();
		List<DataStore> secondaryDataStores = CommonDataStoreFactory.getSecondaryDataStores();
		dataStores.add(primaryDataStore);
		dataStores.addAll(secondaryDataStores);
		pool = Executors.newCachedThreadPool();
		Runtime.getRuntime().addShutdownHook(new Thread() {
			public void run() {
				pool.shutdown();
			}
		});
		delay.put(primaryDataStore, 85L);
		delay.put(secondaryDataStores.get(0), 187L);
	}

	private DataStore pickAServer() {
		DataStore pickedServer = dataStores.get(serverIndex);
		serverIndex++;
		if (serverIndex >= dataStores.size()) {
			serverIndex = 0;
		}
		return pickedServer;
	}

	private void replicate(int originatingServerId, String key, String value, long systemTime) {
		for (DataStore secondaryDataStore : dataStores) {
			int serverId = dataStores.indexOf(secondaryDataStore);
			if (serverId != originatingServerId) {

				Runnable runnable = new Runnable() {

					@Override
					public void run() {
						writeMapToStore(secondaryDataStore, originatingServerId, key, value, systemTime);
					}
				};
				// new Thread(runnable).start();
				pool.execute(runnable);

			}
		}
	}

	@Override
	public String read(String key) {
		throw new UnsupportedOperationException("Eventual consistency returns a map of values");
	}

	@Override
	public void write(String key, String value) {

		writeToMap(key, null, value);
	}

	@Override
	public Map<String, String> readAllValues(String key) {
		DataStore server = pickAServer();
		try {
			Thread.sleep(delay.get(server));
		} catch (InterruptedException e1) {
			// TODO Auto-generated catch block
			e1.printStackTrace();
		}
		return server.readAllValues(key);
	}

	private boolean writeMapToStore(DataStore dataStore, int serverId, String key, String value, long systemTime) {
		try {
			Thread.sleep(delay.get(dataStore));
		} catch (InterruptedException e1) {
			// TODO Auto-generated catch block
			e1.printStackTrace();
		}
		return dataStore.writeToMap(key, "server" + serverId, value + ":" + systemTime);
	}

	@Override
	public boolean writeToMap(String key, String mapKey, String mapValue) {
		DataStore dataStore = pickAServer();
		int serverId = dataStores.indexOf(dataStore);
		long systemTime = System.nanoTime();
		if (writeMapToStore(dataStore, serverId, key, mapValue, systemTime)) {
			replicate(serverId, key, mapValue, systemTime);
			return true;
		}
		return false;
	}

}
