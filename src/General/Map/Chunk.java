package General.Map;

import General.Game;
import General.Utility.AnimLoader;

import java.awt.Graphics;
import java.awt.Image;


public class Chunk {
	private int[][] field;
	long x,y;
	Game parent;
	int size;
	//(X|Y) Coordinates from upper left corner of chunk
	Image[] pics;
			
	public Chunk(AnimLoader al,long x, long y, Game p,int size){
		this.size=size;
		field = new int[size][size];
		pics=al.loadPics("world/grass.jpg", 3);
		this.x=x;
		this.y=y;
		this.parent= p;
		//StandardInitialising
		for(int i=0;i<size;i++){
			for(int j=0;j<size;j++){
				field[i][j]=(int)(Math.random()*3);
			}
		}
		
	}
	
	public void drawChunk(Graphics g){
		for(int i=0;i<size;i++){
			for(int j=0;j<size;j++){
				g.drawImage(pics[field[i][j]],(int) ((50*i)-parent.getplayerx()),(int) ((50*j)-parent.getplayery()),null);
			}
		}
	}
}
