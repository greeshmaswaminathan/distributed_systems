package consistency.type;

import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;

import consistency.datastore.DataStore;

public class BoundedStalenessTest {

	private DataStore dataStore;
	
	@Before
	public void init(){
		dataStore = new BoundedStaleness();
	}
	
	@Test
	public void test() throws InterruptedException {
		dataStore.write("Bounded", "BoundedValue");
		String value1 = dataStore.read("Bounded");
		String value2 = dataStore.read("Bounded");
		System.out.println(value1+" "+value2);
		Assert.assertTrue(value1 == null || value2 == null);
		Thread.sleep(3000);
		String value = dataStore.read("Bounded");
		Assert.assertTrue(value.equals("BoundedValue"));
	}

	
}
