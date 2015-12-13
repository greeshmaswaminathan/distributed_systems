package consistency.type.factory;

import java.util.List;

import consistency.datastore.BackEndStore;

public interface DataStoreFactory {
	
	public BackEndStore getPrimaryDataStore() ;

	public List<BackEndStore> getSecondaryDataStores() ;


}
