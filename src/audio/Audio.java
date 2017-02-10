//Author : Vincent Lim
//Email  : vince.lim@outlook.com

package audio;

import java.net.*;

import javax.sound.sampled.TargetDataLine;

public class Audio extends Thread{
	
	public static volatile boolean on = false;
	DatagramPacket sendPacket;
	DatagramSocket dSocket;
	TargetDataLine targetLine;
	String selectedIP;
	int selectedPort;
	
	public Audio(){}
	public Audio(DatagramPacket sendPacket,DatagramSocket dSocket,TargetDataLine targetLine,String selectedIP,int selectedPort){
		this.sendPacket = sendPacket;
		this.dSocket = dSocket;
		this.targetLine = targetLine;
		this.selectedIP = selectedIP;
		this.selectedPort = selectedPort;
		Audio.on = true;
	}
	public void run(){
		
		byte[] buffer = new byte[targetLine.getBufferSize() / 5];
		try{
			while(true){
				while(on){
					targetLine.read(buffer, 0, buffer.length);
					sendPacket = new DatagramPacket(buffer,buffer.length,InetAddress.getByName(selectedIP),selectedPort+1);
					dSocket.send(sendPacket);
				}
			}
		}catch(Exception e){
			
		}

		
	}
}
