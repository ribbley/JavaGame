package General.Entity;

import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.geom.AffineTransform;
import java.awt.image.BufferedImage;

import General.Game;


public class Arrow extends Entity{
	
	
	private static final long serialVersionUID = 1L;
	long speed = 300;
	long lifetime=0;
	BufferedImage rotatedarrow;
	Sprite arrow;
	public Arrow(Sprite i,String id, double theta, double x, double y, Game p) {
		
		super(i, x, y, p);
		this.id=id;
		arrow = i;
		setHorizontalSpeed(Math.sin(Math.toRadians(theta))*speed);
		setVerticalSpeed(-Math.cos(Math.toRadians(theta))*speed);
		rotatedarrow = rotateImage(arrow.getImage(), theta);
		System.out.println("Fired! ("+theta+")");
		System.out.println("x:"+x+" y:"+y+" dx:"+dx+" dy:"+dy);
	}
	public void drawObjects(Graphics g) {	
		//g.drawImage(rotatedarrow,(int) (x-parent.getplayerx()+(parent.getWidth()/2-getWidth()/2)), (int) (y-parent.getplayery()+(parent.getHeight()/2-getHeight()/2)), null);
	}
	
	public void setTheta(double theta){
		this.theta = theta;
		rotatedarrow = rotateImage(arrow.getImage(), theta);
	}
	
	public void doLogic(long delta){
		super.doLogic(delta);
		/*
		 * Adds a counter so that the lifetime of an arrow is 
		 * limited to ~5 seconds (500 ticks)
		 */
		if(lifetime++>500){
			remove=true;
		}
	}
	private static BufferedImage rotateImage(BufferedImage src, double degrees) {
        AffineTransform affineTransform = AffineTransform.getRotateInstance(
                Math.toRadians(degrees),
                src.getWidth() / 2,
                src.getHeight() / 2);
        BufferedImage rotatedImage = new BufferedImage(src.getWidth(), src
                .getHeight(), BufferedImage.TYPE_INT_ARGB);
        Graphics2D g = (Graphics2D) rotatedImage.getGraphics();
        g.setTransform(affineTransform);
        g.drawImage(src, 0, 0, null);
        return rotatedImage;
    }
}