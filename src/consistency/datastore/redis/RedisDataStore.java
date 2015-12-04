package consistency.datastore.redis;

import java.io.IOException;
import java.util.List;
import java.util.Map;

import consistency.datastore.DataStore;
import redis.clients.jedis.Jedis;
import redis.clients.jedis.JedisPool;
import redis.clients.jedis.Transaction;

public class RedisDataStore implements DataStore {

	private JedisPool pool;

	public RedisDataStore(JedisPool pool) {
		this.pool = pool;
		Runtime.getRuntime().addShutdownHook(new Thread() {
			public void run() {
				pool.destroy();
			}
		});
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
	public boolean writeToMap(String key, String mapKey, String mapValue) {
		while(true){
			try {
				boolean result = tryInTransaction(key, mapKey, mapValue);
				return result;
			} catch (FailedTransactionException e) {
				System.out.println("Failed transaction");
			}
		}
	}

	
	private boolean tryInTransaction(String key, String mapKey, String mapValue) throws FailedTransactionException{
		Jedis redisConnection = null;
		Transaction redisTransaction = null;
		
		try {
			redisConnection = pool.getResource();
			redisConnection.watch(key);
			String existingValue = redisConnection.hget(key, mapKey);
			redisTransaction = redisConnection.multi();
			if(existingValue!=null){
				String existingTimeStamp = existingValue.split(":")[1];
				if (Long.parseLong(existingTimeStamp) >= Long.parseLong(mapValue.split(":")[1])) {
					return false;
				}
			}
			redisTransaction.hset(key, mapKey, mapValue);
			List<Object> result = redisTransaction.exec();
			if(result == null)
				throw new FailedTransactionException();
			return true;
		} finally {
			try {
				if(redisTransaction !=null)
				redisTransaction.close();
			} catch (IOException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
			redisConnection.close();
		}
	}
	
	
	
	
	class FailedTransactionException extends Exception{

		/**
		 * 
		 */
		private static final long serialVersionUID = 8942319971527006464L;
		
	}
}
