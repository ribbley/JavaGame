package General.Utility;

import General.Game;

import java.io.UnsupportedEncodingException;
import java.net.UnknownHostException;


public class PositionPack extends Package{
	
	double x,y,dx,dy,theta;

	public PositionPack(Game g,double x, double y, double dx, double dy, double theta) throws UnsupportedEncodingException, UnknownHostException{
		super(g);
		this.x=x;
		this.y=y;
		this.dx=dx;
		this.dy=dy;
		this.theta=theta;
		this.send();
	}



	@Override
	public void send(){
		// 010 - tag for position update!
			g.getConnectI().sendPackage("010#"+tick+"#"+x+"#"+y+"#"+dx+"#"+dy+"#"+theta);
		}
	}
