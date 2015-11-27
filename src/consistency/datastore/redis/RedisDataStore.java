package consistency.datastore.redis;

import java.util.List;

import consistency.datastore.DataStore;
import redis.clients.jedis.Jedis;
import redis.clients.jedis.JedisPool;

public class RedisDataStore implements DataStore {
	
	//private final Jedis redisConnection;
	private JedisPool pool;
	public RedisDataStore(JedisPool pool) {
		this.pool = pool;
		Runtime.getRuntime().addShutdownHook(new Thread() {
		    public void run() { pool.destroy(); }
		});
	}
	
	@Override
	public List<String> read(String key) {
		final Jedis redisConnection = pool.getResource();
		List<String> lrange = redisConnection.lrange(key, 0,-1);
		redisConnection.close();
		return lrange;
	}

	@Override
	public void write(String key, List<String> values) {
		final Jedis redisConnection = pool.getResource();
		 long deleted = redisConnection.del(key);
		 //System.out.println(deleted+":"+key);
		 redisConnection.rpush(key,values.toArray(new String[values.size()]));
		 redisConnection.close();
	}

	
	
	
}
