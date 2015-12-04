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
		redisDataStore.write("Test1","Junit1"  );
		String valuesFromRedis = redisDataStore.read("Test1");
		Assert.assertTrue(valuesFromRedis.equals("Junit1"));
		
		List<String> newValues = new ArrayList<>();
		newValues.add("Junit3");
		redisDataStore.write("Test1", "Junit3");
		valuesFromRedis = redisDataStore.read("Test1");
		Assert.assertTrue(valuesFromRedis.equals("Junit3"));
		
		
	}
	
	

}
