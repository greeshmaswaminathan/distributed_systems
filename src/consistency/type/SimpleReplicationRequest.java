package consistency.type;

import consistency.datastore.BackEndStore;

public class SimpleReplicationRequest {

	private String key;
	private String value;
	private BackEndStore targetDataStore;
	
	public SimpleReplicationRequest(BackEndStore targetDataStore,String key, String value) {
		this.key = key;
		this.value = value;
		this.targetDataStore = targetDataStore;
	}

	public String getKey() {
		return key;
	}

	public String getValue() {
		return value;
	}

	public void setValue(String value) {
		this.value = value;
	}

	public BackEndStore getTargetDataStore() {
		return targetDataStore;
	}


}
