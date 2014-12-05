package General.Utility;

import General.Game;
import General.UI.LoginForm;

import java.awt.BorderLayout;
import java.awt.GridLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.KeyEvent;

import javax.swing.BorderFactory;
import javax.swing.JButton;
import javax.swing.JCheckBox;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JTextField;
import javax.swing.SwingConstants;
import javax.swing.event.CaretEvent;
import javax.swing.event.CaretListener;

import java.io.*;
import java.net.*;

/*
 * This Class implements an object which task is to build up a connection to the server
 * and handle the incoming and outgoing packets
 * 
 * For communication we use UDP, cause TCP is to slow for networking games.
 * @First we send a packet containing our usernickname afterwards we wait for verification from the server-side
 * 
 * after establishing connection and beeing verified,
 * the normal loop containing the receiving of packets will be run.
 */
public class ConnectI implements Runnable{
	final int DATALENGTH=2048;
	final int SENDPORT = 2343;
	final int RECEIVEPORT=2344;
	
	Game p;
	long last=0,delta=0;
	int rtt=0; //roundtriptime (also called : ping) - time to travel to server & back
	DatagramSocket receiveSock,sendSock;
	DatagramPacket sendPack,receivePack;
	byte[] sendData,receiveData;
	String Usernickname ="Player";
	private String serverip;
	JFrame ConnectionGUI;
	boolean verified = false;
	
	
	public void run(){

		p.println("Starting listening thread ...");
			//wait for first package that verifies identity
			try {
				while(!verified){
					receiveSock.receive(receivePack);
					delta=System.nanoTime()-last;
					last=System.nanoTime();
					rtt=(int) (delta/((long)1e6));
					if(new String(receivePack.getData(),0,receivePack.getLength(),"utf-8").equals("000#"+Usernickname))
						verified=true;
					else System.out.print("Wrong return value of identification package!("+(new String(receivePack.getData(),0,receivePack.getLength(),"utf-8"))+")");
					p.println("Latency:"+rtt+" ms");
				}
			} catch (IOException e) {
				p.println("I/O Exception while receiving identity verification. "+e.toString());
			}
			p.println("Connected to:"+receivePack.getAddress().toString());
			
			
		while(true){
			//receive Packages
			try {
				receiveSock.receive(receivePack);
				p.getInterpreter().handle("SERVER",Interpreter.split(new String(receivePack.getData(),0,receivePack.getLength(),"utf-8")));
			} catch (IOException e) {
				p.println("I/O Error while receiving packet. (ConnectI.java)");
				e.printStackTrace();
			}
		}
	}
	
	public void sendPackage(String msg){
		try {
			sendPack = new DatagramPacket(msg.getBytes("utf-8"),msg.getBytes("utf-8").length,InetAddress.getByName(getServerip()),SENDPORT);
		} catch (UnsupportedEncodingException e1) {
			p.println("Unsupp Encoding: ConnectI.java ");
			e1.printStackTrace();
		} catch (UnknownHostException e1) {
			p.println("Unknown Host! : ConnectI.java");
			e1.printStackTrace();
		}
		try {
			sendSock.send(sendPack);
		} catch (IOException e) {
			p.println("IO Error while sending Package (while game is runnin')");
			e.printStackTrace();
		}
	}
	
	void connect(){
		if(Usernickname.equals(""))
			Usernickname="Player";
		
			try {
				sendData = ("000#"+Usernickname).getBytes("utf-8");
				sendPack = new DatagramPacket(sendData,sendData.length,InetAddress.getByName(getServerip()),SENDPORT);
				new Thread(this).start();
				while(!verified){
				sendSock.send(sendPack);
				p.println("That was sent to server: "+new String(sendData,"utf-8"));
				last=System.nanoTime();
				Thread.sleep(1000);
				}
			} catch (IOException e) {
				p.println("Is "+getServerip()+" running the server (on the right port? ["+SENDPORT+"])");
				e.printStackTrace();
			} catch (InterruptedException e) {
				p.println("Thread interrupted while sending identity! ");
				e.printStackTrace();
			}
	}
	
	
	public ConnectI(Game p,LoginForm loginform){
		//Processing loginform data.
		if(loginform.getUsernickname().length()>0)
		this.Usernickname=loginform.getUsernickname();
		this.setServerip(loginform.getServerIP());
		p.println("Extracted from LoginForm:"+Usernickname+" "+getServerip());
		
		//connecting.
		receiveData = new byte[DATALENGTH];
		sendData=new byte[DATALENGTH];
		sendPack=new DatagramPacket(sendData,sendData.length);
		receivePack=new DatagramPacket(receiveData,receiveData.length);
		try{
			receiveSock = new DatagramSocket(RECEIVEPORT);
			sendSock = new DatagramSocket();
		} catch (SocketException e1) {
			p.println("SocketException while initialising ConnectI - Port is used by other program! ");
			e1.printStackTrace();
		}
		this.p=p;
		connect();
	}

	public void disconnect(){
		try {
			sendData=("001#"+Usernickname).getBytes("utf-8");
		
		sendPack = new DatagramPacket(sendData,sendData.length,InetAddress.getByName(getServerip()),SENDPORT);
		sendSock.send(sendPack);
		} catch (UnsupportedEncodingException e) {
			//utf-8 is defined everywhere!
			p.println("UnsupportedEncoding while disconnecting.");
			e.printStackTrace();
		} catch (UnknownHostException e) {
			p.println("Sending disconnectsymbol to unknownhost.");
			e.printStackTrace();
		} catch (IOException e) {
			p.println("I/O Error: serverport is not "+SENDPORT);
			e.printStackTrace();
		}
	}

	public boolean getVerified() {
		return this.verified;
	}

	public String getUsernickname(){ return this.Usernickname;}
	public void setUsernickname(String string) {
		this.Usernickname=string;
	}

	public String getServerip() {
		return serverip;
	}

	public void setServerip(String serverip) {
		this.serverip = serverip;
	}
}


	
	

