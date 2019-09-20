import java.awt.Rectangle;

public class MovingBlock extends Block
{
	
	private int initX;
	private int initY;
	private int distanceThresholdX;
	private int distanceThresholdY;
	private int width;
	private int height;
	private Rectangle hitbox;
	private boolean left;
	private boolean right;
	private boolean up;
	private boolean down;
	
	public MovingBlock(int x,int y)
	{
		super(x,y);
		initX = x;
		initY = y;
		width = 75;
		height = 75;
		right = false;
		left = false;
		up = false;
		down = false;
		hitbox = new Rectangle(x*width,y*height,width,height);
		distanceThresholdX = initX+(5*width);
		distanceThresholdY = initY+(5*height);
	}
	
	public int getInitX(){return initX;}
	
	public int getInitY() {return initY;}
	
	public void setInitX(int x)
	{
		initX+=x;
		distanceThresholdX = initX+(5*width);
	}
	
	public int getDistanceThresholdX(){return distanceThresholdX;}
	
	public int getDistanceThresholdY() {return distanceThresholdY;}
	
	public boolean getLeft(){return left;}
	
	public boolean getRight(){return right;}
	
	public boolean getUp(){return up;}
	
	public boolean getDown() {return down;}
	
	public void setLeft(boolean b){left = b;}
	
	public void setRight(boolean b){right = b;}
	
	public void setUp(boolean b){up = b;}
	
	public void setDown(boolean b){down = b;}
}
