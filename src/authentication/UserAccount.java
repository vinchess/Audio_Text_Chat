//Author : Vincent Lim
//Email  : vince.lim@outlook.com

package authentication;

import java.rmi.server.UnicastRemoteObject;
import java.util.*;
import database.connectDB;

@SuppressWarnings("serial")
public class UserAccount extends UnicastRemoteObject implements UAInterface{
	connectDB conn = new connectDB();
	public UserAccount() throws Exception{
		super();
	}
	
	public String userReg(String username, String password){
		HashMap<String,String> userList = conn.queryUser();
		
		if(userList.containsKey(username)){
			return "Unable to register, username exist.";
		}else{
			conn.regUser(username,password);
			return "User Registered, proceed to login";
		}
	}
	public String userLogin(String username, String password){
		HashMap<String,String> userList = conn.queryUser();
		
		if(userList.containsKey(username)&&userList.get(username).equals(password)){
			conn.userLogin(username);
			return "Welcome";
		}
		return "Invalid username or password";
	}
	public void userLogout(String username){
		conn.userLogout(username);
	}
}
