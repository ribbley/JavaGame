package General.Server;

import java.net.*;


public class IdentityTable{
	private InetAddress[] ip;
	private String[] name;
	private int connected=0;
	
	public IdentityTable(int size) throws UnknownHostException{
		ip = new InetAddress[size];
		name = new String[size];
		for(int i=0;i<size;i++){
			name[i]=new String("");
			ip[i]=InetAddress.getByAddress(new byte[]{0,0,0,0});
		}
	}
	
	public InetAddress[] getAllIPs(){return ip;}
	public String[] getAllPlayerNames(){return name;}
	
	public void addIdentity(String user,InetAddress userip){
		boolean added=false;
		for(int i=0;i<name.length;i++){
			if(name[i].equals("")){
				//Free space - here i want to save my new identity
				name[i]=user; ip[i]=userip;
				System.out.println("Identity succesfully added to index "+i);
				added=true;
				connected++;
				break;
			}
		}
		if(!added){
			System.out.println("ERROR_01: Identity ("+user+") could not be saved, maximum of players achieved.");
		}
	}
	public void deleteIdentity(String user){
		boolean deleted=false;
		for(int i=0;i<name.length;i++){
			if(name[i].equals(user)){
				//user found! - set data == null & break loop!
				name[i]="";
				try {
					ip[i]=InetAddress.getByAddress(new byte[]{0,0,0,0});
				} catch (UnknownHostException e) {
					//This host will not be resolved.It's just /0.0.0.0
					e.printStackTrace();
				}
				System.out.println("Identity deleted.("+user+")");
				deleted=true;
				connected--;
				break;
			}
		}
		if(!deleted)
		System.out.println("ERROR_02: Identity ("+user+") not found.");
	}
	public String resolveIP(InetAddress userip){
		for(int i=0;i<ip.length;i++){
			if(ip[i].equals(userip)){
				return name[i];
			}
		}
		System.out.println("ERROR_03: IP ("+userip.toString()+") not found.");
		return null;
	}
	
	public String getPlayers(){
		String tmp=new String("");
		byte usercount=0;
		for(int i=0;i<ip.length;i++){
			if(!name[i].equals("")){
				usercount++;
				tmp+="\tPlayer: "+name[i]+" | IP: "+ip[i].toString()+"\n";
			}
		}
		return ("At this moment on the server ["+usercount+"/"+name.length+"]:\n"+tmp);
	}
	public InetAddress resolveName(String user){
		for(int i=0;i<name.length;i++){
			if(name[i].equals(user)){
				return ip[i];
			}
		}
		System.out.println("ERROR: Name ("+user+") not found.");
		return null;
	}
	public boolean contains(String user){
		for(int i=0;i<name.length;i++){
			if(name[i].equals(user))
				return true;
		}
		return false;
	}
	public boolean contains(InetAddress userip){
		for(int i=0;i<ip.length;i++){
			if(ip[i].equals(userip))
				return true;
		}
		return false;
	}

	public void kickPlayer(String user) {
		System.out.println("Server kicking player: "+user);
		deleteIdentity(user);
	}
	
	public int connectedCnt(){ return this.connected;}
}
