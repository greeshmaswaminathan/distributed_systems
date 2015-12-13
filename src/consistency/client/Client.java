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
			//writeDataToStore(proxy,consistencyType);
			//readFromDataStore(proxy,consistencyType);
			readIntensive(proxy,consistencyType);
			//writeIntensive(proxy, consistencyType);
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}
	
	private void readIntensive(Proxy proxy, Consistency consistencyType) throws IOException {
		try {
			openLog("clientlog_readwrite_");
			
			BufferedReader reader = new BufferedReader(new FileReader("readWritedata"+id));
			String data = "";
			//int temp = 0;
			//long counter = 0;
			for(int i = 1; i <= 100; i++){
				data = reader.readLine();
				if(i%4 == 1){
					//temp = i;
					String[] result = data.split(",");
					long before = System.nanoTime();
					proxy.write(result[0], result[1]);
					writeToLog((System.nanoTime() - before)+System.lineSeparator());
				}else{
					long before = System.nanoTime();
					read(proxy, data);
					writeToLog((System.nanoTime() - before)+System.lineSeparator());
				}
			}
			
			reader.close();
			closeLog();
			 
		} catch (FileNotFoundException e) {
			e.printStackTrace();
		}
	}
	
	private void writeIntensive(Proxy proxy, Consistency consistencyType) throws IOException {
		try {
			openLog("clientlog_writeintense_");
			
			BufferedReader reader = new BufferedReader(new FileReader("writeIntense"+id));
			String data = "";
			//int temp = 0;
			//long counter = 0;
			for(int i = 1; i <= 100; i++){
				data = reader.readLine();
				if(i%4 == 0){
					long before = System.nanoTime();
					read(proxy, data);
					writeToLog((System.nanoTime() - before)+System.lineSeparator());
				}else{
					String[] result = data.split(",");
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
	
	private void read(Proxy proxy, String key){
		if(consistencyType == Consistency.EVENTUAL){
			 proxy.readAllValues(key);
		}
		else if(consistencyType == Consistency.BOUNDED_STALENESS){
			proxy.read(key, 10);
		}
		else if(consistencyType == Consistency.READ_MY_WRITES){
			proxy.readAllValues(key, "Client"+this.id);
		}
		else if(consistencyType == Consistency.MONOTONIC_READS){
			proxy.readAllValues(key, "Client"+this.id);
		}
		else{
			proxy.read(key);
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
