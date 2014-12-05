package General;

import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;
import java.io.UnsupportedEncodingException;
import java.net.UnknownHostException;


public class WindowClosingAdapter 
extends WindowAdapter
{
	Game g=null;
	private boolean exitSystem;
	
	/**
	 * Erzeugt einen Window Closing Adapter zum Schliessen
	 * des Fensters. Ist exitSystem true, wird das komplette
	 * Programm beendet.
	 */
	public WindowClosingAdapter(boolean exitSystem)
	{
		this.exitSystem = exitSystem;
	}
	
	/*
	 * Erzeugt einen WindowClosingAdapter zum Schliessen
	 * des Fensters. Das Programm wird nicht beendet.
	 */
	
	public WindowClosingAdapter(Game g)
	{
		this(false);
		this.g=g;
	}
	
	@Override
	public void windowClosing(WindowEvent event)
	{
		//TODO was ist nötig, und was nicht?
		event.getWindow().setVisible(false);
		event.getWindow().dispose();
		if(g!=null){
		System.out.println("Disconnecting...");
		g.coni.disconnect();
		System.exit(0);
			
		try {
			Thread.sleep(200);
		} catch (InterruptedException e) {
			System.out.println("Interrupted Thread while closing window ...");
			e.printStackTrace();
		}
			
		System.exit(0);
	}
		
	}
}
