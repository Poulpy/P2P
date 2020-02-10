package fr.uvsq.fsp.server;

import java.io.IOException;
import java.net.Socket;

public class WaitingServer implements Runnable {
	  Socket sock;
	  Boolean isStopped;
	  FSPCentral server1;
	
	public synchronized void stop() {
		
	isStopped=true;
	server1.disconnect();
	
	}
	
	private synchronized boolean isStopped ()
	{
		return isStopped;
	
	}
	
	@Override
	public void run() {
		// TODO Auto-generated method stub
		while(!isStopped) {
 
			try {
				sock = server1.serverSocket.accept();
			} catch (IOException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
            System.out.println("Connected");
            new Thread(new FSPCentral("127.0.0.1", 60000,sock)).start();
        
		}
		
	}
	
	

}
