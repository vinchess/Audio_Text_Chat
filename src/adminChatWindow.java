//Author : Vincent Lim
//Email  : vince.lim@outlook.com

import java.awt.*;
import java.awt.event.*;
import java.io.*;
import java.util.*;
import javax.sound.sampled.*;
import javax.swing.*;
import javax.swing.event.*;
import java.net.*;

import audio.Audio;

@SuppressWarnings("unused")
public class adminChatWindow extends Thread{

	HashMap<Integer,String> clientList = new HashMap<Integer,String>();
	HashMap<Integer,JPanel> panelList = new HashMap<Integer,JPanel>();
	static HashMap<Integer,JScrollPane> chatBoxes = new HashMap<Integer,JScrollPane>();
	ArrayList<JRadioButton> checkboxes = new ArrayList<JRadioButton>();
	
	static JFrame mainFrame;
	private JPanel mainPanel;
	private JTabbedPane tabPanel;
	private JScrollPane chatPanel;
	private JLayeredPane chatPane;
	static JPanel checkPanel;
	static ButtonGroup bGroup;
	private JPanel msgPanel;
	private JPanel buttonPanel;
	//private JTextArea chatArea;
	private JTextField msgField;
	
	volatile static DatagramSocket dSocket;
	static DatagramPacket sendPacket;
	static DatagramPacket receivePacket;
	private Socket socket;
	
	static int clients = 0;

	private int cport = 0;
	//private JPanel submitPanel;
	
	public adminChatWindow(Socket socket){
		this.socket = socket;
	}
	
	public void run(){
		init();
		while(clients<10){
			if(clients<10){
				requestClient();
			}
		}
	}
	
	private void init(){
		
		bGroup = new ButtonGroup();
		JRadioButton checkBox = new JRadioButton();// = new JRadioButton("CientIP");
		//chatArea = new JTextArea();
		msgField =  new JTextField();
		JButton send = new JButton("Send");
		JButton sendAudio =  new JButton("Audio");
		JButton sendMany = new JButton("Multi Send");
		
		mainFrame = new JFrame("Chat Window");
		mainFrame.setSize(650,570);
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
		
		checkBox.setVerticalAlignment(JRadioButton.TOP);
		
		mainPanel = new JPanel(new FlowLayout());
		tabPanel = new JTabbedPane();
		chatPane = new JLayeredPane();
		chatPane.setLayout(new GridLayout(1,1));
		checkPanel = new JPanel(new GridLayout(10,1));
		msgPanel = new JPanel(new GridLayout(1,2));
		buttonPanel = new JPanel(new GridLayout(3,1));
		
		tabPanel.setPreferredSize(new Dimension(540,30));
		chatPane.setPreferredSize(new Dimension(420,400));
		checkPanel.setPreferredSize(new Dimension(120,400));
		msgPanel.setPreferredSize(new Dimension(420,90));
		buttonPanel.setPreferredSize(new Dimension(120,90));
		
		//chatPanel.add(chatArea);
		//chatPanel.setLayout(new ScrollPaneLayout());
		//chatPanel.setWheelScrollingEnabled(true);
	      
		tabPanel.setAlignmentX(JTabbedPane.LEFT_ALIGNMENT);
		
		for(int i =0;i<10;i++){
			//Create JCheckBoxes
			JRadioButton box = new JRadioButton();
			box.addActionListener(new ActionListener(){
				public void actionPerformed(ActionEvent e){
					for(Component c : chatPane.getComponents()){
						chatPane.remove(c);
					}
					AbstractButton button = (AbstractButton)e.getSource();
					chatPane.add(chatBoxes.get(Integer.parseInt(button.getText())));
					chatPanel.repaint();
					chatPane.repaint();
					chatPane.revalidate();
					checkPanel.repaint();
				}
			});

			box.setEnabled(false);
			bGroup.add(box);
			checkboxes.add(box);
			checkPanel.add(box);
			
		}
		
		msgPanel.add(msgField);
		msgPanel.add(buttonPanel);
		buttonPanel.add(send);
		buttonPanel.add(sendAudio);
		buttonPanel.add(sendMany);
		
		sendMany.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e){
				multiSend();
			}
		});
		
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
		msgField.setAlignmentY(JTextField.TOP_ALIGNMENT);
		
		sendAudio.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e){
				if(Audio.on){
					Audio.on = false;
					sendAudio.setText("Audio");
				}else{
					Audio.on = true;
					sendAudio.setText("Stop");
					String selectedIP = "";
					int selectedPort = 0;
					
					Component[] comp = checkPanel.getComponents();
					for(Component c:comp){
						if(((JRadioButton)c).isSelected()){
							selectedPort = Integer.parseInt(((JRadioButton)c).getText());
							selectedIP = clientList.get(selectedPort);
						}
					}
					
					AudioFormat format = new AudioFormat(44100, 16, 2, true, true);

					DataLine.Info targetInfo = new DataLine.Info(TargetDataLine.class, format);
					
					try {
						TargetDataLine targetLine = (TargetDataLine) AudioSystem.getLine(targetInfo);
						targetLine.open(format);
						targetLine.start();
						
						Audio audio = new Audio(sendPacket,dSocket,targetLine,selectedIP,selectedPort);
						
						audio.start();

					}catch (Exception ex) {
						System.out.println("Unexpected error in sending audio.");
					}
				}
			}
		});
		
		
		//mainPanel.add(tabPanel);
		mainPanel.add(chatPane);
		mainPanel.add(checkPanel);
		mainPanel.add(msgPanel);
		mainPanel.add(buttonPanel);
		
		mainFrame.add(mainPanel);
		
		
		mainFrame.setVisible(true);
	}
	public void sendClicked(){
		String selectedIP = "";
		int selectedPort = 0;
		
		Component[] comp = checkPanel.getComponents();
		for(Component e:comp){
			if(((JRadioButton)e).isSelected()){
				selectedPort = Integer.parseInt(((JRadioButton)e).getText());
				selectedIP = clientList.get(selectedPort);
				
				Component[] pane = chatPane.getComponents();
				for(Component p:pane){
					if(((JScrollPane)p).isVisible()){
						Component innerComp = ((JScrollPane)p).getComponent(0);
						if(innerComp instanceof JViewport){
							Component ch = ((JViewport) innerComp).getComponent(0);
							if(ch instanceof JTextArea){
								((JTextArea)ch).append("Admin : " + msgField.getText() + "\n");
								((JTextArea)ch).setCaretPosition(((JTextArea)ch).getDocument().getLength());
								
								byte[] buffer = new byte[1024];
								buffer = msgField.getText().getBytes();
								
								try{
									sendPacket = new DatagramPacket(buffer,buffer.length,InetAddress.getByName(selectedIP),selectedPort);
									dSocket.send(sendPacket);
									
								}catch(IOException ioe){
									System.out.println("IO Error. Failed to send chat.");
								}catch(Exception ex){
									System.out.println("Unexpected Error.");
								}
							}
						}
					}
				}
			}
		}
		msgField.setText("");
	}
	public void audioClicked(){
		
		String selectedIP = "";
		int selectedPort = 0;
		
		Component[] comp = checkPanel.getComponents();
		for(Component e:comp){
			if(((JRadioButton)e).isSelected()){
				selectedPort = Integer.parseInt(((JRadioButton)e).getText());
				selectedIP = clientList.get(selectedPort);
			}
		}
		
		AudioFormat format = new AudioFormat(44100, 16, 2, true, true);

		DataLine.Info targetInfo = new DataLine.Info(TargetDataLine.class, format);
		
		try {
			TargetDataLine targetLine = (TargetDataLine) AudioSystem.getLine(targetInfo);
			targetLine.open(format);
			targetLine.start();
			
			byte[] buffer = new byte[targetLine.getBufferSize() / 5];

			long start = System.currentTimeMillis();
			long end = start + 10*1000; 
			
			while (System.currentTimeMillis() < end) {
			//while(trackButton.isPressed()){
				targetLine.read(buffer, 0, buffer.length);
				sendPacket = new DatagramPacket(buffer,buffer.length,InetAddress.getByName(selectedIP),selectedPort+1);
				dSocket.send(sendPacket);

			}
		}catch(IOException ioe){
			System.out.println("IO Error. Audio failed to send.");
		}catch (Exception e) {
			System.out.println("Unexpected error in sending audio.");
		}
	}
	public void multiSend(){
		JFrame multi = new JFrame();
		JButton audio = new JButton("Audio");
		JPanel main = new JPanel(new FlowLayout());
		JPanel auPanel =  new JPanel(new GridLayout(1,1));
		JPanel msg = new JPanel(new GridLayout(1,1));
		JPanel checkbox = new JPanel(new GridLayout(5,2));
		JTextField msgField = new JTextField();
		
		multi.setSize(750, 160);
		auPanel.setPreferredSize(new Dimension(120,120));
		msg.setPreferredSize(new Dimension(400,120));
		checkbox.setPreferredSize(new Dimension(200,120));
		
		Component[] comp = checkPanel.getComponents();
		for(Component e:comp){
			if(!((JRadioButton)e).getText().equals("")){
				checkbox.add(new JCheckBox(((JRadioButton)e).getText()));
			}
		}
		
		audio.addActionListener(new ActionListener(){
			public void actionPerformed(ActionEvent e){
				if(Audio.on){
					Audio.on = false;
					audio.setText("Audio");
				}else{
					Audio.on = true;
					audio.setText("Stop");
					
					Component[] check = checkbox.getComponents();
		    		for(Component c:check){
		    			if(((JCheckBox)c).isSelected()){
		    				AudioFormat format = new AudioFormat(44100, 16, 2, true, true);

							DataLine.Info targetInfo = new DataLine.Info(TargetDataLine.class, format);
							
							try {
								TargetDataLine targetLine = (TargetDataLine) AudioSystem.getLine(targetInfo);
								targetLine.open(format);
								targetLine.start();
								
								Audio audio = new Audio(sendPacket,dSocket,targetLine,
										clientList.get(Integer.parseInt(((JCheckBox)c).getText())),
			    						Integer.parseInt(((JCheckBox)c).getText()));
								
								audio.start();

							}catch (Exception ex) {
								System.out.println("Unexpected error in sending audio.");
							}
		    			}
		    		}
				}
			}
		});
		
		msgField.addKeyListener(new KeyListener(){
			public void keyTyped(KeyEvent e){}
		    public void keyPressed(KeyEvent e){
		    	if(e.getKeyCode()==KeyEvent.VK_ENTER){
		    		Component[] check = checkbox.getComponents();
		    		for(Component c:check){
		    			if(((JCheckBox)c).isSelected()){
		    				JScrollPane jsp = chatBoxes.get(Integer.parseInt(((JCheckBox)c).getText()));
		    				Component innerComp = jsp.getComponent(0);
		    				if(innerComp instanceof JViewport){
		    					Component ch = ((JViewport) innerComp).getComponent(0);
		    					if(ch instanceof JTextArea){
		    						((JTextArea)ch).append("Admin : " + msgField.getText() + "\n");
		    						((JTextArea)ch).setCaretPosition(((JTextArea)ch).getDocument().getLength());
		    					}
		    				}
		    				
		    				byte[] buff = new byte[1024];
		    				buff = msgField.getText().getBytes();
		    				try{
		    		    		DatagramSocket multiSocket = new DatagramSocket();
		    					multiSocket.send(new DatagramPacket(buff,buff.length,
			    						InetAddress.getByName(clientList.get(Integer.parseInt(((JCheckBox)c).getText()))),
			    						Integer.parseInt(((JCheckBox)c).getText())));
		    					multiSocket.close();
		    				}catch(Exception ex){
		    					
		    				}
		    			}
		    		}
		    		msgField.setText("");
		    	}
		    }
		    public void keyReleased(KeyEvent e){}
		});
		
		Dimension dim = Toolkit.getDefaultToolkit().getScreenSize();
		multi.setLocation(dim.width/2-multi.getSize().width/2, dim.height/2-multi.getSize().height/2);
		
		auPanel.add(audio);
		msg.add(msgField);
		main.add(auPanel);
		main.add(msg);
		main.add(checkbox);
		multi.add(main);
		multi.setVisible(true);
	}
	public void requestClient(){

		try{
			InputStream is = socket.getInputStream();
			OutputStream os = socket.getOutputStream();
			DataInputStream dis = new DataInputStream(is);
			DataOutputStream dos = new DataOutputStream(os);
			
			dos.writeUTF("giveMe");
			String client = dis.readUTF();
			if(!client.equals("empty")){
				JTextArea chatArea = new JTextArea();
				chatArea.setEditable(false);
				chatArea.setLineWrap(true);
				
				String newString = client.replace("/", "");
				String[] array = newString.split(";");
				cport = Integer.parseInt(array[1]);
				
				clients++;
				clientList.put(Integer.parseInt(array[1]),array[0]);
				
				new adminReceiveChat(chatArea,Integer.parseInt(array[1]),array[0]).start();
				//chatArea.setText(array[1]);
				//checkPanel.add(new JCheckBox(array[1]));
				for(JRadioButton box:checkboxes){
					if(box.getText().isEmpty()){
						box.setText(array[1]);
						box.setSelected(true);
						box.setEnabled(true);
						break;
					}
				}

				chatPanel = new JScrollPane(chatArea,JScrollPane.VERTICAL_SCROLLBAR_ALWAYS,JScrollPane.HORIZONTAL_SCROLLBAR_NEVER);
				chatPanel.setPreferredSize(new Dimension(420,400));
				chatPanel.setLayout(new ScrollPaneLayout());
				chatPanel.setWheelScrollingEnabled(true);
				chatPanel.setVisible(true);
				
				for(Component e : chatPane.getComponents()){
					chatPane.remove(e);
				}
				chatBoxes.put(Integer.parseInt(array[1]), chatPanel);
				
				chatPane.add(chatPanel);
				chatPane.setVisible(true);
				
				chatPanel.repaint();
				chatPane.repaint();
				chatPane.revalidate();
				checkPanel.repaint();
				new adminReceiveAudio((adminChatWindow.dSocket.getLocalPort()+1)).start();
			}
	
		}catch(IOException ioe){
			System.out.print("IO Error at request client");
		}catch(Exception e){
			System.out.print("Error at request client");
		}
	}
	public void sendExit(){
		int selectedPort = 0;
		String selectedIP = "";
		byte[] buff = new byte[1024];
		buff = "EXIT_SYSTEM".getBytes();
		try{
			Component[] comp = checkPanel.getComponents();
			for(Component c:comp){
				if(c instanceof JRadioButton){
					JRadioButton jrb = ((JRadioButton)c);
					if(!jrb.getText().equals("")){
						selectedPort = Integer.parseInt(jrb.getText());
						selectedIP = clientList.get(selectedPort);
						sendPacket = new DatagramPacket(buff,buff.length,InetAddress.getByName(selectedIP),selectedPort);
						dSocket.send(sendPacket);
					}
				}
			}
		}catch(IOException ioe){
			
		}
	}
}

class adminReceiveChat extends Thread{
	JTextArea chatArea;
	DatagramSocket chatSocket;
	DatagramPacket receive;
	String clientAdd;
	int clientPort;
	
	public adminReceiveChat(){}
	public adminReceiveChat(JTextArea chatArea,int clientPort,String clientAdd){
		this.chatArea = chatArea;
		this.clientPort = clientPort;
		this.clientAdd = clientAdd;
	}
	public void run(){
		
		byte[] buff = new byte[1024];
		try{
			chatSocket = new DatagramSocket();
			adminChatWindow.dSocket = chatSocket;
			System.out.println("Chat receiver on at port: " + chatSocket.getLocalPort());
			//buff = "Hello".getBytes();
			chatSocket.send(new DatagramPacket(buff,buff.length,InetAddress.getByName(clientAdd),clientPort));//notify client
			receive = new DatagramPacket(buff,buff.length);
			
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
			
			chatSocket.receive(receive);
			
			String newStr = new String(receive.getData(),0, receive.getLength());
			
			if(newStr.equals("EXIT_SYSTEM")){
				Component[] comp = adminChatWindow.checkPanel.getComponents();
				for(Component e:comp){
					if(e instanceof JRadioButton){
						JRadioButton rButton = (JRadioButton)e;
						if(rButton.getText().equals(String.valueOf(receive.getPort()))){
							JScrollPane jsp = adminChatWindow.chatBoxes.get(Integer.parseInt(rButton.getText()));
							adminChatWindow.chatBoxes.remove(Integer.parseInt(rButton.getText()));
							rButton.setText("");
							rButton.setEnabled(false);
							adminChatWindow.bGroup.clearSelection();
							if(jsp.getParent()!=null){
								JLayeredPane jlp = (JLayeredPane)jsp.getParent();
								System.out.println(jlp.getComponentCount());
								jlp.remove(0);
								jlp.repaint();
							}
						}
					}
				}
				adminChatWindow.clients--;
				
			}else{
				chatArea.append(receive.getPort() + " : " + newStr + "\n");
				chatArea.setCaretPosition(chatArea.getDocument().getLength());
			}
		}catch(IOException ioe){
			System.out.println("IO Error at chat receive.");
		}catch(Exception e){
			System.out.println("Error at chat receive.");
		}
	}
}
class adminReceiveAudio extends Thread{
	int datagramPort;
	DatagramSocket audioSocket;
	DatagramPacket audioPacket;

	
	public adminReceiveAudio(){}
	public adminReceiveAudio(int datagramPort){
		this.datagramPort = datagramPort;
	}
	public void run(){
		System.out.println("Audio receiver on at port: " + datagramPort);
		byte[] buff = new byte[17640];
		try{
			audioSocket = new DatagramSocket(datagramPort);
			audioPacket = new DatagramPacket(buff,buff.length);
			
		}catch(IOException ioe){
			System.out.println("Audio datagram admin failed.");
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