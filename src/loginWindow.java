//Author : Vincent Lim
//Email  : vince.lim@outlook.com

import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.KeyEvent;
import java.awt.event.KeyListener;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;
import java.io.IOException;
import java.net.Socket;

import javax.swing.*;

import authentication.UAInterface;

public class loginWindow{
		
	final int portNo = 9191;
	private String ipNo;
	private int datagramPort;
	private static JFrame mainFrame;
	private JPanel mainPanel;
	private JPanel userPanel;
	private JPanel passPanel;
	private JPanel submitPanel;
	private UAInterface user;
	private Socket socket;
	
	public loginWindow(){}
	public loginWindow(UAInterface user,String ipNo){
		this.user = user;
		this.ipNo = ipNo;
	}

	public void init(){
				
		JTextField username = new JTextField();
		JPasswordField password = new JPasswordField();
		JLabel userLabel = new JLabel("Username : ");
		JLabel passLabel = new JLabel("Password : ");
		JButton submit = new JButton("Login");
		JButton register = new JButton("Register");
		
		userLabel.setHorizontalAlignment(JLabel.RIGHT);
		passLabel.setHorizontalAlignment(JLabel.RIGHT);
		
		mainFrame = new JFrame("Sign In/Sign Up");
		mainFrame.setSize(300,160);
		mainFrame.setResizable(false);
		
		Dimension dim = Toolkit.getDefaultToolkit().getScreenSize();
		mainFrame.setLocation(dim.width/2-mainFrame.getSize().width/2, dim.height/2-mainFrame.getSize().height/2);
		
		//mainFrame.setLayout(new GridLayout(3,1));
		mainFrame.addWindowListener(new WindowAdapter() {
			public void windowClosing(WindowEvent windowEvent){
				System.exit(0);
			}        
		});
		
		password.addKeyListener(new KeyListener(){
			public void keyTyped(KeyEvent e){}
		    public void keyPressed(KeyEvent e){
		    	if(e.getKeyCode()==KeyEvent.VK_ENTER){
		    		try{
		        		 String msg = login(username.getText(),String.valueOf(password.getPassword()));
		        		 if(msg.equals("Welcome")){
		        			 mainFrame.setVisible(false);
		        			 if(connect()){
		        				 new chatWindow(datagramPort,socket).start();
		        			 }else{
			        			 msgWindow("Server error...",mainFrame);
		        			 }
		        		 }else{
		        			 mainFrame.setVisible(false);
		        			 msgWindow(msg,mainFrame);
		        		 }
		        	 }catch(IOException ioe){
		        		 System.out.println("Error Establishing Connetion");
		        	 }catch(Exception ex){
		        		 System.out.println("Error Establishing Connetion");
		        	 }
		    	}
		    }
		    public void keyReleased(KeyEvent e){}
		});
	
		mainPanel = new JPanel();
		mainPanel.setLayout(new GridLayout(5,1));
		mainPanel.setPreferredSize(new Dimension(300,100));
		userPanel = new JPanel();
		userPanel.setLayout(new GridLayout(1,2));
		passPanel = new JPanel();
		passPanel.setLayout(new GridLayout(1,2));
		submitPanel = new JPanel();
		submitPanel.setLayout(new GridLayout(1,2));
		
		mainFrame.add(mainPanel);
		
		JLabel title = new JLabel("Client");
		title.setHorizontalAlignment(JLabel.CENTER);
		
		mainPanel.add(new JPanel().add(title));
		//mainPanel.add(new JLabel(pic));
		mainPanel.add(userPanel);
		mainPanel.add(passPanel);
		mainPanel.add(submitPanel);
		
		userPanel.add(userLabel);
		userPanel.add(username);
		passPanel.add(passLabel);
		passPanel.add(password);
		submitPanel.add(submit);
		submitPanel.add(register);
		
		submit.addActionListener(new ActionListener() {
	    	  //perform after button is clicked
	         public void actionPerformed(ActionEvent e) { 
	        	 try{
	        		 String msg = login(username.getText(),String.valueOf(password.getPassword()));
	        		 if(msg.equals("Welcome")){
	        			 mainFrame.setVisible(false);
	        			 if(connect()){
	        				 new chatWindow(datagramPort,socket).start();
	        			 }else{
		        			 msgWindow("Server error...",mainFrame);
	        			 }
	        		 }else{
	        			 mainFrame.setVisible(false);
	        			 msgWindow(msg,mainFrame);
	        		 }
	        	 }catch(IOException ioe){
	        		 System.out.println("Error Establishing Connetion");
	        	 }catch(Exception ex){
	        		 System.out.println("Error Establishing Connetion");
	        	 }
	         }
	      });
		
		register.addActionListener(new ActionListener() {
	    	  //perform after button is clicked
	         public void actionPerformed(ActionEvent e) { 
	        	
	        	 try{
	        		 String msg = register(username.getText(),String.valueOf(password.getPassword()));
	        		 if(msg.equals("User Registered, proceed to login")){ 
	        			 mainFrame.setVisible(false);
	        			 msgWindow(msg,mainFrame);
	        		 }else{
	        			 mainFrame.setVisible(false);
	        			 msgWindow(msg,mainFrame);
	        		 }
	        	 }catch(IOException ioe){
	        		 System.out.println("Error Establishing Connetion");
	        	 }catch(Exception ex){
	        		 System.out.println("Error Establishing Connetion");
	        	 }
	         }
	      });
		
		mainFrame.setVisible(true);
		
	}
	
	public String login(String username,String password) throws Exception{
		return user.userLogin(username, password);
	}
	public String register(String username,String password) throws Exception{
		return user.userReg(username, password);
	}
	public void msgWindow(String msg, JFrame mainFrame){
		JFrame msgFrame = new JFrame();
		JPanel msgPanel = new JPanel(new GridLayout(2,1));
		JLabel msgLabel = new JLabel(msg);
		JButton ok = new JButton("OK");
		
		msgFrame.setSize(300, 150);
		msgFrame.setResizable(false);
		
		Dimension dim = Toolkit.getDefaultToolkit().getScreenSize();
		msgFrame.setLocation(dim.width/2-msgFrame.getSize().width/2, dim.height/2-msgFrame.getSize().height/2);
		
		msgLabel.setHorizontalAlignment(JLabel.CENTER);
		
		msgFrame.add(msgPanel);
		msgPanel.add(msgLabel);
		msgPanel.add(ok);
		ok.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) { 
	        	msgFrame.setVisible(false);
	        	mainFrame.setVisible(true);
	         }
	      });
		
		msgFrame.setVisible(true);
	}
	public boolean connect(){
		try{
			socket = new Socket(ipNo,portNo);
			datagramPort = socket.getLocalPort();
			return true;
		}catch(IOException ioe){
			System.out.println("Server error...");
			return false;
		}
	}		
}
