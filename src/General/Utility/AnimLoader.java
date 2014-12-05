package General.Utility;

import java.awt.GraphicsConfiguration;
import java.awt.GraphicsEnvironment;
import java.awt.Image;
import java.awt.Transparency;
import java.awt.image.BufferedImage;
import java.io.IOException;
import java.net.URL;
import java.util.HashMap;
import General.Entity.Sprite;
import javax.imageio.ImageIO;


public class AnimLoader {
	/** The single instance of this class */
	private static AnimLoader single = new AnimLoader();
	private HashMap<String,Sprite> sprites;

	public Image[] chatgui;
	Image[] andi,testfig;
	Image[] arrowpic,greenblurb;
	Image[] chunks;
	/**
	 * Get the single instance of this class 
	 * 
	 * @return The single instance of this class
	 */
	public static AnimLoader get() {
		return single;
	}
	
	public void loadAll(){
		greenblurb = loadPics("enemys/greenblurbwalk.gif",4);
		chunks = loadPics("world/grass.jpg",3);
		chatgui = loadPics("UI/chatbackground.gif",1);
		andi = loadPics("players/andi.gif",1);
		arrowpic = loadPics("objects/arrow.gif",1);
		testfig = loadPics("players/TestingFig.gif",1);
		
	}
	
	public AnimLoader(){
		sprites = new HashMap<String,Sprite>();
		loadAll();
	}
	
	public Image[] loadPics(String path, int cnt){
		Image[] anim = new Image[cnt];
		BufferedImage source = null;
		BufferedImage temp;
		
		URL pic_url = getClass().getResource(path);
		if (pic_url == null) {
			fail("Can't find ref: "+path);
		}
		try{
			source = ImageIO.read(pic_url);
		}catch(IOException e){
			fail("Failed to load: "+path);
		}
		// create an accelerated image of the right size to store our sprite in
		GraphicsConfiguration gc = GraphicsEnvironment.getLocalGraphicsEnvironment().getDefaultScreenDevice().getDefaultConfiguration();
		
		for(int x=0;x<cnt;x++){
			temp = source.getSubimage(x*source.getWidth()/cnt, 0, 
					source.getWidth()/cnt, source.getHeight());
			anim[x]= gc.createCompatibleImage(temp.getWidth(), temp.getHeight(), Transparency.BITMASK);
			anim[x].getGraphics().drawImage(temp,0,0,null);

		}
		System.out.println(pic_url+" - loaded.");
		return anim;		
	}
	
	public Sprite getSprite(String ref,int cnt) {
		// if we've already got the sprite in the cache
		// then just return the existing version
		if (sprites.get(ref) != null) {
			return sprites.get(ref);
		}
		
		// otherwise, go away and grab the sprite from the resource
		// loader
		// create a sprite, add it the cache then return it
		Sprite sprite = new Sprite(loadPics(ref,cnt));
		sprites.put(ref,sprite);
		
		return sprite;
	}
	
	/**
	 * Utility method to handle resource loading failure
	 * 
	 * @param message The message to display on failure
	 */
	private void fail(String message) {
		// we're pretty dramatic here, if a resource isn't available
		// we dump the message and exit the game
		System.err.println(message);
		System.exit(0);
	}
}
