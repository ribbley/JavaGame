package General.Entity;

import java.awt.Color;
import java.awt.Graphics;
import java.awt.Image;
import java.awt.image.BufferedImage;

import General.Game;
import General.Utility.Parent;


public class Greenblurb extends Entity{
	
	private static final long serialVersionUID = 1L;
	int speed = 30;
	long lastchange=0;
	long randomedtime = (long) (Math.random()*300);
	double x,y;
	
	
	public Greenblurb(Sprite i, double x, double y, Parent p) {
		super(i, x, y, p);
		delay=500;
		currentpic=(int) (Math.random()*images.length);
	}
	
	public double getX(){
		return x;
	}
	public double getY(){
		return y;
	}
	
	public void doLogic(long delta){
		super.doLogic(delta);
		super.move(delta);
		lastchange+=1;
		if(lastchange>randomedtime+200){ // NACH WIEVIELEN TICKS RICHTUNGSWECHSEL?
			lastchange=0;
			randomedtime = (long) (Math.random()*500);
			if((int)(Math.random()*2)==0){
				setVerticalSpeed(0);
				setHorizontalSpeed(0);
			}
			
		switch((int) (Math.random()*4)){
		case 0: setVerticalSpeed(-speed);
				break;
		case 1: setHorizontalSpeed(speed);
				break;
		case 2: setVerticalSpeed(speed);
				break;
		case 3: setHorizontalSpeed(-speed);
		}
		}
		
	}
	public void drawObjects(Graphics g){
		super.drawObjects(g);
	}
}
