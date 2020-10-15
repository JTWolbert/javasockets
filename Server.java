package cnt5505;

import java.io.BufferedReader;
import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.HashMap;
import java.util.Map;
import java.util.stream.Collectors;

public class Server {
    
	public static void main(String args[])
	{
		int port = 2750;
	    Map<String, String> validCommands = new HashMap<String, String>();
	    validCommands.put("1","date");
        validCommands.put("2", "uptime");
        validCommands.put("3", "free");
        validCommands.put("4", "netstat");
        validCommands.put("5", "who");
        validCommands.put("6", "ps -e");
        validCommands.put("7", "quit");
        
        try
        {
	        ServerSocket s = new ServerSocket(port);
	        s.setSoTimeout(7000);
	        
	        
		    while(true)
		    {
				System.out.println("Waiting for client on port " + s.getLocalPort() + "...");
				
			    Socket c = s.accept();
			            
			    System.out.println("Just connected to " + c.getRemoteSocketAddress());
			    
			    DataInputStream in = new DataInputStream(c.getInputStream());
			    
			    DataOutputStream out = new DataOutputStream(c.getOutputStream());
			    
			    while(true)
			    {
				    String com = in.readUTF();
				    
				    if(com.equals("7"))
				    {
				    	System.out.println("Closing socket...");
				    	c.close();
				    	break;
				    }
				    else
				    {
				    	if(validCommands.containsKey(com))
				    	{
					    	out.writeUTF(shellCom(validCommands.get(com)));
				    	}
				    }
			    
			    }
			    
		    }
		    
        }
        catch(Exception e)
        {
        	System.out.println(e);
        }
	}
	
	static String shellCom(String com)
	{
		if (com == null) {
            return null;
        }

        System.out.println("Executing command " + com);
        ProcessBuilder pb = new ProcessBuilder(com.split(" ")).redirectErrorStream(true);
        try
        {
        	Process p = pb.start();
        	BufferedReader in = new BufferedReader(new InputStreamReader(p.getInputStream()));
            return in.lines().collect(Collectors.joining(System.lineSeparator()));
        }
        catch (IOException e)
        {
            e.printStackTrace();
        }

        return null;
	}

}
