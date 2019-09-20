import java.awt.Rectangle;

public class Bullet {

	private int x;
	private int y;
	private int width;
	private int height;
	private Rectangle hitbox;
	private boolean dir;
	
	public Bullet(int x,int y,boolean dir)
	{
		this.x = x;
		this.y = y;
		width = 10;
		height = 10;
		this.dir = dir;
		hitbox = new Rectangle(x*width,y*height,width,height);
	}
	
	public boolean getDir()
	{
		return dir;
	}
	
	public int getWidth()
	{
		return width;
	}
	
	public int getHeight()
	{
		return height;
	}
	
	public int getX()
	{
		return x;
	}
	
	public void setX(int b)
	{
		x+=b;
	}
	
	public int getY()
	{
		return y;
	}
	
	public Rectangle getHitbox()
	{
		return hitbox;
	}
	
	public void setHitbox(int newX,int newY)
	{
		hitbox = new Rectangle(newX,newY,width,height);
	}
	
}
