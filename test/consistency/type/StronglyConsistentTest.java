package consistency.type;

import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;

public class StronglyConsistentTest {
	
	private StronglyConsistentStore consistentStore;
	
	@Before
	public void init(){
		consistentStore = new StronglyConsistentStore();
	}

	

	

	@Test
	public void testConsistentWrite() {
		
		consistentStore.write("Strong", "All");
		//primary
		String valueFromRedis = consistentStore.read("Strong");
		Assert.assertTrue(valueFromRedis.equals("All"));
		valueFromRedis = consistentStore.read("Strong");
		//secondary
		Assert.assertTrue(valueFromRedis.equals("All"));
		//overwrite
		consistentStore.write("Strong", "Still Strong");
		//Read from both servers
		valueFromRedis = consistentStore.read("Strong");
		Assert.assertTrue(valueFromRedis.equals("Still Strong"));
		valueFromRedis = consistentStore.read("Strong");
		Assert.assertTrue(valueFromRedis.equals("Still Strong"));
		
	}

	@Test
	public void testIfServersAreConsistent(){
		for(int index=1; index <= 100 ; index++){
			String value1 = consistentStore.read("Key"+index);
			String value2 = consistentStore.read("Key"+index);
			if(value1!=null && value2!=null){
				if(!value1.equals(value2)){
					System.out.println("Diff values for Key"+index+": "+value1 + value2);
				}
			}
		}
		
	}
	

}
