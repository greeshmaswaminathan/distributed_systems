import redis.clients.jedis.Jedis;
import redis.clients.jedis.JedisPool;
import redis.clients.jedis.JedisPoolConfig;

public class RedisTest {
	
	private static JedisPool pool = new JedisPool(new JedisPoolConfig(), "192.168.99.100",6379);
	
	
	public void connectToRedis(){
		/// Jedis implements Closable. Hence, the jedis instance will be auto-closed after the last statement.
		try (Jedis jedis = pool.getResource()) {
		  /// ... do stuff here ... for example
		  jedis.set("Test1", "Value1_Updated");
		  String value = jedis.get("Test1");
		  System.out.println(value);
		  
		}
		/// ... when closing your application:
		pool.destroy();
	}
	
	
	public static void main(String[] args) {
		new RedisTest().connectToRedis();
	}

}
