package consistency.type;

import java.util.Map;

import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;

import consistency.datastore.DataStore;

public class EventuallyConsistentTest {

	private DataStore dataStore;
	
	@Before
	public void init(){
		dataStore = new EventuallyConsistentStore();
	}
	
	@Test
	public void test() throws InterruptedException {
		dataStore.write("Eventual", "First Value");
		dataStore.write("Eventual", "Second Value");
		dataStore.write("Eventual", "Third Value");
		dataStore.write("Eventual", "Fourth Value");
		Thread.sleep(100);
		Map<String,String> values = dataStore.readAllValues("Eventual");
		//System.out.println(values+":"+values.size());
		org.junit.Assert.assertTrue(values.size()==2);
		//Set<Entry<String, String>> entrySet = values.entrySet();
		Assert.assertTrue(values.get("server0").startsWith("Third Value"));
		Assert.assertTrue(values.get("server1").startsWith("Fourth Value"));
	}

	@Test
	public void testIfServersAreConsistent(){
		for(int index=1; index <= 10000 ; index++){
			Map<String,String> value1 = dataStore.readAllValues("Key"+index);
			Map<String,String> value2 = dataStore.readAllValues("Key"+index);
			if(!value1.equals(value2)){
				System.out.println("Diff values for Key"+index+": "+value1 + value2);
			}
		}
		
	}
}
