package consistency.proxy;

import java.util.Map;

import consistency.datastore.DataStore;
import consistency.type.BoundedStaleness;
import consistency.type.ConsistentPrefixStore;
import consistency.type.EventuallyConsistentStore;
import consistency.type.MonotonicReadsStore;
import consistency.type.ReadMyWritesStore;
import consistency.type.SimpleEventualStore;
import consistency.type.StronglyConsistentStore;

public class Proxy implements DataStore{
	
	public  static  enum  Consistency {EVENTUAL,SIMPLE_EVENTUAL,STRONG, CONSISTENT_PREFIX, BOUNDED_STALENESS, READ_MY_WRITES, MONOTONIC_READS};
	private DataStore dataStore;
	private Consistency consistencyType;
	private static Proxy eventual = new Proxy(Consistency.EVENTUAL);
	private static Proxy strong = new Proxy(Consistency.STRONG);
	private static Proxy consistentPrefix = new Proxy(Consistency.CONSISTENT_PREFIX);
	private static Proxy boundedStaleness = new Proxy(Consistency.BOUNDED_STALENESS);
	private static Proxy readMyWrites = new Proxy(Consistency.READ_MY_WRITES);
	private static Proxy monotonicReads = new Proxy(Consistency.MONOTONIC_READS);
	private static Proxy simpleEventualReads = new Proxy(Consistency.SIMPLE_EVENTUAL);
	
	public synchronized static Proxy getProxy(Consistency consistency){
		if(consistency == Consistency.EVENTUAL){
			return eventual;
		}else if(consistency == Consistency.STRONG){
			return strong;
		}else if(consistency == Consistency.CONSISTENT_PREFIX){
			return consistentPrefix;
		}else if(consistency == Consistency.BOUNDED_STALENESS){
			return boundedStaleness;
		}else if(consistency == Consistency.READ_MY_WRITES){
			return readMyWrites;
		}else if(consistency == Consistency.MONOTONIC_READS){
			return monotonicReads;
		}else if(consistency == Consistency.SIMPLE_EVENTUAL){
			return simpleEventualReads;
		}
		return null;
		
	}
	
	
	private Proxy(Consistency consistencyType) {
		this.consistencyType = consistencyType;
		if(consistencyType == Consistency.EVENTUAL){
			dataStore = new EventuallyConsistentStore();
		}else if(consistencyType == Consistency.STRONG){
			dataStore = new StronglyConsistentStore();
		}else if(consistencyType == Consistency.CONSISTENT_PREFIX){
			dataStore = new ConsistentPrefixStore();
		}else if(consistencyType == Consistency.BOUNDED_STALENESS){
			dataStore = new BoundedStaleness();
		}else if(consistencyType == Consistency.READ_MY_WRITES){
			dataStore = new ReadMyWritesStore();
		}else if(consistencyType == Consistency.MONOTONIC_READS){
			dataStore = new MonotonicReadsStore();
		}else if(consistencyType == Consistency.SIMPLE_EVENTUAL){
			dataStore = new SimpleEventualStore();
		}
	}
	
	public void write(String key, String value){
		dataStore.write(key, value);
	}
	
	public String read(String key){
		if(consistencyType == Consistency.STRONG || consistencyType == Consistency.CONSISTENT_PREFIX || consistencyType == Consistency.SIMPLE_EVENTUAL){
			return dataStore.read(key);
		}
		throw new UnsupportedOperationException();
	}


	@Override
	public Map<String, String> readAllValues(String key) {
		if(consistencyType == Consistency.EVENTUAL){
			return dataStore.readAllValues(key);
		}
		throw new UnsupportedOperationException();
	}


	@Override
	public void writeToMap(String key, String mapKey, String mapValue) {
		if(consistencyType == Consistency.EVENTUAL){
			 dataStore.writeToMap(key, mapKey, mapValue);
		}
		throw new UnsupportedOperationException();
	}


	@Override
	public String read(String key, int timeBoundInSeconds) {
		if(consistencyType == Consistency.BOUNDED_STALENESS){
			 return dataStore.read(key, timeBoundInSeconds);
		}
		throw new UnsupportedOperationException();
	}


	@Override
	public Map<String, String> readAllValues(String key, String clientId) {
		if(consistencyType == Consistency.READ_MY_WRITES || consistencyType == Consistency.MONOTONIC_READS){
			 return dataStore.readAllValues(key, clientId);
		}
		throw new UnsupportedOperationException();
	}


	@Override
	public int getDelay() {
		// TODO Auto-generated method stub
		return 0;
	}
	

}
