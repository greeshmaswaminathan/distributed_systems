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
		 pool = Executors.newCachedThreadPool();
		 Runtime.getRuntime().addShutdownHook(new Thread() {
			    public void run() { 
			    	pool.shutdown(); 
			    	}
			});
		 
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
		boolean updated = updateValue(key, values.get(0), dataStore, serverId,systemTime);
		if(updated) {
			replicate(serverId, key, values.get(0),systemTime);
		}
		
	}

	private synchronized boolean updateValue(String key, String valueToWrite, DataStore dataStore, int serverId, long systemTime) {
		//System.out.println("Trying to update value "+key+":"+valueToWrite+":"+systemTime+":"+serverId);
		List<String> existingValues = dataStore.read(key);
		List<String> revisedValues = new ArrayList<String>();
		
		boolean isNewValueWritten = false;
		if(existingValues != null && existingValues.size()>0){
			for (String value : existingValues) {
				String[] split = value.split(":");
				boolean isSameServer = isSameServerEntry(split[1],serverId);
				if(isSameServer){
					if(isOutDated(split[2], serverId,  systemTime)){
						return false;
					}else{
						revisedValues.add(appendValueWithServerDetails(valueToWrite, serverId, systemTime));
						isNewValueWritten = true;
					}
				}else{
					revisedValues.add(value);
				}
				
			}
		}
		
		if(!isNewValueWritten){
			String value = appendValueWithServerDetails(valueToWrite, serverId,systemTime);
			revisedValues.add(value);
		}
		
		if(revisedValues.size() > 0){
			dataStore.write(key, revisedValues);
			return true;
		}
		return false;
	}
	
	private boolean isSameServerEntry(String existingServerEntry, int serverId) {
		if(existingServerEntry.equals("Server"+serverId)){
			return true;
		}
		return false;
	}

	private String appendValueWithServerDetails(String valueToWrite, int serverId, long systemTime){
		return valueToWrite+":Server"+serverId+":"+systemTime;
	}
	
	private boolean isOutDated(String timeStampInStore, int serverId,long systemTime){
		if(Long.parseLong(timeStampInStore) > systemTime){
			//value = markValueWithServerDetails(valueToWrite, serverId, systemTime);
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
						//System.out.println("Trying to replicate value "+key+":"+value+":"+systemTime+":"+primaryServerId +" to "+serverId);
						updateValue(key, value, secondaryDataStore, primaryServerId, systemTime);
					}
				};
				//new Thread(runnable).start();
				pool.execute(runnable);
				
			}
		}
	}
	
	

}
