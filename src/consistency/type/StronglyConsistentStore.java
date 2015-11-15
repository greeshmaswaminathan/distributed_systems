package consistency.type;

import java.util.List;

import consistency.datastore.DataStore;
import consistency.type.factory.CommonDataStoreFactory;

public class StronglyConsistentStore implements DataStore{

	private DataStore primaryDataStore;
	private List<DataStore> secondaryDataStores;
	private int serverIndex = 0;
	
	public  StronglyConsistentStore() {
		primaryDataStore = CommonDataStoreFactory.getPrimaryDataStore();
		secondaryDataStores = CommonDataStoreFactory.getSecondaryDataStores();
	}
	
	@Override
	public List<String> read(String key) {
		//round robin server picking
		return pickAServer().read(key);
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
	public synchronized void write(String key, List<String> values) {
		primaryDataStore.write(key, values);
		for (DataStore secondaryDataStore : secondaryDataStores) {
			secondaryDataStore.write(key, values);
		}
	}

	
	
	

}
