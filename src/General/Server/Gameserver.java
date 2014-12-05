package General.Server;

import java.io.*;
import java.net.*;
import java.util.*;

import General.Entity.Player;
import General.Utility.AnimLoader;
import General.Utility.EntityManager;
import General.Utility.Interpreter;
import General.Utility.Parent;

public class Gameserver implements Runnable,Parent{
	final int SENDPORT = 2344;
	final int RECEIVEPORT = 2343;
	final int SERVERPORT = RECEIVEPORT;
	final int DATALENGTH = 2048;
	final int MAXPLAYERS = 8;
	
	//UDP
	static boolean udp = false;
	DatagramSocket serversocket, sendSocket;
	byte[] data,sendData;
	DatagramPacket pack,sendPack;
	
	//TCP
	ServerSocket server_socket;
	ClientHandler tmp_clientHandler;
	Vector<ClientHandler> client_vector;
	Socket tmp_socket;

	long delta = 0;
	long last = 0;
	long tps = 0; //ticksPerSecond
	int tick = 0;
	
	private IdentityTable identable;
	Interpreter interpreter;
	EntityManager em;
	AnimLoader animloader;
	
	boolean started = false;
	boolean running,msgthere = false;
	static boolean guion=true;
	
	String msg,tmp;
	
	public static void main(String[] args) throws IOException{
		if(args.length>=1){
		if(args[0].equalsIgnoreCase("nogui"))
						guion = false;
		if(args[0].equalsIgnoreCase("udp"))
						udp=true;
		}
		if(args.length>=2){
			if(args[1].equalsIgnoreCase("udp"))
						udp = true;
			if(args[1].equalsIgnoreCase("nogui"))
						guion = false;
		}
			
		new Gameserver();
		
	}
	Gameserver() throws IOException{
				if(guion) println("start the server with java -jar Gameserver.jar nogui \n to start the server without gui.");
				doInitializations();

				println("Server set up and now waiting for connectionrequests...");
				/*
				 * This part is ridicously important: the run() method handles logic
				 * this has to handle communication between server & clients
				 * Receive packets and assign them to the right client, buffer them up for the serverloop()
				 */
				if(udp){
					println("Started server in UDP mode.");
					while(running){
						serversocket.receive(pack);
						tmp = new String(pack.getData(),0,pack.getLength(),"utf-8");
						if(getIdentityTable().contains(pack.getAddress())){
							//known client
							interpreter.handle(getIdentityTable().resolveIP(pack.getAddress()),Interpreter.split(new String(pack.getData(),0,pack.getLength(),"utf-8")));
						}else if(Interpreter.split(new String(pack.getData(),0, pack.getLength(),"utf-8"))[0].equals("000")){
							//new client
							println("New player connecting from: "+pack.getAddress());
							em.add(new Player(animloader,getIdentityTable().resolveIP(pack.getAddress()),0,0,this));
							send("ALL","000#"+Interpreter.split(new String(pack.getData(),0, pack.getLength(),"utf-8"))[1]);
							getIdentityTable().addIdentity(Interpreter.split(new String(pack.getData(),0, pack.getLength(),"utf-8"))[1], pack.getAddress());
							serversocket.send(new DatagramPacket(pack.getData(), pack.getLength(),pack.getAddress(),pack.getPort()));
						}
						else{ // wrong verification from unknown client
							println("Attempt from "+pack.getAddress().toString()+" , message: "+new String(pack.getData(),0, pack.getLength(),"utf-8"));
						}
					}
				}
				/*
				else{
					println("Started server in TCP mode.");
					server_socket = new ServerSocket(SERVERPORT);
					client_vector = new Vector<ClientHandler>();
					while(running){
						tmp_socket=new Socket();
						tmp_socket=server_socket.accept();
						client_vector.add(tmp_clientHandler = new ClientHandler(this,tmp_socket));
						tmp_clientHandler.start();
					}
				}
				*/
	}
	
	public void send(String toWho, String cmd){
		if(toWho.equalsIgnoreCase("ALL")){
			//send to every connected player
			for(int i=0;i<MAXPLAYERS;i++){
				if(!getIdentityTable().getAllPlayerNames()[i].equals("")){
					try {
						sendSocket.send(new DatagramPacket(cmd.getBytes(),cmd.getBytes().length,getIdentityTable().getAllIPs()[i],SENDPORT));
					} catch (IOException e) {
						println("Error while sending package! "+e.toString());
					}
				}
			}
		}else{
			//send to a specific player
			try {
				sendSocket.send(new DatagramPacket(cmd.getBytes(),cmd.getBytes().length,getIdentityTable().resolveName(toWho),SENDPORT));
			} catch (IOException e) {
				System.out.println("Error while sending package! "+e.toString());
			}
		}
	}
	private void doInitializations() throws IOException {
		
		setIdentityTable(new IdentityTable(MAXPLAYERS));
		interpreter = new Interpreter(this);
		em = new EntityManager(this);
		animloader = new AnimLoader();

		tmp = new String();
		data = new byte[DATALENGTH];
		sendData = new byte[DATALENGTH];
		sendPack = new DatagramPacket(sendData,sendData.length);
		pack = new DatagramPacket(data,data.length);
		
		last = System.nanoTime();
		if(guion){
			new SUI(this);
		}
		else{
			NOGUIconsoleUpdater th=new NOGUIconsoleUpdater(this);
			th.start();
		}

		Thread th = new Thread(this);
		th.start();
		serversocket = new DatagramSocket(RECEIVEPORT);
		sendSocket = new DatagramSocket();
		
		running=true;
		println("Server set up - running!");
		//started=true;
	}
	// ONE SERVER TICK:
	public void run() {
		while(started){
			try {
				deleteDisc();
				computeDelta();	
				handleReceivedPackages();
				
			} catch (IOException e1) {
				e1.printStackTrace();
			}
			
			doLogic();
			moveObjects();
			

			try {
				sendPackages();
				Thread.sleep(50); // 20 ticks per second
			} catch (InterruptedException e) {
				println(e.toString());
			} catch (IOException e) {
				println(e.toString());
			}
		}
	}
	
	void sendPackages() throws IOException{
		
	}
	void handleReceivedPackages() throws IOException{
		
	}
	
	void deleteDisc() throws IOException{
		//handle disconnected players, delete them from list of connected players
	}
	void broadcast(String msg,String fromwho) throws IOException{
		//sends a message to all clients! @fromwho : from who is the message? 
		println("["+fromwho+"]:"+msg);
		send("ALL","["+fromwho+"]:"+msg);
	}
	public void print(String msg){
		System.out.print(msg);
	}
	public void println(String msg){ print(msg+"\n");}
	
	private void moveObjects() {
		//moveObjects for sprites
		em.moveObjects(delta);		
	}
	
	private void doLogic() {
		
		//Tickberechnung & Anzeige
		tick++;
		if(tick>=65536)
				tick=0;
		
		// doLogic for sprites
		em.doLogic(delta);
	}
	
	
	private void computeDelta() {
		delta = System.nanoTime() - last;
		last = System.nanoTime();
		tps = ((long)1e9/delta);
		
	}
	
	public void servercommand(String cmd){
		if(cmd.equals("/list")){
			println(getIdentityTable().getPlayers());
		}else if(cmd.length()>6 && cmd.substring(0, 5).equals("/kick")){
			getIdentityTable().kickPlayer(cmd.substring(6));
		}
		else if(cmd.equals("/stop")){
			System.exit(0);
		}else if(cmd.charAt(0)=='/'){ println("Unknown servercommand.");
		}else{
			try {
				interpreter.handle("SERVER", Interpreter.split("003#SERVER#"+cmd));
			} catch (IOException e) {
				println(e.toString());
			}
		}
	}
	public IdentityTable getIdentityTable() {
		return identable;
	}
	private void setIdentityTable(IdentityTable identable) {
		this.identable = identable;
	}
	public int getTick(){ return this.tick;}
	public EntityManager getEntityManager(){ return this.em;}
	public AnimLoader getAnimLoader(){ return this.animloader;}
}

class ClientHandler extends Thread{
	Gameserver server;
	String msg;
	Socket sock;
	InputStreamReader isr;
	BufferedReader br;
	boolean remove = false;


	ClientHandler(Gameserver server,Socket sock){
		this.server=server;
		this.sock=sock;
		try{
		br = new BufferedReader(isr = new InputStreamReader(sock.getInputStream()));
		}catch(Exception e){
			this.server.println(e.toString());
		}
	}

	public void run(){
		while(!remove){
			try{
			msg = br.readLine();
			}catch(Exception e){
				this.server.println(e.toString());
			}
			
		}
		//remove!
	}

}

class NOGUIconsoleUpdater extends Thread{
	Gameserver server;
	InputStreamReader isr;
	BufferedReader br;
	String msg;
	
	NOGUIconsoleUpdater(Gameserver server){
		this.server=server;
		br = new BufferedReader(new InputStreamReader(System.in));
		server.println("NOGUI-mode started.");
	}
	
	public void run(){
		while (true)
		  {
			try {
				Thread.sleep(200);
				
				while (System.in.available() > 0){
					msg = br.readLine();
					if(msg.length()>0){
						server.servercommand(msg);
					}else
						server.println("No command entered. Read the README text-file for help.");
				}
			} catch (IOException e) {
				server.println("I/O Exception in NOGUI-ConsoleUpdater! "+e.toString());
			}catch (InterruptedException e1) {
				server.println("NOGUI-ConsoleUpdater interrupted (Thread)! "+e1.toString());
			}
		  }
	}
}
