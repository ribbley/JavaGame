package General.Utility;
import General.Utility.*;

public interface Parent {
	//hier wird alles verein was Game Klasse und Gameserver Klasse gemein haben :)
	public EntityManager getEntityManager();
	public void send(String toWho,String msg);
	public int getTick();
	public void println(String msg);
	public void print(String msg);
	public AnimLoader getAnimLoader();
}
