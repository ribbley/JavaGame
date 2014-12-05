package General.UI;

import General.Game;
import General.Utility.ConnectI;
import General.Utility.Message;

import java.awt.BorderLayout;
import java.io.UnsupportedEncodingException;
import java.net.UnknownHostException;

public class Chatcmd{
	boolean cmdon=false;
	Game p;
	int x,y;
	StringBuffer eingabe,sb;
	int c;
	ConnectI coni;
	String msg;
	boolean msgtopost=false;
	
	public Chatcmd(final Game p,ConnectI congui){
		this.coni=congui;
		this.p=p;
		x=10;
		y=p.getHeight()-10;
		
	}
	public void cmdturnon(){
		p.add(p.getChatfield(),BorderLayout.SOUTH);
		p.getFrame().pack();
		p.getChatfield().requestFocus();
	}
	public void cmdoff(){
		if(!p.getChatfield().getText().equalsIgnoreCase("")){
		try {
			new Message(p,p.getChatfield().getText());
		} catch (UnsupportedEncodingException e) {
			e.printStackTrace();
		} catch (UnknownHostException e) {
			p.println(coni.getServerip() + " is an unknown host!");
			e.printStackTrace();
		}
		}
		p.getChatfield().setText("");
		p.remove(p.getChatfield());
		p.getFrame().pack();
		p.getFrame().requestFocus();
		cmdon=false;
		sb = new StringBuffer();
	}
	public boolean getMsgtopost() {
		return msgtopost;
	}	
	public void setMsgtopost(boolean t){
		this.msgtopost=t;
	}
	public void setCmdon(boolean b) {
		this.cmdon=b;		
	}
}
	
