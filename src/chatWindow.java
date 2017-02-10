//Author : Vincent Lim
//Email  : vince.lim@outlook.com

import java.util.*;
import java.util.concurrent.*;
import java.awt.*;
import java.awt.event.*;
import java.io.*;
import java.net.*;
import javax.sound.sampled.*;
import javax.swing.*;

import audio.Audio;

public class chatWindow extends Thread{

	private static JFrame mainFrame;
	private JPanel mainPanel;
	private JPanel chatPanel;
	private JPanel msgPanel;
	private JPanel buttonPanel;
	private JTextField msgField;
	private JButton send;
	static JTextArea chatArea;
	
	private JScrollPane chatScroll;
	
	static DatagramSocket dSocket;
	static DatagramPacket sendPacket;
	
	static volatile InetAddress adminAdd;
	static volatile int adminPort;
	private int datagramPort;
	static Socket socket;
	
	public chatWindow(){
		
	}
	public chatWindow(int datagramPort,Socket socket){
		this.datagramPort = datagramPort;
		chatWindow.socket = socket;
	}
	public void run(){
		init();
	}
	private void init(){
		
		
		chatArea = new JTextArea();
		chatScroll = new JScrollPane(chatArea,JScrollPane.VERTICAL_SCROLLBAR_ALWAYS,JScrollPane.HORIZONTAL_SCROLLBAR_NEVER);
		chatScroll.setLayout(new ScrollPaneLayout());
		chatScroll.setWheelScrollingEnabled(true);
		msgField =  new JTextField();
		send = new JButton("Send");
		JButton sendAudio =  new JButton("Audio");
		
		mainFrame = new JFrame("Chat Window");
		mainFrame.setSize(300,500);
		mainFrame.setResizable(false);
		
		Dimension dim = Toolkit.getDefaultToolkit().getScreenSize();
		mainFrame.setLocation(dim.width/2-mainFrame.getSize().width/2, dim.height/2-mainFrame.getSize().height/2);
		
		//mainFrame.setLayout(new GridLayout(3,1));
		mainFrame.addWindowListener(new WindowAdapter() {
			public void windowClosing(WindowEvent windowEvent){
				sendExit();
				System.exit(0);
			}        
		});
		
		mainPanel = new JPanel(new FlowLayout());
		chatPanel = new JPanel(new GridLayout(1,1));
		msgPanel = new JPanel(new GridLayout(1,1));
		buttonPanel = new JPanel(new GridLayout(1,2));
		
		chatPanel.setPreferredSize(new Dimension(290,400));
		msgPanel.setPreferredSize(new Dimension(290,30));
		buttonPanel.setPreferredSize(new Dimension(290,30));
		
		send.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e){
				sendClicked();
			}
		});
		
		msgField.addKeyListener(new KeyListener(){
			public void keyTyped(KeyEvent e){}
		    public void keyPressed(KeyEvent e){
		    	if(e.getKeyCode()==KeyEvent.VK_ENTER){
					sendClicked();
		    	}
		    }
		    public void keyReleased(KeyEvent e){}
		});
		
		/*sendAudio.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e){
				audioClicked(sendAudio);
			}
		});*/
		
		sendAudio.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e){
				if(Audio.on){
					Audio.on = false;
					sendAudio.setText("Audio");
				}else{
					Audio.on = true;
					sendAudio.setText("Stop");
					
					AudioFormat format = new AudioFormat(44100, 16, 2, true, true);

					DataLine.Info targetInfo = new DataLine.Info(TargetDataLine.class, format);
					
					try {
						TargetDataLine targetLine = (TargetDataLine) AudioSystem.getLine(targetInfo);
						targetLine.open(format);
						targetLine.start();

						Audio audio = new Audio(sendPacket,dSocket,targetLine,chatWindow.adminAdd.getHostAddress(),chatWindow.adminPort);
						
						audio.start();

					}catch (Exception ex) {
						System.out.println("Unexpected error in sending audio.");
					}
				}
			}
		});
		
		chatArea.setEditable(false);
		chatArea.setLineWrap(true);
		
		chatPanel.add(chatScroll);
		msgPanel.add(msgField);
		buttonPanel.add(send);
		buttonPanel.add(sendAudio);
		
		mainPanel.add(chatPanel);
		mainPanel.add(msgPanel);
		mainPanel.add(buttonPanel);
		
		mainFrame.add(mainPanel);
		
		
		mainFrame.setVisible(true);
		
		new receiveChat(chatArea,datagramPort).start();
		new receiveAudio((datagramPort+1)).start();
	}
	public void sendClicked(){
		
		byte[] buff = new byte[1024];
		
		buff = msgField.getText().getBytes();
		chatArea.append("Client : " + msgField.getText() + "\n");
		chatArea.setCaretPosition(chatArea.getDocument().getLength());
		msgField.setText("");
		
		try{
			sendPacket = new DatagramPacket(buff,buff.length,adminAdd,adminPort);
			dSocket.send(sendPacket);
		}catch(IOException ioe){
			chatWindow.chatArea.append("Connection Error... \n");			
		}catch(NullPointerException npe){
			chatWindow.chatArea.append("No Admin Online... \n");
		}
	}
	public void audioClicked(JButton sendAudio){
		
		ButtonModel trackButton = sendAudio.getModel();
		
		AudioFormat format = new AudioFormat(44100, 16, 2, true, true);

		DataLine.Info targetInfo = new DataLine.Info(TargetDataLine.class, format);
		
		try {
			TargetDataLine targetLine = (TargetDataLine) AudioSystem.getLine(targetInfo);
			targetLine.open(format);
			targetLine.start();
			
			byte[] buffer = new byte[targetLine.getBufferSize() / 5];

			//long start = System.currentTimeMillis();
			//long end = start + 10*1000; 
			
			//while (System.currentTimeMillis() < end) {
			while(trackButton.isPressed()){
				targetLine.read(buffer, 0, buffer.length);
				sendPacket = new DatagramPacket(buffer,buffer.length,chatWindow.adminAdd,chatWindow.adminPort+1);
				dSocket.send(sendPacket);
			}
		}catch(IOException ioe){
			System.out.println("IO Error. Audio failed to send.");
		}catch (Exception e) {
			System.out.println("Unexpected error in sending audio.");
		}
	}
	public void sendExit(){
		byte[] buff = new byte[1024];
		buff = "EXIT_SYSTEM".getBytes();
		try{
			sendPacket = new DatagramPacket(buff,buff.length,adminAdd,adminPort);
			dSocket.send(sendPacket);
		}catch(IOException ioe){
			
		}
	}
	static void addQueue(){
		try{
			OutputStream os = socket.getOutputStream();
			DataOutputStream dos = new DataOutputStream(os);
			dos.writeUTF("checkin");
			dos.close();
		}catch(IOException ioe){
			
		}
	}

}
class receiveChat extends Thread{
	JTextArea chatArea;
	int datagramPort;
	DatagramSocket chatSocket;
	DatagramPacket chatPacket;
	
	public receiveChat(){}
	public receiveChat(JTextArea chatArea,int datagramPort){
		this.chatArea = chatArea;
		this.datagramPort = datagramPort;
	}
	public void run(){
		System.out.println("Chat receiver on at port: " + datagramPort);
		byte[] buff = new byte[1024];
		try{
			chatSocket = new DatagramSocket(datagramPort);
			chatWindow.dSocket = chatSocket;
			chatPacket = new DatagramPacket(buff,buff.length);
			chatWindow.addQueue();
			chatSocket.receive(chatPacket);
			
			chatWindow.adminAdd = chatPacket.getAddress();
			chatWindow.adminPort = chatPacket.getPort();
			
		}catch(IOException ioe){
			System.out.println("Chat datagram client failed.");
		}catch(Exception e){
			System.out.println("Chat management unexpected error.");
		}
		
		while(true){
			getMsg();
		}
	}
	public void getMsg(){
		try{
			chatSocket.receive(chatPacket);
			String newStr = new String(chatPacket.getData(),0, chatPacket.getLength());
			if(newStr.equals("EXIT_SYSTEM")){
				this.chatArea.append("Admin is no longer online, please try again later.\n");
				this.chatArea.setCaretPosition(this.chatArea.getDocument().getLength());
			}else{
				this.chatArea.append("Admin : " + newStr + "\n");
				this.chatArea.setCaretPosition(this.chatArea.getDocument().getLength());
			}
		}catch(IOException ioe){
			System.out.println("IO Error at chat receive.");
		}catch(Exception e){
			System.out.println("Error at chat receive.");
		}
	}
}
class receiveAudio extends Thread{
	int datagramPort;
	DatagramSocket audioSocket;
	DatagramPacket audioPacket;
	static ConcurrentLinkedQueue<byte[]> queue = new ConcurrentLinkedQueue<byte[]>();
	
	public receiveAudio(){}
	public receiveAudio(int datagramPort){
		this.datagramPort = datagramPort;
	}
	public void run(){
		System.out.println("Audio receiver on at port: " + datagramPort);
		byte[] buff = new byte[17640];
		try{
			audioSocket = new DatagramSocket(datagramPort);
			audioPacket = new DatagramPacket(buff,buff.length);
			
		}catch(IOException ioe){
			System.out.println("Audio datagram client failed.");
		}catch(Exception e){
			System.out.println("Audio management unexpected error.");
		}
		while(true){
			getAudio();
		}
	}
	public void getAudio(){
		try{
			AudioFormat format = new AudioFormat(44100, 16, 2, true, true);

			DataLine.Info sourceInfo = new DataLine.Info(SourceDataLine.class, format);
			
			SourceDataLine sourceLine = (SourceDataLine) AudioSystem.getLine(sourceInfo);
			sourceLine.open(format);
			sourceLine.start();
			
			
			while (true) {
				audioSocket.receive(audioPacket);
				byte[] buff = Arrays.copyOfRange(audioPacket.getData(), 0, audioPacket.getLength());
				System.out.println(audioPacket.getLength());
				sourceLine.write(buff, 0, buff.length);
			}	
		}catch(IOException ioe){
			System.out.println("IO Error at audio receive.");
		}catch(Exception ex){
			System.out.println("Error at audio receive.");
		}
	}
}
