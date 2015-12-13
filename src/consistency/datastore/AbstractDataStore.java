package consistency.datastore;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Random;

import consistency.type.factory.CommonDataStoreFactory;

public abstract class AbstractDataStore implements DataStore{
	
	protected BackEndStore primaryDataStore;
	protected List<BackEndStore> secondaryDataStores;
	protected List<BackEndStore> dataStores;
	
	public void initDataStores(){
		dataStores = new ArrayList<BackEndStore>();
		primaryDataStore = CommonDataStoreFactory.getPrimaryDataStore();
		secondaryDataStores = CommonDataStoreFactory.getSecondaryDataStores();
		dataStores.add(primaryDataStore);
		dataStores.addAll(secondaryDataStores);
	}
	
	public BackEndStore pickAServer() {
		Random randomizer = new Random();
		BackEndStore pickedServer = dataStores.get(randomizer.nextInt(dataStores.size()));
		return pickedServer;
	}
	
	public void injectDelay(BackEndStore server){
		try {
			Thread.sleep(server.getDelay());
		} catch (InterruptedException e1) {
			// TODO Auto-generated catch block
			e1.printStackTrace();
		}
	}
	
	@Override
	public int getDelay() {
		// TODO Auto-generated method stub
		return 0;
	}
	
	public String read(String key, int timeBoundInSeconds){
		throw new UnsupportedOperationException();
	}
	
	public Map<String,String> readAllValues(String key){
		throw new UnsupportedOperationException();
	}
	
	public Map<String,String> readAllValues(String key, String clientId){
		throw new UnsupportedOperationException();
	}
	
	@Override
	public void writeToMap(String key, String mapKey, String mapValue) {
		throw new UnsupportedOperationException();
		
	}

}
