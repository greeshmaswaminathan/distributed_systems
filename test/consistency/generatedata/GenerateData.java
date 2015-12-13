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
	
	public void generateReadWriteData() throws IOException{
		int temp = 0;
		for(int j = 1; j <=5; j++){
			FileWriter writer = new FileWriter(new File("readWritedata"+j));
			for(int i = 1; i <= 100; i++){
				if(i%4 == 1){
					temp = i;
					writer.write("Key"+i+",Client"+j+"-Value"+i+System.lineSeparator());
				}else{
					writer.write("Key"+temp+System.lineSeparator());
				}
			}
			writer.close();
		}
	}
	
	
	public void generateWriteIntensiveData() throws IOException{
		for(int j = 1; j <=5; j++){
			FileWriter writer = new FileWriter(new File("writeIntense"+j));
			for(int i = 1; i <= 100; i++){
				if(i%4 == 0){
					writer.write("Key"+(i-1)+System.lineSeparator());
				}else{
					writer.write("Key"+i+",Client"+j+"-Value"+i+System.lineSeparator());
					
				}
			}
			writer.close();
		}
	}
	public static void main(String[] args) throws IOException {
		new GenerateData().generateWriteIntensiveData();
		//new GenerateData().generateWriteData();
		//new GenerateData().generateReadWriteData();
		//new GenerateData().generateWriteTestData();
	}

}
