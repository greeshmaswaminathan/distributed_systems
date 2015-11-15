package consistency.type.factory;

import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Properties;

import consistency.datastore.DataStore;
import consistency.datastore.redis.RedisDataStore;
import redis.clients.jedis.JedisPool;
import redis.clients.jedis.JedisPoolConfig;

public class RedisDataStoreFactory implements DataStoreFactory {

	private DataStore primary;
	private List<DataStore> secondaryList;

	public RedisDataStoreFactory() {
		Properties properties = new Properties();
		try {
			properties.load(new FileInputStream("redis.properties"));
			primary = new RedisDataStore(new JedisPool(new JedisPoolConfig(), properties.getProperty("primary.ip"),
					Integer.parseInt(properties.getProperty("primary.port"))));
			secondaryList = new ArrayList<>();
			int secondaryCount = Integer.parseInt(properties.getProperty("secondary.count"));
			for (int index = 0; index < secondaryCount; index++) {
				secondaryList.add(
						new RedisDataStore(new JedisPool(new JedisPoolConfig(), properties.getProperty("secondary"+(index+1)+".ip"),
								Integer.parseInt(properties.getProperty("secondary"+(index+1)+".port")))));
			}

		} catch (FileNotFoundException e) {
			// unlikely
		} catch (IOException e) {
			// unlikely
		}
	}

	@Override
	public DataStore getPrimaryDataStore() {
		return primary;
	}

	@Override
	public List<DataStore> getSecondaryDataStores() {
		return secondaryList;
	}

}
