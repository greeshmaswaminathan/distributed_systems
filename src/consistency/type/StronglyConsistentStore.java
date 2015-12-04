package consistency.type;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import consistency.datastore.DataStore;
import consistency.type.factory.CommonDataStoreFactory;

public class StronglyConsistentStore implements DataStore{

	private DataStore primaryDataStore;
	private List<DataStore> secondaryDataStores;
	private int serverIndex = 0;
	private Map<DataStore, Long> delay = new HashMap<DataStore, Long>();
	
	public  StronglyConsistentStore() {
		primaryDataStore = CommonDataStoreFactory.getPrimaryDataStore();
		secondaryDataStores = CommonDataStoreFactory.getSecondaryDataStores();
		delay.put(primaryDataStore, 85L);
		delay.put(secondaryDataStores.get(0), 187L);
	}
	
	@Override
	public String read(String key) {
		//round robin server picking
		DataStore pickedServer =  pickAServer();
		try {
			Thread.sleep(delay.get(pickedServer));
		} catch (InterruptedException e1) {
			// TODO Auto-generated catch block
			e1.printStackTrace();
		}
		return pickedServer.read(key);
	}
	
	private DataStore pickAServer(){
		DataStore pickedServer = null;
		if(serverIndex == 0){
			pickedServer = primaryDataStore;
		}else{
			pickedServer = secondaryDataStores.get(serverIndex-1);
		}
		serverIndex++;
		if(serverIndex > secondaryDataStores.size()){
			serverIndex = 0;
		}
		return pickedServer;
	}

	@Override
	public synchronized void write(String key, String value) {
		try {
			Thread.sleep(delay.get(primaryDataStore));
		} catch (InterruptedException e1) {
			// TODO Auto-generated catch block
			e1.printStackTrace();
		}
		primaryDataStore.write(key, value);
		
		for (DataStore secondaryDataStore : secondaryDataStores) {
			try {
				Thread.sleep(delay.get(secondaryDataStore));
			} catch (InterruptedException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
			secondaryDataStore.write(key, value);
		}
	}

	@Override
	public Map<String, String> readAllValues(String key) {
		throw new UnsupportedOperationException("Strongly consistent store -- returns only a single value");
	}


	@Override
	public boolean writeToMap(String key, String mapKey, String mapValue) {
		throw new UnsupportedOperationException("Strongly consistent store -- maintaining only single value");
	}

	
	
	

}
