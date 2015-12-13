package consistency.datastore;

import java.util.Map;

public interface BackEndStore {
	
	public String read(String key);
	public void write(String key, String value);
	
	public Map<String,String> readAllValues(String key);
	public void writeToMap(String key, String mapKey, String mapValue);
	
	public int getDelay();

}
