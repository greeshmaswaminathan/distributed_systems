package consistency.generatedata;

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;

public class GenerateData {
	
	public void generateWriteData() throws IOException{
		for(int j = 1; j <=5; j++){
			FileWriter writer = new FileWriter(new File("writedata"+j));
			for(int i = 1; i <= 100; i++){
				writer.write("Key"+i+",Client"+j+"-Value"+i+System.lineSeparator());
			}
			writer.close();
		}
		
	}
	
	public void generateWriteTestData() throws IOException{
		for(int j = 1; j <=5; j++){
			FileWriter writer = new FileWriter(new File("writedata_test"+j));
			for(int i = 1; i <= 100; i++){
				writer.write("Key"+1+",Client"+j+"-Value"+i+System.lineSeparator());
			}
			writer.close();
		}
		
	}
	public void generateReadData() throws IOException{
		for(int j = 1; j <=5; j++){
			FileWriter writer = new FileWriter(new File("readdata"+j));
			for(int i = 1; i <= 100; i++){
				writer.write("Key"+i+System.lineSeparator());
			}
			writer.close();
		}
	}
	
	public static void main(String[] args) throws IOException {
		new GenerateData().generateReadData();
		new GenerateData().generateWriteData();
		//new GenerateData().generateWriteTestData();
	}

}
