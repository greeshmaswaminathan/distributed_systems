package consistency.datastore.redis.connection;

import java.util.Collection;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import redis.clients.jedis.Jedis;
import redis.clients.jedis.JedisPool;
import redis.clients.jedis.JedisPoolConfig;

public class RedisConnectionPool {
	
	private static final Map<String, JedisPool> poolMap = new HashMap<>();
	
	public static void init(List<ServerDetails> servers){
		for (ServerDetails server : servers) {
			poolMap.put(server.getName(),new JedisPool(new JedisPoolConfig(),server.getIp(),server.getPort()));
			
		}
	}
	
	public static Jedis getConnection(String serverName){
		return poolMap.get(serverName).getResource();
	}
	
	@Override
	protected void finalize() throws Throwable {
		super.finalize();
		Collection<JedisPool> jedisPools = poolMap.values();
		for (JedisPool jedisPool : jedisPools) {
			jedisPool.destroy();
		}
		
	}
	
	static final class ServerDetails{
		
		private final String ip;
		private final int port;
		private final String name;
		
		public ServerDetails(String name,String ip, int port){
			this.ip = ip;
			this.port = port;
			this.name = name;
		}

		public String getIp() {
			return ip;
		}

		public int getPort() {
			return port;
		}
		
		public String getName(){
			return name;
		}
	}	

}
 