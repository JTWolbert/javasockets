package cnt5505;

import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.io.PrintStream;
import java.net.Socket;
import java.util.ArrayList;
import java.util.Scanner;

public class Client extends Thread{
	
	String ip;
    int port;
    int testMode;
    long timeElapsed;
    
    
    Client(String ip,int port,int testMode)
    {
    	this.ip = ip;
        this.port = port;
        this.testMode = testMode;
    }
	
	public void run()
	{
		if(this.ip != null)
		      try {
		    	  
		    	 long start = System.currentTimeMillis();
		         Socket client = new Socket(this.ip, this.port);
		         Scanner scn = new Scanner(System.in);
		         
		         DataOutputStream out = new DataOutputStream(client.getOutputStream());
		         
		         DataInputStream in = new DataInputStream(client.getInputStream());
		         
		         if(this.testMode == 0)
		         {
		        	 System.out.println("Just connected to " + client.getRemoteSocketAddress());
		        	 while(true)
		        	 {
		        		 DisplayMenu();
		        		 String com = scn.next();
		        		 if(isValid(com))
		        		 {
		        			 out.writeUTF(com);
		        			 if(com.equals("7"))
		        			 {
		        				 break;
		        			 }
		        			 System.out.println(in.readUTF());
		        		 }
		        		 else
		        		 {
		        			 System.out.println("Command invalid... enter valid command");
		        		 }
		        	 }
		         }
		         else
		         {
		        	 out.writeUTF(Integer.toString(testMode));
		        	 String answer = in.readUTF();
		        	 this.timeElapsed =  System.currentTimeMillis() - start;
		        	 out.writeUTF("7");
		         }
		         client.close();
		      } catch (IOException e) {
		         e.printStackTrace();
		      }
	}
	
	void DisplayMenu()
	{
		//TODO: implement
		StringBuilder sb = new StringBuilder("Please choose a command:\n");
        sb.append("1 - Host current Date and Time\n");
        sb.append("2 - Host uptime\n");
        sb.append("3 - Host memory use\n");
        sb.append("4 - Host Netstat\n");
        sb.append("5 - Host current users\n");
        sb.append("6 - Host running processes\n");
        sb.append("7 - Quit\n");
        
        System.out.println(sb.toString());
	}
	
	boolean isValid(String com)
	{
		try
		{
			int input = Integer.parseInt(com);
        	if (input > 0 && input <= 7)
        	{
        		return true;
        	}
        	else
        	return false;
    	}
		catch (NumberFormatException e)
		{
        	return false;
    	}
	}
	
	public static void main(String args[])
	{
		String ip = "192.168.101.100";
	    int port = 2750;
	    int threadNum = 1;
	    int testMode = 0;
	    
	    if(args.length > 0)
		{
			ip = args[0];
		}
	    else
	    {
	    	System.out.println("Please enter IP address as command line arg");
	    	return;
	    }
	    if(args.length > 1)
		{
	        threadNum = Integer.parseInt(args[1]);
			testMode = Integer.parseInt(args[2]);
		}
	    
	    System.out.println("Starting " + threadNum + " clients in test-mode " + testMode);
		
		ArrayList<Client> clientList = new ArrayList<Client>();

		for(int i = 0;i < threadNum;i++)
		{
			clientList.add(new Client(ip,port,testMode));
		}
		
		for(Client x : clientList)
		{
			x.start();
		}
		
		for(Client x : clientList)
		{
			try
			{
				x.join();
			}
			catch (InterruptedException e)
			{
				e.printStackTrace();
			}
		}
		
		long meanTimeElapsed = 0;
		
		for(Client x : clientList)
		{
			meanTimeElapsed += x.timeElapsed;
		}
		
		double result = (double)meanTimeElapsed / threadNum;
		
		if(testMode != 0)
		{
			System.out.println("Average time for " + threadNum + " threads: " + result + " msec");
		}
		
	}

}
