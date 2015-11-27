package consistency.type;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

import consistency.datastore.DataStore;
import consistency.type.factory.CommonDataStoreFactory;

public class EventuallyConsistentStore implements DataStore{

	//private DataStore primaryDataStore;
	private List<DataStore> dataStores;
	private int serverIndex = 0;
	private ExecutorService pool = null;
	
	 public EventuallyConsistentStore() {
		 dataStores = new ArrayList<DataStore>();
		 DataStore primaryDataStore = CommonDataStoreFactory.getPrimaryDataStore();
		 List<DataStore> secondaryDataStores = CommonDataStoreFactory.getSecondaryDataStores();
		 dataStores.add(primaryDataStore);
		 dataStores.addAll(secondaryDataStores);
		 pool = Executors.newFixedThreadPool(10);
		 
		 
	}
	
	 private DataStore pickAServer(){
			DataStore pickedServer = dataStores.get(serverIndex);
			serverIndex++;
			if(serverIndex >= dataStores.size()){
				serverIndex = 0;
			}
			return pickedServer;
		}

	@Override
	public List<String> read(String key) {
		//round robin server picking
		return pickAServer().read(key);
	}

	@Override
	public void write(String key, List<String> values) {
		
		long systemTime = System.nanoTime();
		
		int serverId =0;
		//check if values are existing	
		DataStore dataStore = pickAServer();
		serverId = dataStores.indexOf(dataStore);
		updateValue(key, values.get(0), dataStore, serverId,systemTime);
		replicate(serverId, key, values.get(0),systemTime);
		
	}

	private synchronized void updateValue(String key, String valueToWrite, DataStore dataStore, int serverId, long systemTime) {
		System.out.println("Trying to update value "+key+":"+valueToWrite+":"+systemTime+":"+serverId);
		List<String> existingValues = dataStore.read(key);
		List<String> updatedValues = new ArrayList<String>();
		boolean updatedWithNewValue = false;
		if(existingValues != null && existingValues.size()>0){
			for (String value : existingValues) {
				if(updateExisting(value, updatedValues, serverId, valueToWrite, systemTime)){
					updatedWithNewValue = true;
				}else{
					updatedValues.add(value);
				}
			}
		}
		
		if(!updatedWithNewValue){
			String value = markValueWithServerDetails(valueToWrite, serverId,systemTime);
			updatedValues.add(value);
		}
		if(updatedValues.size() > 0){
			dataStore.write(key, updatedValues);
			System.out.println("Updated value "+key+":"+valueToWrite+":"+systemTime+":"+serverId);
		}
		
	}
	
	private String markValueWithServerDetails(String valueToWrite, int serverId, long systemTime){
		return valueToWrite+":Server"+serverId+":"+systemTime;
	}
	
	private boolean updateExisting(String value, List<String> updatedValues, int serverId, String valueToWrite,long systemTime){
		String[] split = value.split(":");
		if(split[1].equals("Server"+serverId)){
			if(Long.parseLong(split[2]) < systemTime){
				value = markValueWithServerDetails(valueToWrite, serverId, systemTime);
				updatedValues.add(value);
			}
			return true;
		}
		return false;
	}
	
	private void replicate(int primaryServerId,String key, String value, long systemTime){
		for (DataStore secondaryDataStore : dataStores) {
			int serverId = dataStores.indexOf(secondaryDataStore);
			if(serverId != primaryServerId){
				
				Runnable runnable = new Runnable() {
					
					@Override
					public void run() {
						System.out.println("Trying to replicate value "+key+":"+value+":"+systemTime+":"+primaryServerId +" to "+serverId);
						updateValue(key, value, secondaryDataStore, serverId, systemTime);
					}
				};
				//new Thread(runnable).start();
				pool.execute(runnable);
				
			}
		}
	}
	
	

}
