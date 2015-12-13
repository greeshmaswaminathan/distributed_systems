package consistency.type;

import java.util.Map;

import org.junit.Before;
import org.junit.Test;

import consistency.datastore.DataStore;

public class ReadMyWritesTest {

	private DataStore dataStore;
	
	@Before
	public void init(){
		dataStore = new ReadMyWritesStore();
	}
	
	@Test
	public void test() throws InterruptedException {
		dataStore.write("ReadMyWrites", "Client1-Value1");
		Map<String,String> values1 = dataStore.readAllValues("ReadMyWrites","Client1");
		Map<String,String> values2 = dataStore.readAllValues("ReadMyWrites","Client1");
		//Assert.assertTrue(values1.values().equals(values2.values()));
		System.out.println(values1);
		System.out.println(values2);
	}

	@Test
	public void testIfServersAreConsistent(){
		for(int index=1; index <= 100 ; index++){
			Map<String,String> value1 = dataStore.readAllValues("Key"+index);
			Map<String,String> value2 = dataStore.readAllValues("Key"+index);
			if(!value1.equals(value2)){
				System.out.println("Diff values for Key"+index+": "+value1 + value2);
			}
		}
		
	}
}
