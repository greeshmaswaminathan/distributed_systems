package consistency.datastore.redis;

import java.util.ArrayList;
import java.util.List;

import org.junit.Assert;
import org.junit.BeforeClass;
import org.junit.Test;

import redis.clients.jedis.JedisPool;
import redis.clients.jedis.JedisPoolConfig;

public class RedisDataStoreTest {
	
	private static RedisDataStore redisDataStore;
	
	@BeforeClass
	public static void init(){
		redisDataStore = new RedisDataStore(new JedisPool(new JedisPoolConfig(), "192.168.99.100",6379));
	}


	@Test
	public void testReadWrite() {
		List<String> values = new ArrayList<String>();
		values.add("Junit1");
		values.add("Junit2");
		redisDataStore.write("Test1",values  );
		List<String> valuesFromRedis = redisDataStore.read("Test1");
		Assert.assertTrue(valuesFromRedis.size() == 2);
		Assert.assertTrue(valuesFromRedis.get(0).equals("Junit1"));
		Assert.assertTrue(valuesFromRedis.get(1).equals("Junit2"));
		
		List<String> newValues = new ArrayList<>();
		newValues.add("Junit3");
		redisDataStore.write("Test1", newValues);
		valuesFromRedis = redisDataStore.read("Test1");
		Assert.assertTrue(valuesFromRedis.size() == 1);
		Assert.assertTrue(valuesFromRedis.get(0).equals("Junit3"));
	}
	
	

}
