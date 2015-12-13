package consistency.type;

import java.util.HashMap;
import java.util.Map;

import consistency.datastore.BackEndStore;

public class MonotonicReadsStore extends EventuallyConsistentStore {

	
	private Map<String, BackEndStore>  clientSessions = new HashMap<>();

	public MonotonicReadsStore() {
		super();
		
	}
	

	
	
	@Override
	public Map<String,String> readAllValues(String key, String clientId){
		BackEndStore server = clientSessions.get(clientId);
		if(server == null){
			server = pickAServer();
			clientSessions.put(key, server);
		}
		injectDelay(server);
		return server.readAllValues(key);
	}

	
	
	
	

	

	

}
