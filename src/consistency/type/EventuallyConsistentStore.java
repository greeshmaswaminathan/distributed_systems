package consistency.type;

import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.util.Map;
import java.util.Properties;
import java.util.concurrent.Executors;

import consistency.datastore.AbstractDataStore;
import consistency.datastore.BackEndStore;

public class EventuallyConsistentStore extends AbstractDataStore {

	private boolean geoReplication;
	

	public EventuallyConsistentStore() {
		initDataStores();
		//start the queue
		Executors.newSingleThreadExecutor().execute(new Runnable() {
			public void run() {
				EventualReplicationQueue.getInstance().run();
			}
		});
		
		Properties properties = new Properties();
		try {
			properties.load(new FileInputStream("redis.properties"));
			geoReplication =  Boolean.parseBoolean(properties.get("georeplication").toString());
		} catch (FileNotFoundException e) {
			// unlikely
		} catch (IOException e) {
			// unlikely
		}
		
	}
	

	protected void replicate(int originatingServerId, String key,  String mapKey, String mapValue) {
		for (BackEndStore secondaryDataStore : dataStores) {
			int serverId = dataStores.indexOf(secondaryDataStore);
			if (serverId != originatingServerId) {

				EventualReplicationRequest request = new EventualReplicationRequest(secondaryDataStore,key, mapKey, mapValue);
				EventualReplicationQueue.getInstance().add(request);
				

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
		BackEndStore server = pickAServer();
		injectDelay(server);
		return server.readAllValues(key);
	}

	
	protected BackEndStore getServer(){
		/*if(geoReplication){
			return primaryDataStore;
		}*/
		return pickAServer();
	}
	
	@Override
	public void writeToMap(String key, String mapKey, String mapValue) {
		BackEndStore dataStore = getServer();
		int serverId = dataStores.indexOf(dataStore);
		long systemTime = System.nanoTime();
		String modifiedMapKey = "server" + serverId;
		String modifiedMapValue = mapValue+ ":" +systemTime;
		injectDelay(dataStore);
		//synchronized (key.intern()) {
			dataStore.writeToMap(key,modifiedMapKey, modifiedMapValue);
			replicate(serverId,key, modifiedMapKey, modifiedMapValue);
		//}
		
	}


	

	

}
