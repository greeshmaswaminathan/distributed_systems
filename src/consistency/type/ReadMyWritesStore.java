package consistency.type;

import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

import consistency.datastore.BackEndStore;

public class ReadMyWritesStore extends EventuallyConsistentStore {

	
	private Map<String, BackEndStore>  clientSessions = new ConcurrentHashMap<>();

	public ReadMyWritesStore() {
		super();
		
	}
	

	
	
	@Override
	public Map<String,String> readAllValues(String key, String clientId){
		BackEndStore server = clientSessions.get(clientId);
		if(server == null)
			server = pickAServer();
		injectDelay(server);
		return server.readAllValues(key);
	}

	
	
	
	@Override
	public void writeToMap(String key, String mapKey, String mapValue) {
		BackEndStore dataStore = pickAServer();
		int serverId = dataStores.indexOf(dataStore);
		long systemTime = System.nanoTime();
		String modifiedMapKey = "server" + serverId;
		String modifiedMapValue = mapValue+ ":" +systemTime;
		injectDelay(dataStore);
		//synchronized (key.intern()) {
			clientSessions.put(mapValue.split("-")[0], dataStore);
			dataStore.writeToMap(key,modifiedMapKey, modifiedMapValue);
			replicate(serverId,key, modifiedMapKey, modifiedMapValue);
		//}
		
	}


	

	

}
