package consistency.proxy;

import java.util.Map;
import java.util.Map.Entry;

import consistency.datastore.DataStore;
import consistency.type.EventuallyConsistentStore;
import consistency.type.StronglyConsistentStore;

public class Proxy {
	
	public  static  enum  Consistency {EVENTUAL,STRONG};
	private DataStore dataStore;
	private Consistency consistencyType;
	private static Proxy eventual = new Proxy(Consistency.EVENTUAL);
	private static Proxy strong = new Proxy(Consistency.STRONG);
	
	public synchronized static Proxy getProxy(Consistency consistency){
		if(consistency == Consistency.EVENTUAL){
			return eventual;
		}else if(consistency == Consistency.STRONG){
			return strong;
		}
		return null;
		
	}
	
	
	private Proxy(Consistency consistencyType) {
		this.consistencyType = consistencyType;
		if(consistencyType == Consistency.EVENTUAL){
			dataStore = new EventuallyConsistentStore();
		}else if(consistencyType == Consistency.STRONG){
			dataStore = new StronglyConsistentStore();
		}
	}
	
	public void write(String key, String value){
		dataStore.write(key, value);
	}
	
	public String read(String key){
		if(consistencyType == Consistency.EVENTUAL){
			Map<String, String> allValues = dataStore.readAllValues(key);
			String finalValue = null ;
			long timeStamp = 0;
			for (Entry<String, String> entry : allValues.entrySet()) {
				String[] splitValues = entry.getValue().split(":");
				long tempTimeStamp = Long.parseLong(splitValues[1]);
				if(tempTimeStamp > timeStamp){
					timeStamp = tempTimeStamp;
					finalValue = splitValues[0];
				}
			}
			return finalValue;
		}
		return dataStore.read(key);
	}
	

}
