package utils;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.util.ArrayList;
import java.util.List;
import java.util.Scanner;

import jodd.io.StreamGobbler;

public class MonitorKillAndStartExec {
	public static void main(String[] args) throws Exception {
		MonitorKillAndStartExec mp = new MonitorKillAndStartExec();
		String execPId = mp.readExecPId(System.getProperty("user.dir")).trim();
				
		if (execPId.length() !=0) {
			//KILL EXECUTOR
			killExec("taskkill /pid " + execPId);

			//START EXECUTOR
			startExecutorJar();
		}else{
//			killExec("taskkill /pid " + execPId);
	startExecutorJar();
			System.out.println("Executor Pid not found, No need to Kill");
		}		
	}

	//START EXECUTER JAR AND EXIT MONITOR i.e. this APPLICATION
	public static void startExecutorJar(){		
		try{
			List<String> command = new ArrayList<String>();
		    
		    command.add("java");
		    command.add("-jar");
		    command.add("C:\\BypassEstableQlty\\Bypass.jar");
		    
		    ProcessBuilder builder = new ProcessBuilder(command);		    
		    Process process = builder.start();		
		    
		    System.exit(0);
		    
		    InputStream is = process.getInputStream();		    
		    // any error message?
		    StreamGobbler errorGobbler = new StreamGobbler(process.getErrorStream(), "ERROR");      
	        // any output?
		    StreamGobbler outputGobbler = new StreamGobbler(process.getInputStream(), "OUTPUT");
	        
	        // kick them off
	        errorGobbler.start();
	        outputGobbler.start();
	        
	        // any error???
	       int exitVal=-1; 
	       try {
	    	    exitVal = process.waitFor();
			} catch (Throwable t) {
				System.out.println("Executer, In catch");
				t.printStackTrace();
			}
    		
		}catch(Exception e){
			System.out.println("Executer threw a SQLException : " + e);
			e.printStackTrace();			
		}finally{
			
		}
		
		System.out.println("Exec FINISHED");
		
	}

	public String readExecPId(String fFilePath) {
		StringBuilder executorPId = new StringBuilder();
		Scanner scanner = null;
		try {
			scanner = new Scanner(new File(fFilePath+"\\"+"execPId.txt"));
			while (scanner.hasNextLine()) {
				executorPId.append(scanner.nextLine());
			}
		}catch(IOException ie){
			System.out.println("MonitorKillAndStartExec.readExecPId() could not find : " + fFilePath+"\\"+"execPId.txt");
		}finally {
			if(scanner!=null)
				scanner.close();
		}
		return executorPId.toString();
	}
	
	private static ArrayList<String> killExec(String processStr) throws IOException {

		String outStr = "";
		ArrayList<String> processOutList = new ArrayList<String>();
		int i = -1;
		
		Process p = Runtime.getRuntime().exec(processStr);
		
		// OutputStream out = p.getOutputStream();
		//OutputStream out = p.getOutputStream();
		InputStream in = p.getInputStream();
		
		x11: while ((i = in.read()) != -1) {
			if ((char) i == '\n') {
				processOutList.add((outStr));
				outStr = "";
				continue x11;
			}
			outStr += (char) i;
		}
		
		return processOutList;
	}

}