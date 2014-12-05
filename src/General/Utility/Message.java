package General.Utility;

import General.Game;
import java.io.UnsupportedEncodingException;
import java.net.UnknownHostException;


public class Message extends Package{

	String message;
	
	public Message(Game g,String message) throws UnsupportedEncodingException, UnknownHostException {
		super(g);
		this.message=message;
		this.send();
	}
	
	public String getMessage(){return this.message;}

	@Override
	public void send(){
		// #003# - tag for a message!
		//TODO add to queue instead of sending immediately
		g.getConnectI().sendPackage("003#"+tick+"#"+message);
	}

}
