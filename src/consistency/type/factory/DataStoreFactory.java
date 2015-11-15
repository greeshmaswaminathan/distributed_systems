package consistency.type.factory;

import java.util.List;

import consistency.datastore.DataStore;

public interface DataStoreFactory {
	
	public DataStore getPrimaryDataStore() ;

	public List<DataStore> getSecondaryDataStores() ;


}
