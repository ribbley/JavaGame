package General;

import General.Entity.*;
import General.UI.*;
import General.Utility.*;
import General.Utility.Package;
import General.Map.*;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Component;
import java.awt.Container;
import java.awt.Dimension;
import java.awt.Font;
import java.awt.FontMetrics;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.KeyEvent;
import java.awt.event.KeyListener;
import java.awt.image.BufferStrategy;
import java.io.*;
import java.net.*;
import java.util.ListIterator;
import java.util.Vector;

import javax.swing.*;


public class Game extends JPanel implements Runnable, KeyListener,Parent{
	
	private static final long serialVersionUID = 1L;
	final int UPDATETIME = 30; // value says, how many packets will be send in 1 second for positioning
	
	long delta = 0;
	long last = 0;
	int fps = 0;
	int tick = 0;
	
	String ip;
	ConnectI coni;
	JFrame frame;
	JTextField chatfield;
	
	public Chat chat;
	Interpreter interpreter;
	Chatcmd chatcmd;
	AnimLoader al;
	EntityManager em;
	PackageHandler ph;
	static LoginForm loginform;

	//GUI
	boolean fps_on=false;
	boolean tick_on=false;
	public boolean entity_info_on=false;
	//ENDGUI
	
	//GRAPHICS
	FontMetrics fm=null;
	BufferStrategy strategy;
	Graphics2D g;
	
	boolean up = false;
	boolean down = false;
	boolean right = false;
	boolean left = false;
	boolean started = false;
	
	double[] xarray;
	double[] yarray;
	
	public double playerx,playery; //SECOND PLAYER_VARIABLES
	
	
	Me me;
	public Me getMe(){return this.me;}
	
	Vector<Package> packages_to_handle = new Vector<Package>();
	Vector<Entity> vectorpainter = new Vector<Entity>();
	Vector<Chunk> chunkvector;
	
	public static void main(String[] args) throws UnknownHostException, IOException{
		readConfig();
	}
	public static void readConfig(){
		/* This part reads config and loads default configurations into the loginform */
		String[][] template = TextAquirer.readConfig(TextAquirer.path,TextAquirer.defaultText);
		String user=null,ip=null;
		boolean opengl=false;
		for(int j=0;j<template.length;j++){
			if(template[j][0].equals("forceOpenGL")){
				if(template[j][1].equalsIgnoreCase("true")){
					/* Enables OpenGL acceleration: */
					System.setProperty("sun.java2d.opengl","True");
					opengl=true;
				}
			}else if(template[j][0].equals("ServerIP")){
				ip=template[j][1];
			}else if(template[j][0].equals("Username")){
				user=template[j][1];
			}
		}
		loginform = new LoginForm(user,ip,opengl);
	}
	public static void next(){
		/* This part reads config and loads default configurations into the loginform */
		String[][] template = TextAquirer.readConfig(TextAquirer.path,TextAquirer.defaultText);
		String user=loginform.getUsernickname(),ip=loginform.getServerIP();
		if(loginform.getOpenGL()) TextAquirer.editConfig("forceOpenGL", "true"); else TextAquirer.editConfig("forceOpenGL", "false");
		TextAquirer.editConfig("ServerIP", ip);
		TextAquirer.editConfig("Username", user);
		
		try {
			new Game(1200,800);
		} catch (UnknownHostException e) {
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		}
	}
	Game(int w, int h) throws UnknownHostException, IOException{
		setLayout(new BorderLayout());
	    chatfield = new JTextField(30);
	    chatfield.addActionListener(new ActionListener() {
			
			@Override
			public void actionPerformed(ActionEvent arg0) {
				println(chatfield.getText());
				chatcmd.cmdoff();
			}
		});
	    chatfield.setSize(getPreferredSize());
	    chatfield.setLocation(10, h-15);
		frame = new JFrame("GameMap");
		this.setPreferredSize(new Dimension(w,h-15));
		this.setDoubleBuffered(true);
		frame.setLocation(70,100);
		frame.add(this);
		frame.pack();			
		frame.addWindowListener(new WindowClosingAdapter(this));
		frame.addKeyListener(this);
		/* BEGINNING NEW PART */
		frame.setIgnoreRepaint(true);
		frame.createBufferStrategy(2);
		strategy = frame.getBufferStrategy();
		/*END NEW PART*/
		frame.setVisible(true);
		//Print info if essential settings were changed!
	    if(loginform.getRestartRequired()){
			JFrame prompt = new JFrame("Restart Required!");
			prompt.setLocation(200,200);
			prompt.setSize(new Dimension(500,70));
			JLabel promptlabel = new JLabel("You need to restart the game, to make some settings take effect!");
			JPanel promptpanel = new JPanel();
			promptpanel.add(promptlabel);	
			prompt.add(promptpanel);
			prompt.addWindowListener(new WindowClosingAdapter(null));
			prompt.setVisible(true);
			}
			
		coni = new ConnectI(this,loginform);
		chatcmd = new Chatcmd(this,coni);
		
		Thread th = new Thread(this);
		th.start();
	}
	
	private void doInitializations() throws IOException {	
		interpreter = new Interpreter(this);
		last = System.nanoTime();
		al=new AnimLoader();
		em=new EntityManager(this);
		ph=new PackageHandler(this);
		fm=strategy.getDrawGraphics().getFontMetrics();
		if(coni.getVerified()){
			chat = new Chat(al,coni,this);
			}
		chunkvector = new Vector<Chunk>();
		
		me = new Me(al.getSprite("players/TestingFig.gif", 1),0,0,this,coni.getUsernickname());
		getEntityManager().add(getMe());		
		
		Chunk chunk1 = new Chunk(al,0,0,this,20);
		// GroupGreenblurb group1 = new GroupGreenblurb(spritevector, 1, 0, 0, 1, 1, this);
		
		chunkvector.add(chunk1);
		if(coni.getUsernickname().equalsIgnoreCase("")){
			coni.setUsernickname("Player");
		}
		if(coni.getVerified()){
			//connected?
		}
		started = true;
		for(int j=0;j<20;j++){
			em.add(new Greenblurb(al.getSprite("enemys/greenblurbwalk.gif", 4),0,0,this));
		}
	}
	
	
	@Override
	public void paintComponent(Graphics g){
		super.paintComponent(g);
		
	}

	@Override
	//GAMELOOP!!!
	public void run() {
		while(frame.isVisible()){
			
			computeDelta();
			tick++;
			if(tick>=65536)
					tick=0;
			
			if(started){
			handleActions();
			checkKeys();
			if(coni.getVerified())
			try {
				sendMsg();
				sendPackages();
			} catch (IOException e) {
				e.printStackTrace();
			}
			}
			// GRAPHICS DRAWING
			if(started){
				try{
				g = (Graphics2D) strategy.getDrawGraphics();
				g.setColor(Color.white);
				g.fillRect(0,0,frame.getWidth(),frame.getHeight());
				
				em.cloneVectors();
				em.drawChunks(g,this.chunkvector);
				em.doLogic(delta);
				em.moveObjects(delta);
				em.drawObjects(g);
				//Chat
				
				chat.update(g);
				
				//Framerate Anzeige
				if(fps_on){
					g.setColor(Color.red);
					g.drawString("FPS: "+fps, 20, 40);
				}
				
				if(tick_on){
					g.setColor(Color.red);
					g.drawString("Tick: "+tick,80,40);
				}
				
				//end drawing
				g.dispose();
				}catch (IOException e) {
					println("I/O Exception while updating chat! "+e.toString());
				}finally{
				strategy.show();
				}
			}
			
			try{
				if(fps>100)
				Thread.sleep(1000/fps);
				else
				Thread.sleep(10);
				
			}catch(InterruptedException e){
				println(e.toString());
			}
		}
	}

	private void handleActions() {
		for(ListIterator<Package> it = packages_to_handle.listIterator();it.hasNext();){
			Package r = it.next();
			ph.handle(r);
			it.remove();
		}
	}
	
	private void sendMsg() throws IOException{
		if(chatcmd.getMsgtopost()){
			//coni.Username..
			chatcmd.setMsgtopost(false);
		}
	}
	
	private void sendPackages() throws IOException{
		
	}
	public void send(String toWho, String msg){
		//private messages : toWho
		getConnectI().sendPackage(msg);
	}
	private void doLogic() {
		em.doLogic(delta);
	}
	public ConnectI getConnectI(){ return this.coni; }
	public EntityManager getEntityManager(){ return this.em;}
	public int getFPS(){ return fps;}
	public int getHeight(){ return this.frame.getHeight();}
	public int getWidth(){ return this.frame.getWidth();}
	public double getplayerx(){
		return playerx;
	}
	public double getplayery(){
		return playery;
	}
	private void checkKeys() {
		if(up){
			getMe().setVerticalSpeed(-(getMe().getSpeed()));
		}
		if(down){
			getMe().setVerticalSpeed(getMe().getSpeed());
		}
		if(right){
			getMe().setHorizontalSpeed(getMe().getSpeed());
		}
		if(left){
			getMe().setHorizontalSpeed(-(getMe().getSpeed()));
		}
		if(!up&&!down){
			getMe().setVerticalSpeed(0);
		}
		if(!left&&!right){
			getMe().setHorizontalSpeed(0);
		}
	}
	private void computeDelta() {
		delta = System.nanoTime() - last;
		last = System.nanoTime();
		fps = (int)((long)1e9/delta);
		
	}
	
	/*
	 * This method occurs when Exceptions occur, or when something has to be written to the console.
	 * prints a fail msg to standardoutput.
	 */
	public void print(String msg){
		System.out.println(msg);
	}
	public void println(String msg){ print(msg+"\n");}
	
	public void stop(){ this.started=false;}
	@Override
	public void keyPressed(KeyEvent arg0) {
		if(arg0.getKeyCode() == KeyEvent.VK_W){
			up=true;
		}
		if(arg0.getKeyCode() == KeyEvent.VK_S){
			down=true;
		}
		if(arg0.getKeyCode() == KeyEvent.VK_D){
			right=true;
		}
		if(arg0.getKeyCode() == KeyEvent.VK_A){
			left=true;
		}
		}
	
	public void keyReleased(KeyEvent arg0) {
		
		if(arg0.getKeyCode() == KeyEvent.VK_W){
			up=false;
		}
		if(arg0.getKeyCode() == KeyEvent.VK_S){
			down=false;
		}
		if(arg0.getKeyCode() == KeyEvent.VK_D){
			right=false;
		}
		if(arg0.getKeyCode() == KeyEvent.VK_A){
			left=false;	
		}
		if(arg0.getKeyCode() == KeyEvent.VK_F9){
			entity_info_on=!entity_info_on;
			if(entity_info_on) println("EntityInfo display activated."); else println("EntityInfo display deactivated");
		}
		if(arg0.getKeyCode() == KeyEvent.VK_F11){
			tick_on=!tick_on;
			if(tick_on) println("Tick display activated."); else println("Tick display deactivated.");
		}
		if(arg0.getKeyCode() == KeyEvent.VK_F10){
			fps_on=!fps_on;
			if(fps_on) println("FPS display activated."); else println("FPS display deactivated.");
		}
		if(arg0.getKeyCode() == KeyEvent.VK_T){
			chatcmd.setCmdon(true);
			chatcmd.cmdturnon();
		}
		if(arg0.getKeyCode() == KeyEvent.VK_ENTER){
			if(!started){
				println("Game started.");
					try {
						doInitializations();
					} catch (IOException e) {
						println(e.toString());
					}
			}else println("Enter pressed.");
		}
		
	}
	public void keyTyped(KeyEvent arg0) {
	}
	public void spawnplayer(String string) {
		// TODO ADD UNIQUE PLAYERMODEL OPTION!
		if(!string.equals(getMe().getID())){
		println("Assigning new Player with ID:"+string);
		new Player(al,string,0,0,this);
		}
	}
	
	public int getTick() {
		return this.tick;
	}
	
	public AnimLoader getAnimLoader(){ return this.al;}
	private void setAnimLoader(AnimLoader al){ this.al=al;}
	public JFrame getFrame() {
		return this.frame;
	}
	public JTextField getChatfield() {
		return this.chatfield;
	}
	public Chat getChat() {
		return this.chat;
	}
	public Interpreter getInterpreter(){return this.interpreter;}
	public FontMetrics getFontMetrics(){if(this.fm!=null) return this.fm;
	else throw new NullPointerException();
	} 
}


