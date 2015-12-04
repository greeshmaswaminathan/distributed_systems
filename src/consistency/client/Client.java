package consistency.client;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;

import consistency.proxy.Proxy;
import consistency.proxy.Proxy.Consistency;

public class Client implements Runnable{

	private int id;
	private FileWriter logWriter;
	private Consistency consistencyType;
	
	
	public Client(int id, Consistency consistencyType) {
		this.id = id;
		this.consistencyType = consistencyType;
	}
	
	@Override
	public void run() {
		
		try {
			Proxy proxy = Proxy.getProxy(consistencyType);
			writeDataToStore(proxy,consistencyType);
			readFromDataStore(proxy,consistencyType);
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}
	
	private void writeDataToStore(Proxy proxy, Proxy.Consistency consistency) throws IOException{
		try {
			openLog("clientlog_write_");
			
			BufferedReader reader = new BufferedReader(new FileReader("writedata"+id));
			String value = "";
			//long counter = 0;
			while((value = reader.readLine()) != null){
				if(!"".equals(value)){
					String[] result = value.split(",");
					long before = System.nanoTime();
					proxy.write(result[0], result[1]);
					writeToLog((System.nanoTime() - before)+System.lineSeparator());
				}
			}
			reader.close();
			closeLog();
			 
		} catch (FileNotFoundException e) {
			e.printStackTrace();
		}
	}
	
	private void readFromDataStore(Proxy proxy, Proxy.Consistency consistency) throws IOException{
		try {
			openLog("clientlog_read_");
			BufferedReader reader = new BufferedReader(new FileReader("readdata"+id));
			String key = "";
			//long counter = 0;
			while((key = reader.readLine()) != null){
				if(!"".equals(key)){
					long before = System.nanoTime();
					String value = proxy.read(key);
					writeToLog((System.nanoTime() - before)+System.lineSeparator());
					
				}
			}
			reader.close();
			closeLog();
			 
		} catch (FileNotFoundException e) {
			e.printStackTrace();
		}
	}
	
	
	private void openLog(String logName) throws IOException{
		logWriter = new FileWriter(new File(logName+consistencyType.name()+"_"+id));
	}
	private void writeToLog(String value) throws IOException{
		logWriter.write(value);
	}
	
	private void closeLog() throws IOException{
		logWriter.close();
	}

}
