package General.Entity;

import General.Game;
import General.Utility.*;
import java.awt.Color;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;
import java.awt.event.MouseMotionListener;
import java.awt.geom.AffineTransform;
import java.awt.image.BufferedImage;
import java.io.UnsupportedEncodingException;
import java.net.UnknownHostException;


public class Me 
extends Entity implements MouseMotionListener, MouseListener{

	private static final long serialVersionUID = 1L;
	Game p;
	double olddx=dx,olddy=dy;
	
	BufferedImage player,player2;
	BufferedImage rotatedplayer;
	Graphics2D playergraphics;
	
	Vektor standartsight;
	Vektor oldplayersight;
	Vektor playersight;
	
	double xsight,ysight;
	double theta=0;
	
	//playerstats
	int lvl; long exp;
	double hp, maxhp,hpreg,mana,maxmana,manareg;
	
	
	public Me(Sprite sprite, double x, double y, Game p,String usernickname) {
		super(sprite, x, y, p);
		this.p=p;
		speed = 250;
		hpreg=1.5; manareg=0.5;
		hp=100; mana=50;
		maxhp=300; maxmana=175;
		playersight=new Vektor(0,0);
		standartsight=new Vektor(0,-10);
		id = usernickname;
		p.getFrame().addMouseMotionListener(this);
		p.getFrame().addMouseListener(this);
		oldplayersight =new Vektor(0,1);//(p.getWidth()/2)+2,(p.getHeight()/2)+8+1);
		player = sprite.getImage();
		player2=player;
		enabled=true;
	}
	
	public double getHP(){
		return hp;
	}
	
	public double getMaxHP(){
		return maxhp;
	}
	public double getMaxMana(){ return maxmana;}
	public double getMana(){ return mana;}
	
	public void doLogic(long delta){
		super.doLogic(delta);
		//hpreg
		if(hp<maxhp)
			hp+=(hpreg/p.getFPS());
		else hp=maxhp;
		//manareg
		if(mana<maxmana)
			mana+=(manareg/p.getFPS());
		else mana=maxmana;
		
		//newdirection
		if(!(olddy==dy && olddx==dx)){
			moved=true;
			olddy= dy; olddx = dx;
		}
		
		//ROTATE PLAYER IF THETA != 0
		if(moved){
			playersight=new Vektor(xsight,ysight);
			
			if(playersight.getX()==0)
				playersight.setX(1);
			if(playersight.getY()==0)
				playersight.setY(1);
			//Berechnet Winkel zwischen Maus und einem Standardvektor(0,1)
			theta = Math.acos(
                    ((standartsight.getX() * playersight.getX()) + (standartsight.getY() * playersight.getY()))
                                              /
                    ((Math.sqrt((standartsight.getX() * standartsight.getX()) + (standartsight.getY() * standartsight.getY())))
                                              *
                    (Math.sqrt((playersight.getX() * playersight.getX()) + (playersight.getY() * playersight.getY()))))
                   );
			
			if(playersight.getX()<0){
				theta = Math.toRadians(360) - theta;
			}
			theta= Math.toDegrees(theta);
			rotatedplayer = rotateImage(player,theta);
			player2 = rotatedplayer;
			rotated=true;
		}else
			rotatedplayer = player2;
		
		//send new x,y,dx,dy,theta!
		try {
			new PositionPack(p,x,y,dx,dy,theta);
		} catch (UnsupportedEncodingException e) {
			e.printStackTrace();
		} catch (UnknownHostException e) {
			e.printStackTrace();
		}
	}
	//Wird nur von GameClient , nicht von server ausgeführt.
	public void drawObjects(Graphics g) {
		
		//Player kriegt fixe x,y werte zum anzeigen!		
		g.drawImage(rotatedplayer, (int) (p.getWidth()/2-getWidth()/2), (int) (p.getHeight()/2-getHeight()/2), null);
		
		g.setColor(Color.black);
		g.drawString(id, (int) (p.getWidth()/2-getWidth()/2+(50-5*id.length())), (int)(p.getHeight()/2-getHeight()/2-10));
		//HP Anzeigen
		g.setColor(Color.black);
		g.drawRect((p.getWidth()/2-100), p.getHeight()-50, 200, 20);
		g.setColor(Color.green);
		g.fillRect(p.getWidth()/2-99, p.getHeight()-49,(int)(hp/maxhp*199), 19);
		g.setColor(Color.black);
		g.drawString((int)hp+"/"+(int)maxhp, p.getWidth()/2-p.getFontMetrics(p.getFont()).stringWidth((int)hp+"/"+(int)maxhp)/2,p.getHeight()-37);
		g.drawString("+"+hpreg, p.getWidth()/2+65, p.getHeight()-37);
		
		//Mana Anzeigen
		g.setColor(Color.black);
		g.drawRect((p.getWidth()/2-120), p.getHeight()-30, 240, 16);
		g.setColor(new Color(0,173,255));
		g.fillRect(p.getWidth()/2-119,p.getHeight()-29,(int)(mana/maxmana*239),15);
		g.setColor(Color.black);
		g.drawString((int)mana+"/"+(int)maxmana,p.getWidth()/2-p.getFontMetrics(p.getFont()).stringWidth((int)mana+"/"+(int)maxmana)/2,p.getHeight()-17);
		g.drawString("+"+manareg, p.getWidth()/2+85, p.getHeight()-17);
		
		//entity info
		if(((Game)parent).entity_info_on){
			g.drawString(id,(int) (p.getWidth()/2-getWidth()/2),(int)(p.getHeight()/2+getHeight()/2));
			g.drawString("X:"+(int)x+"|Y:"+(int)y,(int)(p.getWidth()/2-getWidth()/2),(int)(p.getHeight()/2+getHeight()/2+11));
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

	@Override
	public void mouseDragged(MouseEvent arg0) {		
	}

	@Override
	public void mouseMoved(MouseEvent arg0) {
		moved = true;
		xsight = arg0.getX()-(p.getWidth()/2);//+5;
		ysight = arg0.getY()-(p.getHeight()/2);//+18;
	}
	@Override
	public void mouseClicked(MouseEvent arg0) {
		// TODO Auto-generated method stub
		
	}
	@Override
	public void mouseEntered(MouseEvent arg0) {
		// TODO Auto-generated method stub
		
	}
	@Override
	public void mouseExited(MouseEvent arg0) {
		// TODO Auto-generated method stub
		
	}
	@Override
	public void mousePressed(MouseEvent arg0) {
		xsight = arg0.getX()-(p.getWidth()/2);
		ysight = arg0.getY()-(p.getHeight()/2);
		lmbEvent();
	}
	private void lmbEvent() {
		theta = Math.acos(
                ((standartsight.getX() * playersight.getX()) + (standartsight.getY() * playersight.getY()))
                                          /
                ((Math.sqrt((standartsight.getX() * standartsight.getX()) + (standartsight.getY() * standartsight.getY())))
                                          *
                (Math.sqrt((playersight.getX() * playersight.getX()) + (playersight.getY() * playersight.getY()))))
               );
		if(playersight.getX()<0){
			theta = Math.toRadians(360) - theta;
		}
		theta= Math.toDegrees(theta);
		// TODO erstmal entfernen: 
	}
	@Override
	public void mouseReleased(MouseEvent arg0) {
		// TODO Auto-generated method stub
		
	}
}
