package General.UI;

import General.Game;
import General.Entity.Sprite;
import General.Utility.AnimLoader;
import General.Utility.ConnectI;

import java.awt.Color;
import java.awt.Graphics;
import java.io.IOException;
import java.util.ListIterator;
import java.util.LinkedList;
import java.util.Iterator;


public class Chat {
	LinkedList<String> messagebuffer;
	Game frame;
	ConnectI coni;
	Sprite sp;
	
	public Chat(AnimLoader al,ConnectI coni, Game frame){
		messagebuffer = new LinkedList<String>();
		this.sp=al.getSprite("UI/chatbackground.gif", 1);
		this.coni=coni;
		this.frame=frame;
		printMessage("GAME","Welcome to the game!");
		printMessage("GAME","Read the README.txt for more information.");
		
	}

	public void update(Graphics g) throws IOException {
		
		g.setColor(Color.BLACK);
		g.fillRect(20, (int)(2./3.*frame.getHeight()),(int) (1./3.*frame.getWidth()), (int)(1./3.*frame.getHeight()-5));
		g.setColor(Color.WHITE);
		g.drawString("World - Chat", 30, (int)(2./3.*frame.getHeight()+5));
		LinkedList tmp = (LinkedList) (messagebuffer.clone());
		Iterator it=tmp.descendingIterator();
		for(int i=0;i<(1./3.*((double)(frame.getHeight()))/12.)-2 && it.hasNext();i++){
			g.drawString((String) it.next(), 23, (int) (frame.getHeight())-12-(i*12));
		}
		
	}

	public void printMessage(String from, String msg) {
		if(frame.getFontMetrics().stringWidth(msg)>(1./3.*frame.getWidth()-30)){
			//test wordwise
			int maxindex=0;
			for(int j=0;j<msg.length();j++){
				if(msg.charAt(j)==' '){
					if(frame.getFontMetrics().stringWidth(msg.substring(0,j))<(1./3.*frame.getWidth()-30)){
						maxindex=j;
					}
					else{
						messagebuffer.add(new String("["+from+"]:"+msg.substring(0,maxindex)));
						appendMessage(msg.substring(maxindex+1,msg.length()));
						break;
					}
				}
			}
		}
		else{
		messagebuffer.add(new String("["+from+"]:"+msg));
		}
	}
	public void appendMessage(String msg) {
		if(frame.getFontMetrics().stringWidth(msg)>(1./3.*frame.getWidth()-30)){
			//test wordwise
			int maxindex=0;
			for(int j=0;j<msg.length();j++){
				if(msg.charAt(j)==' '){
					if(frame.getFontMetrics().stringWidth(msg.substring(0,j))<(1./3.*frame.getWidth()-30)){
						maxindex=j;
					}
					else{
						messagebuffer.add(new String(msg.substring(0,maxindex)));
						appendMessage(msg.substring(maxindex+1, msg.length()));
						break;
					}
				}
			}
		}
		else{
		messagebuffer.add(new String(msg));
		}
	}

	public LinkedList<String> getMessagebuffer() {
		return this.messagebuffer;
	}
}
