package consistency.type.factory;

import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.util.List;
import java.util.Properties;

import consistency.datastore.BackEndStore;

public class CommonDataStoreFactory {
	
	private static DataStoreFactory datastoreFactory;
	
	static{
		Properties properties = new Properties();
		try {
			properties.load(new FileInputStream("datastore.properties"));
			String dataStore = (String)properties.get("datastore");
			if("redis".equals(dataStore)){
				datastoreFactory = new RedisDataStoreFactory();
			}
		} catch (FileNotFoundException e) {
			//unlikely
		} catch (IOException e) {
			//unlikely
		}
	}

	public static BackEndStore getPrimaryDataStore() {
		return datastoreFactory.getPrimaryDataStore();
	}

	public static List<BackEndStore> getSecondaryDataStores() {
		return datastoreFactory.getSecondaryDataStores();
	}

}
