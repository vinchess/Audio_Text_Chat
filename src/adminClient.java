//Author : Vincent Lim
//Email  : vince.lim@outlook.com

import java.awt.*;
import java.awt.event.*;
import java.io.*;
import java.rmi.registry.LocateRegistry;
import java.rmi.registry.Registry;

import javax.swing.*;

import authentication.UAInterface;

public class adminClient {

	public static int portNo = 1099;
	public static String ipNo;
	private static Registry registry;
	private static UAInterface user;
	
	public static void main(String[] args){
		
		JFrame frame = new JFrame("Connect");
		JPanel main = new JPanel(new FlowLayout());
		JPanel titlePane = new JPanel(new GridLayout(1,1));
		JLabel title = new JLabel("Enter IP Address");
		JPanel textPane = new JPanel(new GridLayout(1,1));
		JTextField text = new JTextField();
		JPanel ipPane = new JPanel(new GridLayout(1,1));
		JButton getIP = new JButton("Ok");
		
		frame.addWindowListener(new WindowAdapter() {
			public void windowClosing(WindowEvent windowEvent){
				System.exit(0);
			}        
		});
		
		frame.setSize(270, 150);
		Dimension dim = Toolkit.getDefaultToolkit().getScreenSize();
		frame.setLocation(dim.width/2-frame.getSize().width/2, dim.height/2-frame.getSize().height/2);
		
		title.setHorizontalAlignment(JLabel.CENTER);
		titlePane.setPreferredSize(new Dimension(150,30));
		textPane.setPreferredSize(new Dimension(200,30));
		ipPane.setPreferredSize(new Dimension(100,30));
		titlePane.add(title);
		textPane.add(text);
		ipPane.add(getIP);
		main.add(titlePane);
		main.add(textPane);
		main.add(ipPane);
		frame.add(main);
		
		text.addActionListener(new ActionListener(){
			public void actionPerformed(ActionEvent e){
				frame.setVisible(false);
				ipNo = text.getText();
				try{
					getRMI();
					adminLoginWindow window = new adminLoginWindow(user,ipNo);
					window.init();
				}catch(IOException ioe){
					System.out.println("RMI IO Failed...");
					frame.setVisible(true);
				}catch(Exception ex){
					System.out.println("RMI Failed...");
					frame.setVisible(true);
				}
			}
		});
		getIP.addActionListener(new ActionListener(){
			public void actionPerformed(ActionEvent e){
				frame.setVisible(false);
				ipNo = text.getText();
				try{
					getRMI();
					adminLoginWindow window = new adminLoginWindow(user,ipNo);
					window.init();
				}catch(IOException ioe){
					System.out.println("RMI IO Failed...");
					frame.setVisible(true);
				}catch(Exception ex){
					System.out.println("RMI Failed...");
					frame.setVisible(true);
				}
			}
		});
		
		frame.setVisible(true);
		
		while(true){}
	}
	private static void getRMI() throws IOException,Exception{
		registry = LocateRegistry.getRegistry(ipNo,portNo);
		user = (UAInterface)registry.lookup("userObj");
	}
}
