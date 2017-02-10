//Author : Vincent Lim
//Email  : vince.lim@outlook.com

import java.net.*;
import java.rmi.registry.*;
import java.util.concurrent.*;

import authentication.UAInterface;
import authentication.UserAccount;

public class Server {
	
	static volatile ConcurrentLinkedQueue<Socket> queue = new ConcurrentLinkedQueue<Socket>();
	
	public static void main(String args[]) throws Exception{
		
		Logger logger = new Logger();
		initRMI();
		
		new clientServer(logger).start();
		new adminServer(logger).start();
		
		while(true){}
	}
	public static void initRMI() throws Exception{
		Registry registry = LocateRegistry.createRegistry(1099);
		UAInterface user = new UserAccount();
		
		registry.bind("userObj", user);
	}
}

//clientServer class
class clientServer extends Thread{
	ServerSocket connectionListener;
	Socket connection;
	Logger logger;
	
	public clientServer(Logger logger){
		this.logger = logger;
	}
	public void run(){
		System.out.println("Client connection server started...");
		try{
			connectionListener = new ServerSocket(9191);
	
			while(true){
				connection = connectionListener.accept();
				ConnectionHandler<clientServer> handler = new ConnectionHandler<clientServer>(connection,logger,this);
				handler.start();
			}	
			
		}catch(Exception e){
			System.out.println("Client connection error...");
		}
	}
}

//adminServer class
class adminServer extends Thread{
	ServerSocket connectionListener;
	Socket connection;
	Logger logger;
	
	public adminServer(Logger logger){
		this.logger = logger;
	}
	public void run(){
		System.out.println("Admin connection server started...");
		try{
			connectionListener = new ServerSocket(9292);
	
			while(true){
				connection = connectionListener.accept();
				ConnectionHandler<adminServer> handler = new ConnectionHandler<adminServer>(connection,logger,this);
				handler.start();
			}	
			
		}catch(Exception e){
			System.out.println("Admin connection error...");
		}
	}
}
