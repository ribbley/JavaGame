package General.Entity;

import java.awt.image.BufferedImage;
import java.io.IOException;
import java.net.URL;
import java.util.Enumeration;
import java.util.Vector;

import javax.imageio.ImageIO;

import General.Utility.Point;
import General.Game;


public class GroupGreenblurb{
	
	Vector<Greenblurb> moblist;
	Enumeration<Point> mobpoints;
	Point[] listpoints;
	int speed = 30;
	double aggrorange = 50;
	int count;
	
	/**
	 * @param xpos x-Position
	 * @param ypos y-Position
	 */
	public GroupGreenblurb(Vector<Entity> painter,int count,double xpos,double ypos,double width,double height, Game game){
		this.count = count;
		double[] xarray = new double[count];
		double[] yarray = new double[count];
		
		BufferedImage[] animation = loadPics("enemys/greenblurbwalk.gif",4);
		createRandomArray(xarray,yarray,width,height);
		Vector<Greenblurb> moblist = new Vector<Greenblurb>();
		
		
		
		for(int i = 0;i<count;i++){
			double x = xarray[i];
			double y = yarray[i];
			Greenblurb monster = new Greenblurb(game.getAnimLoader().getSprite("enemys/greenblurbwalk.gif", 4),xpos+x,ypos+y,game);
			moblist.addElement(monster);
			painter.add(monster);
		}
	}
	private void createRandomArray(double[] x,double[] y,double width,double height){
		for(int i = 0;i<x.length;i++){
			x[i] = (double) (Math.random()*width);	
			y[i] = (double) (Math.random()*height);
			System.out.print("x: "+x[i]+"\ny: "+y[i]+"\n");
			
		}
	}
	private BufferedImage[] loadPics(String path, int cnt){
		BufferedImage[] anim = new BufferedImage[cnt];
		BufferedImage source = null;
		
		URL pic_url = getClass().getClassLoader().getResource(path);
		System.out.println(path);
		System.out.println(pic_url);
		try{
			source = ImageIO.read(pic_url);
		}catch(IOException e){
			System.out.println(e.toString());
		}
		
		for(int x=0;x<cnt;x++){
			anim[x]=source.getSubimage(x*source.getWidth()/cnt, 0, 
					source.getWidth()/cnt, source.getHeight());
		}
		return anim;		
	}
}

