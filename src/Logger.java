//Author : Vincent Lim
//Email  : vince.lim@outlook.com

import java.awt.*;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;
import javax.swing.*;

public class Logger {

	public JLabel adminLabel = new JLabel("ADMIN");
	public JLabel clientLabel = new JLabel("CLIENT");
	public JTextArea adminIP = new JTextArea();
	public JTextArea clientIP = new JTextArea();
	
	public Logger(){
		init();
	}
	
	public void init(){
		JFrame main = new JFrame("Server Logger");
		JPanel mainPanel = new JPanel(new FlowLayout());
		JPanel labelPanel = new JPanel(new GridLayout(1,2));
		JPanel textPanel = new JPanel(new GridLayout(1,2));
		adminLabel.setHorizontalAlignment(JLabel.CENTER);
		clientLabel.setHorizontalAlignment(JLabel.CENTER);
		adminIP.setLineWrap(true);
		clientIP.setLineWrap(true);
		adminIP.setEditable(false);
		clientIP.setEditable(false);
		JScrollPane adminScroll = new JScrollPane(adminIP,JScrollPane.VERTICAL_SCROLLBAR_ALWAYS,
				JScrollPane.HORIZONTAL_SCROLLBAR_NEVER);
		JScrollPane clientScroll = new JScrollPane(clientIP,JScrollPane.VERTICAL_SCROLLBAR_ALWAYS,
				JScrollPane.HORIZONTAL_SCROLLBAR_NEVER);
		adminScroll.setLayout(new ScrollPaneLayout());
		adminScroll.setWheelScrollingEnabled(true);
		adminScroll.setBackground(Color.blue);
		clientScroll.setLayout(new ScrollPaneLayout());
		clientScroll.setWheelScrollingEnabled(true);
		
		main.setSize(400,400);
		labelPanel.setPreferredSize(new Dimension(390,30));
		textPanel.setPreferredSize(new Dimension(390,360));
		Dimension dim = Toolkit.getDefaultToolkit().getScreenSize();
		main.setLocation(dim.width/4+main.getSize().width, dim.height/4-main.getSize().height/4);
		
		main.addWindowListener(new WindowAdapter() {
			public void windowClosing(WindowEvent windowEvent){
				System.exit(0);
			}        
		}); 
		
		mainPanel.add(labelPanel);
		mainPanel.add(textPanel);
		labelPanel.add(adminLabel);
		labelPanel.add(clientLabel);
		textPanel.add(adminIP);
		textPanel.add(clientIP);
		main.add(mainPanel);
		
		main.setVisible(true);
	}
}
