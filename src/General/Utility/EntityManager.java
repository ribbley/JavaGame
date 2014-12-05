package General.Utility;

import java.awt.Graphics;
import java.util.ListIterator;
import java.util.Vector;

import General.Game;
import General.Entity.Entity;
import General.Server.Gameserver;
import General.Map.Chunk;


public class EntityManager {
	Parent parent=null;
	long entitycounter;
	/* TODO : ADD BINARY TREE */
	private Vector<Entity> entityvector,vectorclone;
	
	public EntityManager(Parent g){
		this.parent=g;
		entityvector = new Vector<Entity>();
	}
	public Parent getParent(){
			return parent;
	}
	public boolean isServer(){ return (parent instanceof Gameserver);}
	public boolean isClient(){ return (parent instanceof Game);}
	
	public Vector<Entity> getEntityVector(){ return this.entityvector;}
	public void add(Entity entity){
		entityvector.add(entity);
	}
	public void delete(String id){
		for(ListIterator<Entity> it = vectorclone.listIterator();it.hasNext();){
			Entity r = it.next();
			if(r.getID().equals(id)){
				r.setRemove(true);
			}
		}
	}
	public void setEnabled(String id,boolean value){
		for(Entity r: vectorclone){
			if(r.getID().equals(id)){
				r.enabled=value;
			}
		}
	}
	public String getFullStatus(String id){
		for(Entity r: vectorclone){
			if(r.getID().equals(id)){
				return r.getFullStatus();
			}
		}
		return "009#"+parent.getTick()+'#'+id; //invalid syntax/request
	}
	public void update(String id,String cmd){
		for(ListIterator<Entity> it = vectorclone.listIterator();it.hasNext();){
			Entity r = it.next();
			if(r.getID().equals(id)){
				r.update(cmd);
			}
		}
	}
	public void doLogic(long delta){
		for(ListIterator<Entity> it = vectorclone.listIterator(); it.hasNext();){
		Entity r = it.next();
		if(r.getRemove()){
			it.remove();continue;
		}
		if(r.enabled){
		r.doLogic(delta);
		}else{
			//parent.println(r.getID()+ " is not enabled!");
		//request all information from server - object not enabled!
		//parent.send("SERVER","004#"+parent.getTick()+'#'+id);
		}
		}
		if(getParent() instanceof Game){
		((Game)parent).playerx = ((Game)parent).getMe().x;
		((Game)parent).playery = ((Game)parent).getMe().y;
		}
		
	}
	
	public void moveObjects(long delta) {
		for(Entity r: entityvector){
			if(r.enabled)
			r.move(delta);
			
		}
		
	}
	public void drawChunks(Graphics g,Vector<Chunk> chunkvector){
		for(Chunk e: chunkvector){
					e.drawChunk(g);
				}
	}
	public void drawObjects(Graphics g){
		for(Entity e: vectorclone){
			if(e.enabled) e.drawObjects(g);
		}
	}
	
	@SuppressWarnings("unchecked")
	public void cloneVectors(){
		if(entityvector!=null)
		vectorclone=(Vector<Entity>) entityvector.clone();
		else
		vectorclone=null;
	}
}
