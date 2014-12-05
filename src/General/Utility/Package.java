package General.Utility;

import General.Game;

import java.io.UnsupportedEncodingException;
import java.net.UnknownHostException;




public abstract class Package{

	int tick;
	Game g;
	
	public Package(Game g){
		tick=g.getTick();
		this.g=g;
	}
	
	public abstract void send() throws UnsupportedEncodingException, UnknownHostException;
		//TODO sendmethod - give a bytearray to ConnectI so it can be send via sendPack - or handle both things!
	
}
