import javax.swing.*;
import java.awt.*;
import java.awt.event.*;
import java.io.*;
import java.lang.*;
import java.math.*;
import java.awt.image.*;
import java.applet.*;
import javax.swing.border.*;
import javax.imageio.ImageIO;
import java.util.*;

public class Block {

	private int x;
	private int y;
	private int width;
	private int height;
	private Rectangle hitbox;

	public Block(int x,int y){
		this.x = x;
		this.y = y;
		width = 75;
		height = 75;
		hitbox = new Rectangle(x*width,y*height,width,height);
	}

	public int getX(){return x;}

	public int getY(){return y;}
	
	public int getWidth() {return width;}
	
	public int getHeight() {return height;}

	public void setX(boolean dir){
		if(dir)
			this.x++;
		else
			this.x--;
	}
	
	public void setY(boolean dir){
		if(dir)
			this.y++;
		else
			this.y--;
	}

	public Rectangle getHitbox(){return hitbox;}

	public void setHitbox(int newX, int newY){	hitbox = new Rectangle(newX,newY,width,height);}

	public String toString(){return "x: "+x+" | y: "+y;}

}