package General.Entity;

import java.awt.Color;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.event.MouseEvent;
import java.awt.event.MouseMotionListener;
import java.awt.geom.AffineTransform;
import java.awt.image.BufferedImage;

import General.Utility.Parent;
import General.Utility.AnimLoader;
import General.Utility.Vektor;

import General.Game;


public class Player 
extends Entity{

	private static final long serialVersionUID = 1L;
	private final static String imagepath="players/TestingFig.gif";
	int speed = 60;
	
	BufferedImage player,player2;
	BufferedImage rotatedplayer;
	Graphics2D playergraphics;
	
	Vektor standartsight = new Vektor(0,-10);
	Vektor oldplayersight;
	Vektor playersight;
	Sprite i;
	int movedir;
	
	public Player(AnimLoader animloader,String idname, double x, double y, Parent p) {
		super(animloader.getSprite(imagepath, 1), x, y, p);
		i=animloader.getSprite(imagepath,1);
		id = idname;
		player = i.getImage();
		player2=player;
		hp=100;
		maxhp=300;

	}
	
	public void doLogic(long delta){
		super.doLogic(delta);
		
		/*CALCULATE MOVEDIR!		Example:	8	1	2
												7	0	3		0==staying in playce
												6	5	4		*/

			
			if(getVerticalSpeed()>0){
				if(getHorizontalSpeed()>0)
					movedir=4;
				else if(getHorizontalSpeed()<0)
					movedir=6;
				else movedir=5;
			} else if(getVerticalSpeed()<0){
				if(getHorizontalSpeed()<0){
					movedir=8;
				}else if(getHorizontalSpeed()>0)
					movedir=2;
				else movedir=1;
			}else if(getVerticalSpeed()==0){
				if(getHorizontalSpeed()>0)
					movedir=3;
				else if(getHorizontalSpeed()<0)
					movedir=7;
				else movedir=0;
			}
			else movedir=0;
			
			
		//ROTATE PLAYER IF THETA != 0
		if(rotated){
			rotatedplayer = rotateImage(player,theta);
			player2 = rotatedplayer;
			
		}else
			rotatedplayer = player2;
		
	}
	public void drawObjects(Graphics g) {
		Game parent = (Game) (this.parent);
		//Player kriegt fixe x,y werte zum anzeigen!		
		g.drawImage(rotatedplayer, (int) (x-parent.getplayerx()+(parent.getWidth()/2-getWidth()/2)), (int) (y-parent.getplayery()+(parent.getHeight()/2-getHeight()/2)), null);
		// player= rotatedplayer;
		g.setColor(Color.black);
		g.drawString(id, (int) (x-parent.getplayerx()+parent.getWidth()/2-18), (int)(y-parent.getplayery()+parent.getHeight()/2-40));
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
