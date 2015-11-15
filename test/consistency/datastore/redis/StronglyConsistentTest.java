package consistency.datastore.redis;

import static org.junit.Assert.*;

import java.util.ArrayList;
import java.util.List;

import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;

import consistency.type.StronglyConsistentStore;

public class StronglyConsistentTest {
	
	private StronglyConsistentStore consistentStore;
	
	@Before
	public void init(){
		consistentStore = new StronglyConsistentStore();
	}

	@Test
	public void testRead() {
		List<String> valuesFromRedis = consistentStore.read("Test1");
		Assert.assertTrue(valuesFromRedis.size() == 1);
		Assert.assertTrue(valuesFromRedis.get(0).equals("Junit3"));
		valuesFromRedis = consistentStore.read("Test21");
		Assert.assertTrue(valuesFromRedis.size() == 1);
		Assert.assertTrue(valuesFromRedis.get(0).equals("Value1"));
	}

	@Test
	public void testWrite() {
		fail("Not yet implemented");
	}

	@Test
	public void testConsistentWrite() {
		List<String> values = new ArrayList<String>();
		values.add("Yes");
		values.add("All");
		consistentStore.write("Strong", values);
		List<String> valuesFromRedis = consistentStore.read("Strong");
		Assert.assertTrue(valuesFromRedis.size() == 2);
		Assert.assertTrue(valuesFromRedis.get(0).equals("Yes"));
		Assert.assertTrue(valuesFromRedis.get(1).equals("All"));
		valuesFromRedis = consistentStore.read("Strong");
		Assert.assertTrue(valuesFromRedis.size() == 2);
		Assert.assertTrue(valuesFromRedis.get(0).equals("Yes"));
		Assert.assertTrue(valuesFromRedis.get(1).equals("All"));
	}

	@Test
	public void testGetSecondaryDataStores() {
		fail("Not yet implemented");
	}

}
