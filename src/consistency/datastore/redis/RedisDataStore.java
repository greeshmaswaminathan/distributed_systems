package consistency.datastore.redis;

import java.util.Map;

import consistency.datastore.BackEndStore;
import redis.clients.jedis.Jedis;
import redis.clients.jedis.JedisPool;

public class RedisDataStore implements BackEndStore {

	private JedisPool pool;
	private int delay;

	public RedisDataStore(JedisPool pool, int delay) {
		this.pool = pool;
		Runtime.getRuntime().addShutdownHook(new Thread() {
			public void run() {
				pool.destroy();
			}
		});
		this.delay = delay;
	}

	@Override
	public String read(String key) {
		Jedis redisConnection = null;
		try {
			redisConnection = pool.getResource();
			return redisConnection.get(key);
		} finally {
			redisConnection.close();
		}

	}

	@Override
	public void write(String key, String value) {
		Jedis redisConnection = null;
		try {
			redisConnection = pool.getResource();
			redisConnection.set(key, value);
		} finally {
			redisConnection.close();
		}
	}

	@Override
	public Map<String, String> readAllValues(String key) {
		Jedis redisConnection = null;
		try {
			redisConnection = pool.getResource();
			return redisConnection.hgetAll(key);
		} finally {
			redisConnection.close();
		}
	}

	@Override
	public void writeToMap(String key, String mapKey, String mapValue) {
		Jedis redisConnection = null;
		try {
			redisConnection = pool.getResource();
			redisConnection.hset(key, mapKey, mapValue);
		} finally {
			
			redisConnection.close();
		}
	}

	@Override
	public int getDelay() {
		return delay;
	}

	
}
