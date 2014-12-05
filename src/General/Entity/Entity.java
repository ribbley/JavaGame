package General.Entity;

import General.Utility.Parent;
import General.Game;
import General.Server.Gameserver;

import java.awt.Graphics;
import java.awt.Image;
import java.awt.geom.Rectangle2D;

abstract public class Entity extends Rectangle2D.Double implements Drawable, Movable{

	
	private static final long serialVersionUID = 1L;
	public static int idcnt=0;
	
	String id;
	double hp,maxhp,theta;
	int speed;
	
	public boolean enabled=false; //eine Entity muss erst vom server freigegeben werden!
	long delay=5000;
	long animation;
	Image[] images;
	Parent parent;
	boolean remove=false,moved=false,rotated=false,hpchanged=false;
	Sprite sprite;
	protected int currentpic;
	
	protected double dx;
	protected double dy;
	
	public double getHP(){
		return hp;
	}
	public double getMaxHP(){
		return maxhp;
	}
	public void setMaxHP(double maxhp){
		this.maxhp=maxhp;
	}
	public void setHP(double hp){
		this.hp=hp;
	}
	protected void affectHP(char option,long value){
		/* Different options possible
		 * l= lose life
		 * i= increase maxlife
		 * h= (heal) get life
		 * c= (cripple) lose maxlife
		 */
		switch(option){
		case 'l': 	hp=hp-value;
					break;
		case 'i':	maxhp=maxhp+value;
					break;
		case 'h':	hp=hp+value;
					break;
		case 'c':	maxhp=maxhp-value;
					break;
		}
		hpchanged=true;
	}
	public double getTheta(){
		return theta;
	}
	public void setTheta(double theta){
		this.theta = theta;
	}
	public double getX(){
		return x;
	}
	public double getY(){
		return y;
	}
	public void setX(double x){
		this.x=x;
	}
	public void setY(double y){
		this.y=y;
	}
	public int getSpeed(){ return this.speed;}
	public void setSpeed(int amount){ this.speed=amount;}
	
	public double getHorizontalSpeed(){
		return dx;
	}
	public void setHorizontalSpeed(double dx){
		this.dx = dx;
	}
	public double getVerticalSpeed(){
		return dy;
	}
	public void setVerticalSpeed(double dy){
		this.dy=dy;
	}
	
	public Entity(Sprite sprite, double x, double y,Parent p){
		this.sprite = sprite;
		this.id=""+idcnt++;
		height=sprite.getHeight();
		width=sprite.getWidth();
		this.images=this.sprite.getImages();
		this.x = x;
		this.y = y;
		this.speed=0;
		this.currentpic=0;
		if(p instanceof Game)
		parent = (Game) p;
		else
		this.enabled=true;
	}
	@Override
	public void doLogic(long delta) {
		animation++;
		if(animation>(delay*delta/1e9)){
			currentpic++;
			animation=0;
			if(currentpic>=images.length)
					currentpic=0;
		
		}
	}

	@Override
	public void move(long delta) {
		if(enabled){
		if(dx!=0){
			x += dx*(delta/1e9);
		}
		
		if(dy!=0){
			y += dy*(delta/1e9);
		}
		}
	}
	
	
	/* Diese Methode wird nur von GameClient aufgerufen.*/
	@Override
	public void drawObjects(Graphics g) {
		long xfix= (long) (((Game)parent).playerx-((Game)parent).getWidth()/2+width/2);
		long yfix= (long) (((Game)parent).playery-((Game)parent).getHeight()/2+height/2);
		g.drawImage(images[currentpic],(int)(x-xfix),(int)(y-yfix),null);
		
		if(((Game)parent).entity_info_on){
			g.drawString(id,(int)(x-xfix),(int)(y+height-yfix));
			g.drawString("X:"+(int)x+"|Y:"+(int)y,(int)(x-xfix),(int)(y+height+11-yfix));
		
		}
	}
	public void setRemove(boolean b) {
		this.remove = b;
	}
	public String getID() {
		return this.id;
	}
	public boolean getRemove() {
		return this.remove;
	}
	public void update(String cmd) {
		// TODO Complete all possibilities.
		
	}
	public void setMoved(boolean b) {
		this.moved=b;
	}
	public String getFullStatus(){
		// most important step of all! Break lines of Status by '\n' like in Strings
		
		return getMovementStatus()+'\n'+getHPStatus()+'\n'+getEnabled();	
	}
	public String getHPStatus(){
		return "011#"+parent.getTick()+'#'+this.id+'#'+hp+'#'+maxhp+'#';
	}
	public String getMovementStatus(){
		//cast all double values to int but multiply them by 100 beforehand.
		return "010#"+parent.getTick()+'#'+this.id+'#'+(long)(x*100)+'#'+(long)(y*100)+'#'+(int)(dx*100)+'#'+(int)(dy*100)+'#'+(int)theta;
	}
	public String getEnabled(){ return "012#"+parent.getTick()+'#'+this.id+'#'+this.enabled;}
	

}
