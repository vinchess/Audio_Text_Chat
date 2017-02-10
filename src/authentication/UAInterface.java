//Author : Vincent Lim
//Email  : vince.lim@outlook.com

package authentication;

import java.rmi.Remote;
import java.rmi.RemoteException;

public interface UAInterface extends Remote{
	
	public String userReg(String username, String password) throws RemoteException;
	public String userLogin(String username, String password) throws RemoteException;
	public void userLogout(String username) throws RemoteException;
}
