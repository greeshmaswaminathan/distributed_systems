package consistency.datastore;

import java.util.Map;

public interface DataStore extends BackEndStore {
	//bounded
	public String read(String key, int timeBoundInSeconds);
	//session aware
	public Map<String,String> readAllValues(String key, String clientId);
	
	
}
