import java.awt.Rectangle;

public class Enemy {

	private int x;
	private int y;
	private int width;
	private int height;
	private Rectangle hitbox;
	private int hitCounter;
	private int hitChangeCounter;
	private boolean isHit;
	
	public Enemy(int x,int y)
	{
		this.x = x;
		this.y = y;
		width = 75;
		height = 75;
		hitCounter = 0;
		isHit = false;
		hitChangeCounter = 0;
		hitbox = new Rectangle(x*width,y*height,width,height);
	}
	
	public int getX()
	{
		return x;
	}
	
	public int getHitChangeCounter()
	{
		return hitChangeCounter;
	}
	
	public void setHitChangeCounter(boolean b)
	{
		if(b) {
			hitChangeCounter++;
		}else {
			hitChangeCounter = 0;
		}
	}
	
	public boolean isHit()
	{
		return isHit;
	}
	
	public void setIsHit(boolean b)
	{
		isHit = b;
	}
	
	public void setHitCounter()
	{
		hitCounter++;
	}
	
	public int getHitCounter()
	{
		return hitCounter;
	}
	
	public int getY()
	{
		return y;
	}
	
	public int getWidth()
	{
		return width;
	}
	
	public int getHeight()
	{
		return height;
	}
	
	public Rectangle getHitbox()
	{
		return hitbox;
	}
	
	public void setHitbox(int newX,int newY)
	{
		hitbox = new Rectangle(newX,newY,width,height);
	}
	
	public void setX(int b)
	{
		x+=b;
	}
	
}
