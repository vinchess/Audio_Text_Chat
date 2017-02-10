//Author : Vincent Lim
//Email  : vince.lim@outlook.com

import java.io.*;
import java.net.*;

public class ConnectionHandler<E> extends Thread{

	Socket client;
	String clientAddress;
	Logger logger;
	E e;
	
	public ConnectionHandler(Socket socket,Logger logger,E e){
		client = socket;
		clientAddress = client.getInetAddress().toString();
		this.logger = logger;
		this.e = e;
	}
	
	public void run(){

		if(e instanceof clientServer){
			logger.clientIP.append("Client : " + clientAddress + "\n");
			logger.clientIP.setCaretPosition(logger.clientIP.getDocument().getLength());
			
			try{
				InputStream is = client.getInputStream();
				DataInputStream dis = new DataInputStream(is);
				
				if(dis.readUTF().equals("checkin")){
					Server.queue.offer(client);
				}
				dis.close();
			}catch(IOException ioe){
				System.out.println("IO Error in pushClient");
			}catch(Exception e){
				System.out.println("Error in pushClient");
			}
		}
		
		if(e instanceof adminServer){
			logger.adminIP.append("Admin : " + clientAddress + "\n");
			logger.adminIP.setCaretPosition(logger.adminIP.getDocument().getLength());
			new pushClient(client).start();
		}
	}
}
class pushClient extends Thread{
	
	Socket socket;
	
	public pushClient(){}
	public pushClient(Socket socket){
		this.socket = socket;
	}
	public void run(){
		while(true){
			try{
				InputStream is = socket.getInputStream();
				OutputStream os = socket.getOutputStream();
				DataInputStream dis = new DataInputStream(is);
				DataOutputStream dos = new DataOutputStream(os);
				Socket newSocket;
				
				if(dis.readUTF().equals("giveMe")){
					if(!Server.queue.isEmpty()){
						System.out.println("send to admin");

						newSocket = Server.queue.poll();
						dos.writeUTF(newSocket.getInetAddress() + ";" + String.valueOf(newSocket.getPort()));
					}else{
						dos.writeUTF("empty");
					}
				}
			}catch(IOException ioe){
				System.out.println("IO Error in pushClient");
				break;
			}catch(Exception e){
				System.out.println("Error in pushClient");
				break;
			}
		}
	}
}
