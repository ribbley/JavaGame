package General.Utility;

import General.Game;
import General.Entity.Player;
import General.Server.Gameserver;
import General.Utility.Parent;

import java.io.IOException;
import java.net.DatagramPacket;


public class Interpreter {
	final static char splitchar = '#';
	Parent parent;
	
	public Interpreter(Parent g){
		this.parent=g;
	}
	public static String[] split(String cmd){
		int j=0,cnt=0;
		// 		Count the splitcharacters
		for(int i=0;i<cmd.length();i++){
			if(cmd.charAt(i)==splitchar)
				cnt++;
		}
		String[] tmp = new String[cnt+1];
		cnt=0;
		for(int i=0;i<cmd.length();i++){
			if(cmd.charAt(i)==splitchar){
				//splitpoint!
				tmp[cnt++]=cmd.substring(j, i);
				j=i+1;
			}
		}
		tmp[cnt]=cmd.substring(j);
		return tmp;
	}
	public static String[] splitBy(char sc,String cmd){
		int j=0,cnt=0;
		// 		Count the splitcharacters
		for(int i=0;i<cmd.length();i++){
			if(cmd.charAt(i)==sc)
				cnt++;
		}
		String[] tmp = new String[cnt+1];
		cnt=0;
		for(int i=0;i<cmd.length();i++){
			if(cmd.charAt(i)==sc){
				//splitpoint!
				tmp[cnt++]=cmd.substring(j, i);
				j=i+1;
			}
		}
		tmp[cnt]=cmd.substring(j);
		return tmp;
	}
	/*
	 * VERY IMPORTANT PART - here are passing all packets from server, so here it is essential to improve performance
	 * TODO build binary tree for the numbers.
	 */
	public void handle(String user, String[] cmd) throws IOException{
		if(parent instanceof Game){
			/***** GAME CLIENT *****/
		if(cmd[0].equals("000")){
			//new player
			parent.getEntityManager().getEntityVector().add(new Player(parent.getAnimLoader(),cmd[1],0,0,parent));
		}else if(cmd[0].equals("001")){
			//disconnecting player
			parent.getEntityManager().delete(cmd[2]);
		}else if(cmd[0].equals("003")){
			//message
			int v=3;
			while(v < cmd.length){
					cmd[2]+='#'+cmd[v++];
				}
			((Game)parent).getChat().printMessage(cmd[1],cmd[2]);
			
		}else if(cmd[0].equals("010")){
			
		}else if(cmd[0].equals("012")){
			//switch enable status!
			if(cmd[3].equals("true"))
			parent.getEntityManager().setEnabled(cmd[2],true);
			else
			parent.getEntityManager().setEnabled(cmd[2],false);
			
		}else{
			parent.println("Don't know how to handle:"+cmd[0]);
		}
		}else{ /****** SERVER ******/
				/****** SERVER COMMANDS ******/
			if(cmd[0].equals("000")){
				//resending verification packet for clients (packet got lost - so resend!)
				parent.send(user,"000#"+user);
			}else if(cmd[0].equals("001")){
				//deletes a player (disconnected)
				((Gameserver)parent).getIdentityTable().deleteIdentity(cmd[1]);
				parent.send("ALL","001#"+user);
			}else if(cmd[0].equals("002")){
				//Show playerlist
				parent.send(user,"003#SERVER#"+((Gameserver) parent).getIdentityTable().getPlayers());
			}else if(cmd[0].equals("003")){
				//Message
				int v=3;
				while(v < cmd.length){
					cmd[2]+='#'+cmd[v++];
				}
				parent.send("ALL", "003#"+user+"#"+cmd[2]);
				System.out.println("["+user+"]:"+cmd[2]);
				//TODO handle tick
			}else if(cmd[0].equals("004")){
				//Client does not know id in cmd[2] or it is not enabled!
				String[] tmp=Interpreter.splitBy('\n',parent.getEntityManager().getFullStatus(cmd[2]));
				for(int j=0;j<tmp.length;j++){
					parent.send(user,tmp[j]);
				}
			}else
				/****** OTHER ******/
				
			if(cmd[0].equals("010")){
				//handles new position updates!
				
			}else{
			
				parent.println("Don't know how to handle:"+cmd[0]);
			}
		
		}
	}
}
