import java.util.HashMap;
import java.util.Map;

import redis.clients.jedis.Jedis;
import redis.clients.jedis.JedisPool;
import redis.clients.jedis.JedisPoolConfig;
import redis.clients.jedis.Transaction;

public class RedisTest {
	
	private static JedisPool pool = new JedisPool(new JedisPoolConfig(), "192.168.99.100",6379);
	
	
	public void connectToRedis(){
		/// Jedis implements Closable. Hence, the jedis instance will be auto-closed after the last statement.
		try (Jedis jedis = pool.getResource()) {
		  /// ... do stuff here ... for example
		  /*jedis.set("Test1", "Value1_Updated");
		  String value = jedis.get("Test1");
		  System.out.println(value);*/
			
		  Map<String, String> values = new HashMap<String,String>();
		  values.put("server1","value1");
		 // jedis.hmset("Trial", values);
		  jedis.hset("Trial", "server1", "value2");
		  jedis.hset("Trial", "server2", "value22");
		  Map<String, String> hgetAll = jedis.hgetAll("Trial");
		  jedis.hget("Trial", "server1");
		  jedis.watch ("Trial");
		  Transaction t = jedis.multi();
		  t.set("foo", "bar");
		  
		  t.exec();
		  
		  System.out.println(hgetAll);
		}
		/// ... when closing your application:
		pool.destroy();
	}
	
	
	public static void main(String[] args) {
		new RedisTest().connectToRedis();
	}

}
