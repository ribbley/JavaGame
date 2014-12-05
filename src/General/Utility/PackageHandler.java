package General.Utility;

import General.Game;

public class PackageHandler {
	Game game;
	
	public PackageHandler(Game game){
		this.game=game;
	}
	
	public void handle(Package pack){
		if(pack instanceof Message){
			game.chat.getMessagebuffer().add(((Message) pack).getMessage());
		}
	}
}
