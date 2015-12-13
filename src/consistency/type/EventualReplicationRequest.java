package consistency.type;

import consistency.datastore.BackEndStore;

public class EventualReplicationRequest {

	private String key;
	private String mapKey;
	private String mapValue;
	private BackEndStore targetDataStore;
	
	public EventualReplicationRequest(BackEndStore targetDataStore,String key, String mapKey, String mapValue) {
		this.key = key;
		this.mapKey = mapKey;
		this.mapValue = mapValue;
		this.targetDataStore = targetDataStore;
	}

	public String getKey() {
		return key;
	}


	public String getMapKey() {
		return mapKey;
	}


	public String getMapValue() {
		return mapValue;
	}


	public BackEndStore getTargetDataStore() {
		return targetDataStore;
	}


}
