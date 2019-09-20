import java.awt.Rectangle;

public class Hero {

	private int x;
	private int y;
	private Rectangle hitbox;
	private int width;
	private int height;
	
	public Hero(int x,int y) {
		this.x = x;
		this.y = y;
		width = 65;
		height = 75;
		hitbox = new Rectangle(x,y,width/2,height);
	}
	
	public int getX() {
		return x;
	}
	
	public int getY() {
		return y;
	}
	
	public int getWidth() {return width;}
	
	public int getHeight() {return height;}
	
	public void setY(int newY) {
		y += newY;
	}
	
	public Rectangle getHitbox() {
		return hitbox;
	}
	
	public void setHitbox(int newX, int newY){	hitbox = new Rectangle(newX,newY,width/2,height);}
	
}
