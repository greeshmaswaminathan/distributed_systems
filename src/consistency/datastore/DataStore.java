package consistency.datastore;

import java.util.List;

public interface DataStore {
	
	public List<String> read(String key);
	public void write(String key, List<String> values);

}
